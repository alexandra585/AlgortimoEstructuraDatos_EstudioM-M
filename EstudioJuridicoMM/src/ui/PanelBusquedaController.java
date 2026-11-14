package ui;

import estructuras.Cola;
import estructuras.Nodo;
import modelo.Cliente;
import modelo.Expediente;
import servicio.GestorExpedientes;
import servicio.SistemaEstudio;

/** Módulo Búsqueda de expedientes (ÁRBOL BINARIO DE BÚSQUEDA): RF-14 a RF-16. */
public class PanelBusquedaController {

    private static final String TITULO = Tema.t("Búsqueda de expedientes - Árbol binario de búsqueda", "Búsqueda de expedientes");
    private static final String CABECERA = String.format("%-14s %-28s %-11s %-10s  %-8s%n",
            "Código", "Cliente", "Especialid.", "Apertura", "Estado");

    private SistemaEstudio sistema;
    private GestorExpedientes gestor;

    public PanelBusquedaController(SistemaEstudio sistema) {
        this.sistema = sistema;
        this.gestor = sistema.getGestorExpedientes();
    }

    public void mostrar() {
        int opcion;
        do {
            String menu = Tema.t("Expedientes en el árbol: ", "Expedientes registrados: ") + gestor.getArbolPorCodigo().cantidadElementos()
                    + "   |   Clientes y contrapartes: " + gestor.getArbolPorCliente().cantidadElementos()
                    + "\n\n1. Buscar expediente por código\n"
                    + "2. Buscar expedientes por cliente (DNI/RUC)\n"
                    + Tema.t("3. Listar todos los expedientes ordenados (inorden)\n", "3. Listar todos los expedientes (ordenados por código)\n")
                    + Tema.t("4. Listar clientes y contrapartes (inorden)\n", "4. Listar clientes y contrapartes\n")
                    + Tema.t("5. Información de los árboles\n", "5. Resumen de registros\n")
                    + "6. Volver al menú principal";
            opcion = Dialogos.leerOpcion(menu, TITULO);
            switch (opcion) {
                case 1: buscarPorCodigo(); break;
                case 2: buscarPorCliente(); break;
                case 3: listarOrdenados(); break;
                case 4: listarClientes(); break;
                case 5: infoArboles(); break;
                case 6:
                case -1: opcion = 6; break;
                default: Dialogos.error("Opción no válida.");
            }
        } while (opcion != 6);
    }

    /** RF-14 */
    private void buscarPorCodigo() {
        String codigo = Dialogos.leerTexto("Código del expediente (ej. EXP-2026-0001):", TITULO, true, "EXP-2026-");
        if (codigo == null) return;
        long inicio = System.nanoTime();
        Expediente exp = gestor.buscarPorCodigo(codigo);
        double ms = (System.nanoTime() - inicio) / 1_000_000.0;
        if (exp == null) {
            Dialogos.aviso("No existe el expediente " + codigo.toUpperCase() + ".");
            return;
        }
        if (Dialogos.confirmar("EXPEDIENTE ENCONTRADO (" + String.format("%.3f", ms) + " ms)\n\n"
                + exp.cabecera() + "\n\nÚltimo movimiento: " + exp.obtenerUltimoMovimiento()
                + "\n\n¿Abrir el expediente?", TITULO)) {
            new PanelExpedienteController(sistema).abrir(exp);
        }
    }

    /** RF-15 */
    private void buscarPorCliente() {
        String id = Dialogos.leerIdentificador("DNI / RUC del cliente:", TITULO, true);
        if (id == null) return;
        Cliente c = gestor.buscarCliente(id);
        if (c == null) {
            Dialogos.aviso("No existe un cliente o contraparte con identificador " + id + ".");
            return;
        }
        Cola<Expediente> resultado = gestor.buscarPorCliente(id);
        StringBuilder sb = new StringBuilder();
        sb.append(c.isEsContraparte() ? "CONTRAPARTE: " : "CLIENTE: ").append(c.getNombre())
          .append(" (").append(c.getId()).append(")\nContacto: ").append(c.getContacto()).append("\n\n");
        sb.append(CABECERA).append(Dialogos.linea());
        if (resultado.estaVacia()) {
            sb.append("Sin expedientes.\n");
        }
        Nodo<Expediente> aux = resultado.getFrente();
        while (aux != null) {
            sb.append(aux.getDato()).append("\n");
            aux = aux.getSiguiente();
        }
        sb.append(Dialogos.linea()).append("Expedientes encontrados: ").append(resultado.cantidadElementos());
        Dialogos.mostrarTexto("Expedientes del cliente", sb.toString());

        if (resultado.cantidadElementos() > 0 && Dialogos.confirmar("¿Abrir alguno de estos expedientes?", TITULO)) {
            String[] codigos = new String[resultado.cantidadElementos()];
            aux = resultado.getFrente();
            int i = 0;
            while (aux != null) {
                codigos[i++] = aux.getDato().getCodigo();
                aux = aux.getSiguiente();
            }
            Object elegido = Dialogos.elegir("Expediente:", TITULO, codigos);
            if (elegido != null) {
                new PanelExpedienteController(sistema).abrir(gestor.buscarPorCodigo(elegido.toString()));
            }
        }
    }

    /** RF-16 */
    private void listarOrdenados() {
        Cola<Expediente> lista = gestor.listarOrdenados();
        StringBuilder sb = new StringBuilder(Tema.t("Recorrido INORDEN del árbol por código (orden ascendente)\n\n", "Expedientes ordenados por código\n\n"));
        sb.append(CABECERA).append(Dialogos.linea());
        Nodo<Expediente> aux = lista.getFrente();
        while (aux != null) {
            sb.append(aux.getDato()).append("\n");
            aux = aux.getSiguiente();
        }
        sb.append(Dialogos.linea()).append("Total: ").append(lista.cantidadElementos());
        Dialogos.mostrarTexto("Expedientes ordenados por código", sb.toString());
    }

    private void listarClientes() {
        Cola<Cliente> lista = gestor.listarClientes();
        StringBuilder sb = new StringBuilder(Tema.t("Recorrido INORDEN del árbol por DNI/RUC\n\n", "Clientes y contrapartes ordenados por DNI/RUC\n\n"));
        sb.append(String.format("%-12s %-35s %-11s %s%n", "DNI/RUC", "Nombre", "Tipo", "Expedientes"));
        sb.append(Dialogos.linea());
        Nodo<Cliente> aux = lista.getFrente();
        while (aux != null) {
            sb.append(aux.getDato()).append("\n");
            aux = aux.getSiguiente();
        }
        sb.append(Dialogos.linea()).append("Total: ").append(lista.cantidadElementos());
        Dialogos.mostrarTexto("Clientes y contrapartes", sb.toString());
    }

    /** Versión limpia de la opción 5: totales sin detalles técnicos. */
    private void resumenRegistros() {
        int abiertos = 0, cerrados = 0, clientes = 0, contrapartes = 0;
        Nodo<Expediente> e = gestor.listarOrdenados().getFrente();
        while (e != null) {
            if (e.getDato().getEstado() == modelo.EstadoExpediente.ABIERTO) abiertos++; else cerrados++;
            e = e.getSiguiente();
        }
        Nodo<Cliente> c = gestor.listarClientes().getFrente();
        while (c != null) {
            if (c.getDato().isEsContraparte()) contrapartes++; else clientes++;
            c = c.getSiguiente();
        }
        Dialogos.info("RESUMEN DE REGISTROS\n\n"
                + "Expedientes: " + (abiertos + cerrados)
                + "\n  Abiertos: " + abiertos + "\n  Cerrados: " + cerrados
                + "\n\nClientes: " + clientes
                + "\nContrapartes: " + contrapartes, TITULO);
    }

    private void infoArboles() {
        if (!Tema.MOSTRAR_TEMAS) {
            resumenRegistros();
            return;
        }
        Dialogos.info("ÁRBOL POR CÓDIGO DE EXPEDIENTE\n"
                + "  Nodos: " + gestor.getArbolPorCodigo().cantidadElementos()
                + "\n  Altura: " + gestor.getArbolPorCodigo().altura()
                + "\n  Raíz: " + (gestor.getArbolPorCodigo().estaVacio() ? "-" : gestor.getArbolPorCodigo().getRaiz().getClave())
                + "\n\nÁRBOL POR CLIENTE (DNI/RUC)\n"
                + "  Nodos: " + gestor.getArbolPorCliente().cantidadElementos()
                + "\n  Altura: " + gestor.getArbolPorCliente().altura()
                + "\n  Raíz: " + (gestor.getArbolPorCliente().estaVacio() ? "-" : gestor.getArbolPorCliente().getRaiz().getClave()),
                TITULO);
    }
}
