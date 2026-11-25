package pruebas;

import estructuras.ArbolBinarioBusqueda;
import estructuras.Cola;
import estructuras.ListaEnlazada;
import estructuras.Nodo;
import estructuras.Ordenamiento;
import estructuras.Pila;
import estructuras.TablaHash;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import modelo.Expediente;
import modelo.FichaConsulta;
import modelo.MovimientoLegal;
import modelo.SolicitudConsulta;
import modelo.TipoMovimiento;
import persistencia.DatosIniciales;
import persistencia.ServicioPersistencia;
import servicio.CriteriosOrden;
import servicio.GestorSolicitudes;
import servicio.SistemaEstudio;

/**
 * Pruebas de funcionamiento del sistema (se ejecuta con Shift + F6 en NetBeans).
 * Verifica el orden de atención (cola FIFO), la integridad del historial
 * (pila LIFO) al guardar, cargar y restaurar los datos, y el resto de
 * estructuras: árbol binario de búsqueda, tabla hash, lista enlazada y merge sort.
 * Muestra PASÓ / FALLÓ por cada verificación y un resumen al final.
 */
public class PruebasSistema {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== PRUEBAS DEL SISTEMA - ESTUDIO JURIDICO M&M ===\n");
        pruebaOrdenDeAtencion();
        pruebaHistorialPila();
        pruebaGuardarYCargar();
        pruebaRestaurarDatos();
        pruebaArbolBinario();
        pruebaTablaHash();
        pruebaListaYOrdenamiento();
        pruebaConflictoDeInteres();
        System.out.println("\n=== RESUMEN: " + pasaron + " verificaciones pasaron, "
                + fallaron + " fallaron ===");
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            pasaron++;
            System.out.println("  [PASO]  " + descripcion);
        } else {
            fallaron++;
            System.out.println("  [FALLO] " + descripcion);
        }
    }

    // ---------------------------------------------------------------- P1
    /** P1: las solicitudes se atienden en el mismo orden en que se registran (FIFO). */
    private static void pruebaOrdenDeAtencion() {
        System.out.println("P1. Orden de atencion de solicitudes (Cola FIFO)");
        GestorSolicitudes g = new GestorSolicitudes();
        String[] nombres = {"Ana Torres", "Luis Rojas", "Carla Diaz", "Pedro Soto"};
        String[] codigos = new String[nombres.length];
        for (int i = 0; i < nombres.length; i++) {
            codigos[i] = g.registrarSolicitud(nombres[i], "4000000" + i, "900000000",
                    "Consulta " + i, "Civil", LocalDateTime.of(2026, 11, 2, 9, i)).getCodigo();
        }
        verificar("Se registraron 4 solicitudes pendientes", g.cantidadPendientes() == 4);
        verificar("verSiguiente() muestra la primera registrada sin retirarla",
                g.verSiguiente().getCodigo().equals(codigos[0]) && g.cantidadPendientes() == 4);
        boolean ordenCorrecto = true;
        for (String esperado : codigos) {
            SolicitudConsulta atendida = g.atenderSiguiente();
            ordenCorrecto &= atendida != null && atendida.getCodigo().equals(esperado);
        }
        verificar("atenderSiguiente() devuelve SOL en el orden de llegada: "
                + codigos[0] + ", " + codigos[1] + ", " + codigos[2] + ", " + codigos[3], ordenCorrecto);
        verificar("Con la cola vacia, atenderSiguiente() devuelve null", g.atenderSiguiente() == null);
    }

    // ---------------------------------------------------------------- P2
    /** P2: la cima de la pila es siempre el movimiento más reciente; anular no borra datos. */
    private static void pruebaHistorialPila() {
        System.out.println("\nP2. Historial del expediente (Pila LIFO)");
        Expediente e = DatosIniciales.crearSistema().getGestorExpedientes().buscarPorCodigo("EXP-2026-0001");
        int antes = e.getHistorial().cantidadElementos();
        e.registrarMovimiento(LocalDate.of(2026, 9, 1), TipoMovimiento.ESCRITO, "Alegatos finales", "ESC-77");
        verificar("Un movimiento nuevo queda en la cima (ultimo movimiento)",
                e.obtenerUltimoMovimiento().getDescripcion().equals("Alegatos finales"));
        e.registrarMovimiento(LocalDate.of(2026, 4, 1), TipoMovimiento.ESCRITO, "Escrito presentado tarde", "");
        verificar("Un movimiento con fecha anterior NO cambia la cima (pila auxiliar)",
                e.obtenerUltimoMovimiento().getDescripcion().equals("Alegatos finales"));
        verificar("El historial queda en orden cronologico (de la cima a la base)", historialOrdenado(e));
        MovimientoLegal anulado = e.anularUltimoMovimiento();
        verificar("Anular el ultimo lo mueve a la pila de anulados y lo marca ANULADO",
                anulado.isAnulado() && e.getAnulados().peek() == anulado);
        verificar("Ningun movimiento se pierde: vigentes + anulados = total registrado",
                e.getHistorial().cantidadElementos() + e.getAnulados().cantidadElementos() == antes + 2);
    }

    // ---------------------------------------------------------------- P3
    /** P3: al guardar y volver a cargar, la cola y los historiales quedan idénticos. */
    private static void pruebaGuardarYCargar() throws Exception {
        System.out.println("\nP3. Integridad al guardar y cargar el archivo de datos");
        File archivo = File.createTempFile("prueba_mm", ".dat");
        archivo.deleteOnExit();
        ServicioPersistencia persistencia = new ServicioPersistencia(archivo.getAbsolutePath());
        SistemaEstudio original = DatosIniciales.crearSistema();
        Expediente e2 = original.getGestorExpedientes().buscarPorCodigo("EXP-2026-0002");
        e2.registrarMovimiento(LocalDate.of(2026, 10, 30), TipoMovimiento.AUDIENCIA, "Audiencia de prueba", "");
        verificar("guardarTodo() escribe el archivo", persistencia.guardarTodo(original));
        SistemaEstudio cargado = persistencia.cargarTodo();
        verificar("La cola de solicitudes conserva el mismo orden",
                textoCola(original.getGestorSolicitudes().listarPendientes())
                        .equals(textoCola(cargado.getGestorSolicitudes().listarPendientes())));
        verificar("Los historiales (vigentes y anulados) de los expedientes son identicos",
                historialesIguales(original, cargado));
        Expediente c2 = cargado.getGestorExpedientes().buscarPorCodigo("EXP-2026-0002");
        verificar("El ultimo movimiento registrado antes de guardar sigue en la cima",
                c2.obtenerUltimoMovimiento().getDescripcion().equals("Audiencia de prueba"));
        verificar("Los arboles conservan la misma cantidad de expedientes y clientes",
                cargado.getGestorExpedientes().getArbolPorCodigo().cantidadElementos()
                        == original.getGestorExpedientes().getArbolPorCodigo().cantidadElementos()
                && cargado.getGestorExpedientes().getArbolPorCliente().cantidadElementos()
                        == original.getGestorExpedientes().getArbolPorCliente().cantidadElementos());
    }

    // ---------------------------------------------------------------- P4
    /** P4: restaurar los datos iniciales deja el sistema en su estado de partida. */
    private static void pruebaRestaurarDatos() throws Exception {
        System.out.println("\nP4. Restaurar datos iniciales");
        File archivo = File.createTempFile("prueba_mm_rest", ".dat");
        archivo.deleteOnExit();
        ServicioPersistencia persistencia = new ServicioPersistencia(archivo.getAbsolutePath());
        SistemaEstudio modificado = DatosIniciales.crearSistema();
        modificado.getGestorSolicitudes().atenderSiguiente();
        modificado.getGestorExpedientes().buscarPorCodigo("EXP-2026-0001").anularUltimoMovimiento();
        persistencia.guardarTodo(modificado);
        SistemaEstudio restaurado = persistencia.restaurarDatosIniciales();
        SistemaEstudio recargado = persistencia.cargarTodo();
        verificar("Vuelven las 5 solicitudes pendientes, la primera es SOL-0009",
                restaurado.getGestorSolicitudes().cantidadPendientes() == 5
                && restaurado.getGestorSolicitudes().verSiguiente().getCodigo().equals("SOL-0009"));
        Expediente e1 = restaurado.getGestorExpedientes().buscarPorCodigo("EXP-2026-0001");
        verificar("EXP-2026-0001 recupera sus 6 movimientos y ninguno anulado",
                e1.getHistorial().cantidadElementos() == 6 && e1.getAnulados().estaVacia());
        verificar("El archivo restaurado se vuelve a cargar con los mismos historiales",
                historialesIguales(restaurado, recargado));
    }

    // ---------------------------------------------------------------- P5
    private static void pruebaArbolBinario() {
        System.out.println("\nP5. Arbol binario de busqueda");
        ArbolBinarioBusqueda<Integer, String> arbol = new ArbolBinarioBusqueda<>();
        int[] valores = {15, 6, 20, 3, 9, 18, 24, 1, 4, 7, 12, 17};
        for (int v : valores) {
            arbol.agregar(v, "v" + v);
        }
        arbol.agregar(15, "repetido");
        verificar("Agrega 12 claves e ignora la clave repetida", arbol.cantidadElementos() == 12);
        verificar("buscar(9) encuentra el valor y buscar(99) devuelve null",
                "v9".equals(arbol.buscar(9)) && arbol.buscar(99) == null);
        verificar("El recorrido en orden devuelve las claves ordenadas",
                textoCola(arbol.mostrarElementos()).equals("v1 v3 v4 v6 v7 v9 v12 v15 v17 v18 v20 v24"));
        arbol.eliminar(6);
        verificar("Eliminar un nodo con dos hijos (6) mantiene el orden",
                textoCola(arbol.mostrarElementos()).equals("v1 v3 v4 v7 v9 v12 v15 v17 v18 v20 v24"));
    }

    // ---------------------------------------------------------------- P6
    private static void pruebaTablaHash() {
        System.out.println("\nP6. Tabla hash (busqueda de solicitudes por codigo)");
        GestorSolicitudes g = DatosIniciales.crearSistema().getGestorSolicitudes();
        verificar("SOL-0010 se encuentra al instante y es Inmobiliaria Los Andes",
                g.buscarSolicitud("sol-0010").getNombreSolicitante().startsWith("Inmobiliaria"));
        verificar("Una solicitud ya finalizada (SOL-0006) tambien se encuentra",
                g.buscarSolicitud("SOL-0006") != null);
        TablaHash<String, Integer> tabla = new TablaHash<>(4);
        for (int i = 0; i < 500; i++) {
            tabla.insertar("K" + i, i);
        }
        boolean todas = true;
        for (int i = 0; i < 500; i++) {
            todas &= tabla.buscar("K" + i) == i;
        }
        verificar("500 claves insertadas con rehash se recuperan todas (capacidad "
                + tabla.getCapacidad() + ")", todas && tabla.cantidadElementos() == 500);
    }

    // ---------------------------------------------------------------- P7
    private static void pruebaListaYOrdenamiento() {
        System.out.println("\nP7. Lista enlazada y ordenamiento (merge sort)");
        SistemaEstudio s = DatosIniciales.crearSistema();
        ListaEnlazada<Expediente> deLucia = s.getGestorExpedientes().buscarPorCliente("70214589");
        verificar("La lista enlazada del cliente 70214589 tiene EXP-2026-0005",
                deLucia.cantidadElementos() == 1 && deLucia.obtener(0).getCodigo().equals("EXP-2026-0005"));
        ListaEnlazada<Expediente> ordenada = s.getGestorExpedientes()
                .listarOrdenadosPor(CriteriosOrden.POR_ULTIMO_MOVIMIENTO);
        boolean desc = true;
        Nodo<Expediente> aux = ordenada.getInicio();
        while (aux != null && aux.getSiguiente() != null) {
            desc &= !aux.getDato().obtenerUltimoMovimiento().getFecha()
                    .isBefore(aux.getSiguiente().getDato().obtenerUltimoMovimiento().getFecha());
            aux = aux.getSiguiente();
        }
        verificar("Merge sort ordena por ultimo movimiento (mas reciente primero): "
                + ordenada.obtener(0).getCodigo() + " primero", desc && ordenada.cantidadElementos() == 5);
        ListaEnlazada<Integer> numeros = new ListaEnlazada<>();
        int[] datos = {42, 7, 19, 3, 25, 7, 1};
        for (int d : datos) {
            numeros.agregarFinal(d);
        }
        Ordenamiento.mergeSort(numeros, (a, b) -> a - b);
        verificar("Merge sort sobre enteros: 1 3 7 7 19 25 42",
                textoLista(numeros).equals("1 3 7 7 19 25 42"));
    }

    // ---------------------------------------------------------------- P8
    private static void pruebaConflictoDeInteres() {
        System.out.println("\nP8. Conflicto de interes (busqueda en el arbol de clientes)");
        SistemaEstudio s = DatosIniciales.crearSistema();
        SolicitudConsulta sol = s.getGestorSolicitudes().buscarSolicitud("SOL-0010");
        verificar("Inmobiliaria Los Andes (contraparte de EXP-2026-0005) genera conflicto",
                s.getEvaluadorCasos().existeConflicto(new FichaConsulta(sol, "", "", "", LocalDateTime.now())));
        SolicitudConsulta sol9 = s.getGestorSolicitudes().buscarSolicitud("SOL-0009");
        verificar("Miguel Rojas sin contraparte no genera conflicto",
                !s.getEvaluadorCasos().existeConflicto(new FichaConsulta(sol9, "", "", "", LocalDateTime.now())));
    }

    // ------------------------------------------------------------ apoyo
    private static boolean historialOrdenado(Expediente e) {
        MovimientoLegal[] h = e.obtenerHistorial();
        for (int i = 0; i + 1 < h.length; i++) {
            if (h[i].getFecha().isBefore(h[i + 1].getFecha())) {
                return false;
            }
        }
        return true;
    }

    private static boolean historialesIguales(SistemaEstudio a, SistemaEstudio b) {
        Nodo<Expediente> x = a.getGestorExpedientes().listarOrdenados().getFrente();
        while (x != null) {
            Expediente ea = x.getDato();
            Expediente eb = b.getGestorExpedientes().buscarPorCodigo(ea.getCodigo());
            if (eb == null || !textoPila(ea.getHistorial()).equals(textoPila(eb.getHistorial()))
                    || !textoPila(ea.getAnulados()).equals(textoPila(eb.getAnulados()))) {
                return false;
            }
            x = x.getSiguiente();
        }
        return true;
    }

    private static String textoPila(Pila<MovimientoLegal> p) {
        StringBuilder sb = new StringBuilder();
        Nodo<MovimientoLegal> n = p.getCima();
        while (n != null) {
            MovimientoLegal m = n.getDato();
            sb.append(m.getId()).append('|').append(m.getFecha()).append('|').append(m.getTipo())
              .append('|').append(m.getDescripcion()).append('|').append(m.isAnulado()).append(';');
            n = n.getSiguiente();
        }
        return sb.toString();
    }

    private static <T> String textoCola(Cola<T> c) {
        StringBuilder sb = new StringBuilder();
        Nodo<T> n = c.getFrente();
        while (n != null) {
            Object d = n.getDato();
            sb.append(d instanceof SolicitudConsulta ? ((SolicitudConsulta) d).getCodigo() : String.valueOf(d));
            n = n.getSiguiente();
            if (n != null) sb.append(' ');
        }
        return sb.toString();
    }

    private static <T> String textoLista(ListaEnlazada<T> l) {
        StringBuilder sb = new StringBuilder();
        Nodo<T> n = l.getInicio();
        while (n != null) {
            sb.append(n.getDato());
            n = n.getSiguiente();
            if (n != null) sb.append(' ');
        }
        return sb.toString();
    }
}
