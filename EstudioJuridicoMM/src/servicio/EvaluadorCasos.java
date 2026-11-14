package servicio;

import estructuras.Cola;
import java.io.Serializable;
import java.time.LocalDate;
import modelo.Cliente;
import modelo.EstadoSolicitud;
import modelo.Expediente;
import modelo.FichaConsulta;
import modelo.PropuestaHonorarios;

/**
 * Proceso 1 (continuación) - Evaluación y Apertura de Caso.
 * Las fichas atendidas esperan evaluación en una COLA (se evalúan en orden).
 * El conflicto de interés se verifica buscando en el ÁRBOL de clientes y contrapartes.
 */
public class EvaluadorCasos implements Serializable {

    private static final long serialVersionUID = 1L;

    private GestorExpedientes gestorExpedientes;
    private Cola<FichaConsulta> enEvaluacion;
    private Cola<FichaConsulta> finalizadas;
    private String ultimoMotivoConflicto;

    public EvaluadorCasos(GestorExpedientes gestorExpedientes) {
        this.gestorExpedientes = gestorExpedientes;
        this.enEvaluacion = new Cola<>();
        this.finalizadas = new Cola<>();
    }

    /** RF-05: recibe la ficha de consulta inicial de la solicitud atendida. */
    public void recibirFicha(FichaConsulta ficha) {
        enEvaluacion.agregar(ficha);
    }

    /** Ficha que se está evaluando (frente de la cola de evaluación). */
    public FichaConsulta fichaActual() {
        return enEvaluacion.peek();
    }

    /**
     * RF-06: hay conflicto de interés si
     *  a) el solicitante figura como CONTRAPARTE en un caso del estudio, o
     *  b) la contraparte indicada es CLIENTE del estudio.
     */
    public boolean existeConflicto(FichaConsulta ficha) {
        ultimoMotivoConflicto = null;
        Cliente solicitante = gestorExpedientes.buscarCliente(ficha.getSolicitud().getIdSolicitante());
        if (solicitante != null && solicitante.isEsContraparte()) {
            ultimoMotivoConflicto = "El solicitante " + solicitante.getNombre()
                    + " figura como CONTRAPARTE en un caso del estudio.";
            return true;
        }
        if (ficha.tieneContraparte()) {
            Cliente contraparte = gestorExpedientes.buscarCliente(ficha.getIdContraparte());
            if (contraparte != null && !contraparte.isEsContraparte()) {
                ultimoMotivoConflicto = "La contraparte " + contraparte.getNombre()
                        + " es CLIENTE del estudio.";
                return true;
            }
        }
        ficha.setConflictoVerificado(true);
        return false;
    }

    public String getUltimoMotivoConflicto() {
        return ultimoMotivoConflicto;
    }

    /** Conflicto detectado: la solicitud pasa a RECHAZADA y el flujo termina. */
    public FichaConsulta rechazarCaso(String motivo) {
        FichaConsulta f = enEvaluacion.poll();
        if (f != null) {
            f.getSolicitud().cambiarEstado(EstadoSolicitud.RECHAZADA);
            f.setObservacion("Rechazada: " + motivo);
            finalizadas.agregar(f);
        }
        return f;
    }

    /** RF-07: registra la cotización de servicios con plazo de 5 días. */
    public PropuestaHonorarios registrarPropuesta(double monto, String descripcion, LocalDate fechaEmision) {
        FichaConsulta f = fichaActual();
        PropuestaHonorarios p = new PropuestaHonorarios(monto, descripcion, fechaEmision);
        f.setPropuesta(p);
        return p;
    }

    /**
     * RF-07: registra la decisión del cliente. Si no acepta o respondió fuera
     * del plazo de 5 días, la solicitud se ARCHIVA. Devuelve true si quedó ACEPTADA.
     */
    public boolean registrarDecision(boolean acepta, LocalDate fechaRespuesta) {
        FichaConsulta f = fichaActual();
        PropuestaHonorarios p = f.getPropuesta();
        p.registrarDecision(acepta, fechaRespuesta);
        if (!acepta) {
            archivarSolicitud("El cliente no aceptó la propuesta");
            return false;
        }
        if (p.estaVencida(fechaRespuesta)) {
            archivarSolicitud("Plazo de 5 días vencido (límite " + modelo.Fechas.texto(p.getFechaLimite()) + ")");
            return false;
        }
        f.getSolicitud().cambiarEstado(EstadoSolicitud.ACEPTADA);
        return true;
    }

    /** La solicitud pasa a ARCHIVADA y el flujo termina. */
    public FichaConsulta archivarSolicitud(String motivo) {
        FichaConsulta f = enEvaluacion.poll();
        if (f != null) {
            f.getSolicitud().cambiarEstado(EstadoSolicitud.ARCHIVADA);
            f.setObservacion("Archivada: " + motivo);
            finalizadas.agregar(f);
        }
        return f;
    }

    /** RF-08: contrato firmado -> se abre el expediente y se inserta en los árboles. */
    public Expediente abrirExpediente(LocalDate fechaApertura) {
        FichaConsulta f = enEvaluacion.poll();
        Expediente exp = gestorExpedientes.abrirExpediente(f, fechaApertura);
        f.setObservacion("Expediente " + exp.getCodigo());
        finalizadas.agregar(f);
        return exp;
    }

    public Cola<FichaConsulta> getEnEvaluacion() { return enEvaluacion; }
    public Cola<FichaConsulta> getFinalizadas() { return finalizadas; }
}
