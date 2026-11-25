package ui;

import estructuras.Nodo;
import java.time.LocalDateTime;
import modelo.FichaConsulta;
import modelo.SolicitudConsulta;
import servicio.EvaluadorCasos;
import servicio.GestorSolicitudes;
import servicio.SistemaEstudio;

/** Módulo Solicitudes (Proceso 1 - COLA): RF-01 a RF-05. */
public class PanelSolicitudesController {

    public static final String[] ESPECIALIDADES = {
        "Civil", "Laboral", "Penal", "Familia", "Comercial", "Tributario", "Administrativo"
    };
    private static final String TITULO = Tema.t("Solicitudes de consulta - Cola FIFO", "Solicitudes de consulta");

    private GestorSolicitudes gestor;
    private EvaluadorCasos evaluador;

    public PanelSolicitudesController(SistemaEstudio sistema) {
        this.gestor = sistema.getGestorSolicitudes();
        this.evaluador = sistema.getEvaluadorCasos();
    }

    public void mostrar() {
        int opcion;
        do {
            SolicitudConsulta frente = gestor.verSiguiente();
            String menu = Tema.t("Pendientes en cola: ", "Solicitudes pendientes: ") + gestor.cantidadPendientes()
                    + "\nSiguiente: " + (frente == null ? "-" : frente.getCodigo() + " " + frente.getNombreSolicitante())
                    + "\n\n1. Registrar nueva solicitud\n"
                    + Tema.t("2. Ver cola de solicitudes pendientes\n", "2. Ver solicitudes pendientes (orden de llegada)\n")
                    + "3. Atender siguiente solicitud (llenar ficha)\n"
                    + "4. Ver siguiente sin retirar\n"
                    + Tema.t("5. Buscar solicitud por código (tabla hash)\n", "5. Buscar solicitud por código\n")
                    + "6. Volver al menú principal";
            opcion = Dialogos.leerOpcion(menu, TITULO);
            switch (opcion) {
                case 1: registrar(); break;
                case 2: listar(); break;
                case 3: atenderSiguiente(); break;
                case 4: verSiguiente(); break;
                case 5: buscarPorCodigo(); break;
                case 6:
                case -1: opcion = 6; break;
                default: Dialogos.error("Opción no válida.");
            }
        } while (opcion != 6);
    }

    /** RF-01 */
    private void registrar() {
        String nombre = Dialogos.leerTexto("Nombre o razón social del solicitante:", TITULO, true);
        if (nombre == null) return;
        String id = Dialogos.leerIdentificador("DNI / RUC del solicitante:", TITULO, true);
        if (id == null) return;
        String contacto = Dialogos.leerTexto("Teléfono o correo de contacto:", TITULO, true);
        if (contacto == null) return;
        String motivo = Dialogos.leerTexto("Motivo de la consulta:", TITULO, true);
        if (motivo == null) return;
        Object esp = Dialogos.elegir("Especialidad:", TITULO, ESPECIALIDADES);
        if (esp == null) return;

        SolicitudConsulta s = gestor.registrarSolicitud(nombre, id, contacto, motivo, esp.toString());
        Dialogos.info(Tema.t("Solicitud registrada y encolada.\n\n", "Solicitud registrada.\n\n") + s.detalle()
                + Tema.t("\n\nPosición en la cola: ", "\n\nTurno de atención: ") + gestor.cantidadPendientes(), TITULO);
    }

    /** RF-02 */
    private void listar() {
        if (gestor.listarPendientes().estaVacia()) {
            Dialogos.info("No hay solicitudes pendientes.", TITULO);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-9s %-30s %-16s %s%n", "Pos", "Código", "Solicitante", "Llegada", "Motivo"));
        sb.append(Dialogos.linea());
        Nodo<SolicitudConsulta> aux = gestor.listarPendientes().getFrente();
        int pos = 1;
        while (aux != null) {
            sb.append(String.format("%-4d %s%n", pos, aux.getDato()));
            aux = aux.getSiguiente();
            pos++;
        }
        sb.append(Dialogos.linea());
        sb.append("Total pendientes: ").append(gestor.cantidadPendientes())
          .append(Tema.t("   (FRENTE = posición 1, se atiende primero)", "   (la posición 1 se atiende primero)"));
        Dialogos.mostrarTexto(Tema.t("Cola de solicitudes pendientes", "Solicitudes pendientes"), sb.toString());
    }

    /** RF-03 y RF-05: atiende solo la solicitud del frente y registra la ficha. */
    private void atenderSiguiente() {
        SolicitudConsulta s = gestor.verSiguiente();
        if (s == null) {
            Dialogos.info(Tema.t("No hay solicitudes en la cola.", "No hay solicitudes pendientes."), TITULO);
            return;
        }
        if (!Dialogos.confirmar(Tema.t("Se atenderá la solicitud del FRENTE de la cola:\n\n", "Se atenderá la siguiente solicitud (orden de llegada):\n\n") + s.detalle()
                + "\n\n¿Llenar la Ficha de Consulta Inicial?", TITULO)) {
            return;
        }
        String idContra = Dialogos.leerIdentificador(
                "Ficha de consulta - DNI/RUC de la CONTRAPARTE\n(déjelo vacío si no hay contraparte):", TITULO, false);
        if (idContra == null) return;
        String nombreContra = "";
        if (!idContra.isEmpty()) {
            nombreContra = Dialogos.leerTexto("Nombre o razón social de la contraparte:", TITULO, true);
            if (nombreContra == null) return;
        }
        String adicionales = Dialogos.leerTexto("Datos adicionales de la consulta (opcional):", TITULO, false);
        if (adicionales == null) return;

        SolicitudConsulta atendida = gestor.atenderSiguiente();   // desencolar
        FichaConsulta ficha = new FichaConsulta(atendida, idContra, nombreContra, adicionales, LocalDateTime.now());
        evaluador.recibirFicha(ficha);
        Dialogos.info("Solicitud " + atendida.getCodigo() + " atendida y enviada a Evaluación.\n"
                + Tema.t("Pendientes en cola: ", "Solicitudes pendientes: ") + gestor.cantidadPendientes(), TITULO);
    }

    /** RF-18: búsqueda directa en la tabla hash (pendientes, en evaluación o finalizadas). */
    private void buscarPorCodigo() {
        String codigo = Dialogos.leerTexto("Código de la solicitud (ej. SOL-0010):", TITULO, true, "SOL-");
        if (codigo == null) return;
        SolicitudConsulta s = gestor.buscarSolicitud(codigo);
        if (s == null) {
            Dialogos.aviso("No existe la solicitud " + codigo.trim().toUpperCase() + ".");
            return;
        }
        String extra = Tema.t("\n\nBúsqueda en la tabla hash: " + gestor.getIndicePorCodigo().cantidadElementos()
                + " solicitudes indexadas, capacidad " + gestor.getIndicePorCodigo().getCapacidad()
                + ", cubetas con colisión: " + gestor.getIndicePorCodigo().cubetasConColision(), "");
        Dialogos.info("SOLICITUD ENCONTRADA\n\n" + s.detalle() + extra, TITULO);
    }

    /** RF-04 */
    private void verSiguiente() {
        SolicitudConsulta s = gestor.verSiguiente();
        if (s == null) {
            Dialogos.info(Tema.t("No hay solicitudes en la cola.", "No hay solicitudes pendientes."), TITULO);
        } else {
            Dialogos.info(Tema.t("Solicitud en el FRENTE (no se retira):\n\n", "Siguiente solicitud a atender:\n\n") + s.detalle(), TITULO);
        }
    }
}
