package ui;

import estructuras.Nodo;
import java.time.LocalDate;
import modelo.EstadoExpediente;
import modelo.Expediente;
import modelo.MovimientoLegal;
import modelo.TipoMovimiento;
import servicio.GestorExpedientes;
import servicio.GestorHistorial;
import servicio.SistemaEstudio;

/** Módulo Expediente e Historial (Proceso 2 - PILA): RF-09 a RF-13. */
public class PanelExpedienteController {

    private static final String TITULO = Tema.t("Expediente e historial - Pila LIFO", "Expediente e historial");

    private GestorHistorial historial;
    private GestorExpedientes expedientes;

    public PanelExpedienteController(SistemaEstudio sistema) {
        this.historial = sistema.getGestorHistorial();
        this.expedientes = sistema.getGestorExpedientes();
    }

    /** Pide el expediente y abre su menú. */
    public void mostrar() {
        Expediente exp = seleccionarExpediente();
        while (exp != null) {
            exp = menuExpediente(exp);
        }
    }

    /** Abre directamente un expediente (se usa desde el módulo de búsqueda). */
    public void abrir(Expediente exp) {
        while (exp != null) {
            exp = menuExpediente(exp);
        }
    }

    /** Lista desplegable con los expedientes ordenados por código (inorden del ABB). */
    private Expediente seleccionarExpediente() {
        int n = expedientes.getArbolPorCodigo().cantidadElementos();
        if (n == 0) {
            Dialogos.aviso("No hay expedientes abiertos.");
            return null;
        }
        String[] codigos = new String[n];
        Nodo<Expediente> aux = expedientes.listarOrdenados().getFrente();
        int i = 0;
        while (aux != null) {
            codigos[i++] = aux.getDato().getCodigo() + " | " + aux.getDato().getCliente().getNombre();
            aux = aux.getSiguiente();
        }
        Object elegido = Dialogos.elegir("Seleccione el expediente:", TITULO, codigos);
        if (elegido == null) {
            return null;
        }
        String codigo = elegido.toString().split(" \\| ")[0];
        return historial.obtenerExpediente(codigo);
    }

    /** Devuelve el siguiente expediente a mostrar, o null para volver. */
    private Expediente menuExpediente(Expediente exp) {
        while (true) {
            MovimientoLegal ultimo = historial.consultarUltimo(exp);
            String menu = exp.cabecera()
                    + Tema.t("\n\n>> ÚLTIMO MOVIMIENTO (cima de la pila):\n   ", "\n\n>> ÚLTIMO MOVIMIENTO:\n   ")
                    + (ultimo == null ? "Sin movimientos" : ultimo)
                    + "\n   Movimientos vigentes: " + exp.getHistorial().cantidadElementos()
                    + "   |   Anulados: " + exp.getAnulados().cantidadElementos()
                    + "\n\n1. Ver último movimiento\n"
                    + "2. Registrar movimiento\n"
                    + "3. Modificar un movimiento\n"
                    + "4. Anular último movimiento\n"
                    + "5. Ver historial completo\n"
                    + "6. Navegar historial (anterior / siguiente)\n"
                    + "7. Cerrar / reabrir expediente\n"
                    + "8. Cambiar de expediente\n"
                    + "9. Volver";
            int op = Dialogos.leerOpcion(menu, TITULO);
            switch (op) {
                case 1: verUltimo(exp); break;
                case 2: registrar(exp); break;
                case 3: modificar(exp); break;
                case 4: anularUltimo(exp); break;
                case 5: verHistorial(exp); break;
                case 6: navegar(exp); break;
                case 7: cambiarEstado(exp); break;
                case 8: return seleccionarExpediente();
                case 9:
                case -1: return null;
                default: Dialogos.error("Opción no válida.");
            }
        }
    }

    /** RF-10: un solo paso, O(1). */
    private void verUltimo(Expediente exp) {
        MovimientoLegal m = historial.consultarUltimo(exp);
        if (m == null) {
            Dialogos.info("El expediente no tiene movimientos.", TITULO);
        } else {
            Dialogos.info("ÚLTIMO MOVIMIENTO de " + exp.getCodigo() + "\n\n" + m.detalle(), TITULO);
        }
    }

    /** RF-09 y RF-13 (movimiento intermedio por fecha). */
    private void registrar(Expediente exp) {
        if (exp.getEstado() == EstadoExpediente.CERRADO) {
            Dialogos.aviso("El expediente está CERRADO. Reábralo para registrar movimientos.");
            return;
        }
        LocalDate fecha = Dialogos.leerFecha("Fecha del movimiento:", TITULO, LocalDate.now());
        if (fecha == null) return;
        if (fecha.isBefore(exp.getFechaApertura())) {
            Dialogos.error("La fecha no puede ser anterior a la apertura del expediente ("
                    + modelo.Fechas.texto(exp.getFechaApertura()) + ").");
            return;
        }
        Object tipo = Dialogos.elegir("Tipo de movimiento:", TITULO, TipoMovimiento.values());
        if (tipo == null) return;
        String desc = Dialogos.leerTexto("Descripción del movimiento:", TITULO, true);
        if (desc == null) return;
        String doc = Dialogos.leerTexto("Documento de referencia (opcional):", TITULO, false);
        if (doc == null) return;

        boolean intermedio = exp.esFechaIntermedia(fecha);
        MovimientoLegal m = historial.agregarMovimiento(exp, fecha, (TipoMovimiento) tipo, desc, doc);
        String msg = "Movimiento #" + m.getId() + " registrado.";
        if (intermedio) {
            msg += "\n\nSu fecha es anterior al último movimiento: se ubicó en su posición"
                    + Tema.t("\ncronológica usando una pila auxiliar. La cima sigue siendo el más reciente.", "\ncronológica dentro del historial.");
        } else {
            msg += Tema.t("\n\nSe apiló en la cima: ahora es el ÚLTIMO MOVIMIENTO del expediente.", "\n\nAhora es el ÚLTIMO MOVIMIENTO del expediente.");
        }
        Dialogos.info(msg, TITULO);
    }

    /** RF-13: modificar un movimiento vigente. */
    private void modificar(Expediente exp) {
        MovimientoLegal[] movs = historial.listarHistorial(exp);
        if (movs.length == 0) {
            Dialogos.info("No hay movimientos vigentes.", TITULO);
            return;
        }
        Object elegido = Dialogos.elegir("Seleccione el movimiento a modificar:", TITULO, movs);
        if (elegido == null) return;
        MovimientoLegal m = (MovimientoLegal) elegido;
        Object tipo = elegirTipo(m.getTipo());
        if (tipo == null) return;
        String desc = Dialogos.leerTexto("Nueva descripción:", TITULO, true, m.getDescripcion());
        if (desc == null) return;
        String doc = Dialogos.leerTexto("Documento de referencia:", TITULO, false, m.getDocumentoReferencia());
        if (doc == null) return;
        historial.modificarMovimiento(exp, m.getId(), (TipoMovimiento) tipo, desc, doc);
        Dialogos.info("Movimiento modificado:\n\n" + m.detalle(), TITULO);
    }

    private Object elegirTipo(TipoMovimiento actual) {
        return javax.swing.JOptionPane.showInputDialog(Dialogos.padre(), "Tipo de movimiento:", TITULO,
                javax.swing.JOptionPane.QUESTION_MESSAGE, null, TipoMovimiento.values(), actual);
    }

    /** RF-11: desapilar + anulación lógica (no se elimina). */
    private void anularUltimo(Expediente exp) {
        MovimientoLegal m = historial.consultarUltimo(exp);
        if (m == null) {
            Dialogos.info("No hay movimientos para anular.", TITULO);
            return;
        }
        if (exp.getHistorial().cantidadElementos() == 1) {
            Dialogos.aviso("No se puede anular el movimiento de apertura del expediente.");
            return;
        }
        if (!Dialogos.confirmar("Se anulará el último movimiento:\n\n" + m.detalle()
                + "\n\n¿Confirmar anulación?", TITULO)) {
            return;
        }
        historial.revertirUltimo(exp);
        MovimientoLegal nuevo = historial.consultarUltimo(exp);
        Dialogos.info("Movimiento #" + m.getId() + " ANULADO (se conserva en el historial de anulados).\n\n"
                + "Nuevo último movimiento:\n" + nuevo, TITULO);
    }

    /** RF-12: historial completo (vigentes y anulados). */
    private void verHistorial(Expediente exp) {
        StringBuilder sb = new StringBuilder(exp.cabecera()).append("\n\n");
        sb.append(Tema.t("MOVIMIENTOS VIGENTES (de la cima = más reciente, a la base = más antiguo)\n", "MOVIMIENTOS VIGENTES (del más reciente al más antiguo)\n"));
        sb.append(Dialogos.linea());
        MovimientoLegal[] movs = historial.listarHistorial(exp);
        for (int i = 0; i < movs.length; i++) {
            sb.append(i == 0 ? Tema.t("CIMA -> ", "ÚLTIMO> ") : "        ").append(movs[i]).append("\n");
        }
        sb.append(Dialogos.linea());
        sb.append("\nMOVIMIENTOS ANULADOS (no se eliminan)\n");
        sb.append(Dialogos.linea());
        if (exp.getAnulados().estaVacia()) {
            sb.append("        Ninguno\n");
        } else {
            Nodo<MovimientoLegal> aux = exp.getAnulados().getCima();
            while (aux != null) {
                sb.append("[ANULADO] ").append(aux.getDato()).append("\n");
                aux = aux.getSiguiente();
            }
        }
        Dialogos.mostrarTexto("Historial completo - " + exp.getCodigo(), sb.toString());
    }

    /** RF-12: navegación movimiento por movimiento. */
    private void navegar(Expediente exp) {
        MovimientoLegal[] movs = historial.listarHistorial(exp);
        if (movs.length == 0) {
            Dialogos.info("No hay movimientos.", TITULO);
            return;
        }
        String[] botones = {"<< Más antiguo", "Más reciente >>", "Cerrar"};
        int i = 0;   // 0 = cima (más reciente)
        while (true) {
            String texto = "Movimiento " + (movs.length - i) + " de " + movs.length
                    + (i == 0 ? Tema.t("   (ÚLTIMO - cima)", "   (ÚLTIMO)") : "") + (i == movs.length - 1 ? "   (PRIMERO)" : "")
                    + "\n\n" + movs[i].detalle();
            int r = javax.swing.JOptionPane.showOptionDialog(Dialogos.padre(), texto, "Navegar historial - " + exp.getCodigo(),
                    javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.INFORMATION_MESSAGE,
                    null, botones, botones[0]);
            if (r == 0) {
                if (i < movs.length - 1) i++; else Dialogos.info("Ya está en el primer movimiento.", TITULO);
            } else if (r == 1) {
                if (i > 0) i--; else Dialogos.info("Ya está en el último movimiento.", TITULO);
            } else {
                return;
            }
        }
    }

    private void cambiarEstado(Expediente exp) {
        if (exp.getEstado() == EstadoExpediente.ABIERTO) {
            if (Dialogos.confirmar("¿Cerrar el expediente " + exp.getCodigo() + "?", TITULO)) {
                exp.cerrar();
            }
        } else if (Dialogos.confirmar("¿Reabrir el expediente " + exp.getCodigo() + "?", TITULO)) {
            exp.reabrir();
        }
    }
}
