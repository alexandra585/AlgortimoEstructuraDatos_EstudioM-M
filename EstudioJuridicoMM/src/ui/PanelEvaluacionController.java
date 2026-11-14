package ui;

import estructuras.Nodo;
import java.time.LocalDate;
import modelo.EstadoSolicitud;
import modelo.Expediente;
import modelo.FichaConsulta;
import modelo.Fechas;
import modelo.PropuestaHonorarios;
import servicio.EvaluadorCasos;
import servicio.SistemaEstudio;

/** Módulo Evaluación y Apertura de Caso (Proceso 1, BPMN): RF-05 a RF-08. */
public class PanelEvaluacionController {

    private static final String TITULO = "Evaluación y apertura de caso";

    private EvaluadorCasos evaluador;

    public PanelEvaluacionController(SistemaEstudio sistema) {
        this.evaluador = sistema.getEvaluadorCasos();
    }

    public void mostrar() {
        int opcion;
        do {
            FichaConsulta f = evaluador.fichaActual();
            String actual = (f == null) ? "Ninguna (atienda una solicitud en el módulo 1)"
                    : f.getSolicitud().getCodigo() + " - " + f.getSolicitud().getNombreSolicitante()
                    + "\nPaso: " + pasoActual(f);
            String menu = "Ficha en evaluación: " + actual
                    + "\nEn espera de evaluación: " + evaluador.getEnEvaluacion().cantidadElementos()
                    + "\n\n1. Ver ficha de consulta actual\n"
                    + "2. Verificar conflicto de interés\n"
                    + "3. Registrar propuesta de honorarios\n"
                    + "4. Registrar decisión del cliente\n"
                    + "5. Registrar contrato y abrir expediente\n"
                    + "6. Ver fichas en espera de evaluación\n"
                    + "7. Ver solicitudes finalizadas\n"
                    + "8. Volver al menú principal";
            opcion = Dialogos.leerOpcion(menu, TITULO);
            switch (opcion) {
                case 1: verFicha(); break;
                case 2: verificarConflicto(); break;
                case 3: registrarPropuesta(); break;
                case 4: registrarDecision(); break;
                case 5: abrirExpediente(); break;
                case 6: listar(evaluador.getEnEvaluacion().getFrente(), "Fichas en espera de evaluación"); break;
                case 7: listar(evaluador.getFinalizadas().getFrente(), "Solicitudes finalizadas"); break;
                case 8:
                case -1: opcion = 8; break;
                default: Dialogos.error("Opción no válida.");
            }
        } while (opcion != 8);
    }

    private String pasoActual(FichaConsulta f) {
        if (!f.isConflictoVerificado()) return "verificar conflicto de interés";
        if (f.getPropuesta() == null) return "registrar propuesta de honorarios";
        if (f.getSolicitud().getEstado() != EstadoSolicitud.ACEPTADA) return "registrar decisión del cliente";
        return "registrar contrato y abrir expediente";
    }

    private FichaConsulta fichaOAviso() {
        FichaConsulta f = evaluador.fichaActual();
        if (f == null) {
            Dialogos.aviso("No hay fichas en evaluación.\nPrimero atienda una solicitud en el módulo Solicitudes.");
        }
        return f;
    }

    private void verFicha() {
        FichaConsulta f = fichaOAviso();
        if (f != null) {
            Dialogos.mostrarTexto("Ficha de consulta inicial", f.detalle());
        }
    }

    /** RF-06: búsqueda en el árbol de clientes y contrapartes. */
    private void verificarConflicto() {
        FichaConsulta f = fichaOAviso();
        if (f == null) return;
        if (evaluador.existeConflicto(f)) {
            String motivo = evaluador.getUltimoMotivoConflicto();
            if (Dialogos.confirmar("CONFLICTO DE INTERÉS DETECTADO\n\n" + motivo
                    + "\n\n¿Rechazar el caso " + f.getSolicitud().getCodigo() + "?", TITULO)) {
                evaluador.rechazarCaso(motivo);
                Dialogos.info("Caso rechazado. La solicitud quedó en estado RECHAZADA.", TITULO);
            }
        } else {
            Dialogos.info("SIN CONFLICTO DE INTERÉS.\n\n" + Tema.t("Se buscó en el árbol de clientes y contrapartes:\n", "Se revisó la base de clientes y contrapartes:\n")
                    + "- Solicitante: " + f.getSolicitud().getIdSolicitante() + "\n"
                    + "- Contraparte: " + (f.tieneContraparte() ? f.getIdContraparte() : "no indicada")
                    + "\n\nPuede registrar la propuesta de honorarios.", TITULO);
        }
    }

    /** RF-07 (primera parte) */
    private void registrarPropuesta() {
        FichaConsulta f = fichaOAviso();
        if (f == null) return;
        if (!f.isConflictoVerificado()) {
            Dialogos.aviso("Primero verifique el conflicto de interés (opción 2).");
            return;
        }
        if (f.getPropuesta() != null && !Dialogos.confirmar(
                "Ya existe una propuesta:\n" + f.getPropuesta() + "\n\n¿Reemplazarla?", TITULO)) {
            return;
        }
        double monto = Dialogos.leerMonto("Monto de honorarios (S/):", TITULO);
        if (monto < 0) return;
        String desc = Dialogos.leerTexto("Descripción del servicio cotizado:", TITULO, true,
                "Patrocinio del caso hasta primera instancia");
        if (desc == null) return;
        LocalDate emision = Dialogos.leerFecha("Fecha de emisión de la propuesta:", TITULO, LocalDate.now());
        if (emision == null) return;
        PropuestaHonorarios p = evaluador.registrarPropuesta(monto, desc, emision);
        Dialogos.info("Propuesta registrada:\n" + p + "\n\nEl cliente tiene " + PropuestaHonorarios.PLAZO_DIAS
                + " días para responder (hasta el " + Fechas.texto(p.getFechaLimite()) + ").", TITULO);
    }

    /** RF-07 (segunda parte) */
    private void registrarDecision() {
        FichaConsulta f = fichaOAviso();
        if (f == null) return;
        if (f.getPropuesta() == null) {
            Dialogos.aviso("Primero registre la propuesta de honorarios (opción 3).");
            return;
        }
        if (f.getSolicitud().getEstado() == EstadoSolicitud.ACEPTADA) {
            Dialogos.aviso("El cliente ya aceptó. Registre el contrato y abra el expediente (opción 5).");
            return;
        }
        Object r = Dialogos.elegir("Propuesta:\n" + f.getPropuesta() + "\n\n¿El cliente acepta?", TITULO,
                new String[]{"Cliente acepta", "Cliente NO acepta"});
        if (r == null) return;
        boolean acepta = r.toString().equals("Cliente acepta");
        LocalDate fecha = Dialogos.leerFecha("Fecha de respuesta del cliente:", TITULO, LocalDate.now());
        if (fecha == null) return;

        if (evaluador.registrarDecision(acepta, fecha)) {
            Dialogos.info("Propuesta ACEPTADA dentro del plazo.", TITULO);
            if (Dialogos.confirmar("¿El cliente ya firmó el contrato de representación?\n"
                    + "(Si responde Sí, se abre el expediente ahora)", TITULO)) {
                abrirExpediente();
            }
        } else {
            Dialogos.info("Solicitud ARCHIVADA.\n" + ultimaObservacion(), TITULO);
        }
    }

    /** RF-08 */
    private void abrirExpediente() {
        FichaConsulta f = fichaOAviso();
        if (f == null) return;
        if (f.getSolicitud().getEstado() != EstadoSolicitud.ACEPTADA) {
            Dialogos.aviso("Solo se puede abrir el expediente si el cliente aceptó la propuesta.");
            return;
        }
        LocalDate fecha = Dialogos.leerFecha("Fecha de firma del contrato / apertura:", TITULO, LocalDate.now());
        if (fecha == null) return;
        Expediente exp = evaluador.abrirExpediente(fecha);
        Dialogos.info("EXPEDIENTE ABIERTO\n\nCódigo generado: " + exp.getCodigo() + "\n\n" + exp.cabecera()
                + Tema.t("\n\nSe insertó en el árbol por código y el cliente en el árbol de clientes.", "\n\nEl expediente y el cliente quedaron registrados."), TITULO);
    }

    private String ultimaObservacion() {
        Nodo<FichaConsulta> aux = evaluador.getFinalizadas().getFrente();
        String obs = "";
        while (aux != null) {
            obs = aux.getDato().getObservacion();
            aux = aux.getSiguiente();
        }
        return obs;
    }

    private void listar(Nodo<FichaConsulta> aux, String titulo) {
        if (aux == null) {
            Dialogos.info("No hay registros.", titulo);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-9s %-30s %-14s %s%n", "Código", "Solicitante", "Estado", "Observación"));
        sb.append(Dialogos.linea());
        while (aux != null) {
            sb.append(aux.getDato()).append("\n");
            aux = aux.getSiguiente();
        }
        Dialogos.mostrarTexto(titulo, sb.toString());
    }
}
