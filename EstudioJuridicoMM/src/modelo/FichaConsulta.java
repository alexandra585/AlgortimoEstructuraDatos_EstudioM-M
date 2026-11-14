package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Ficha de Consulta Inicial: la llena Recepción al atender la solicitud del frente
 * de la cola. Registra la contraparte para poder evaluar el conflicto de interés.
 */
public class FichaConsulta implements Serializable {

    private static final long serialVersionUID = 1L;

    private SolicitudConsulta solicitud;
    private String idContraparte;
    private String nombreContraparte;
    private String datosAdicionales;
    private LocalDateTime fechaAtencion;
    private boolean conflictoVerificado;
    private PropuestaHonorarios propuesta;
    private String observacion;        // motivo de rechazo / archivo / código de expediente

    public FichaConsulta(SolicitudConsulta solicitud, String idContraparte, String nombreContraparte,
            String datosAdicionales, LocalDateTime fechaAtencion) {
        this.solicitud = solicitud;
        this.idContraparte = idContraparte;
        this.nombreContraparte = nombreContraparte;
        this.datosAdicionales = datosAdicionales;
        this.fechaAtencion = fechaAtencion;
        this.conflictoVerificado = false;
    }

    public boolean tieneContraparte() {
        return idContraparte != null && !idContraparte.isEmpty();
    }

    public SolicitudConsulta getSolicitud() { return solicitud; }
    public String getIdContraparte() { return idContraparte; }
    public String getNombreContraparte() { return nombreContraparte; }
    public String getDatosAdicionales() { return datosAdicionales; }
    public LocalDateTime getFechaAtencion() { return fechaAtencion; }
    public boolean isConflictoVerificado() { return conflictoVerificado; }
    public void setConflictoVerificado(boolean v) { this.conflictoVerificado = v; }
    public PropuestaHonorarios getPropuesta() { return propuesta; }
    public void setPropuesta(PropuestaHonorarios propuesta) { this.propuesta = propuesta; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String detalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== FICHA DE CONSULTA INICIAL ===\n");
        sb.append(solicitud.detalle()).append("\n");
        sb.append("Contraparte: ")
          .append(tieneContraparte() ? nombreContraparte + " (" + idContraparte + ")" : "No indicada").append("\n");
        sb.append("Datos adicionales: ").append(datosAdicionales.isEmpty() ? "-" : datosAdicionales).append("\n");
        sb.append("Atendida: ").append(Fechas.texto(fechaAtencion)).append("\n");
        sb.append("Conflicto verificado: ").append(conflictoVerificado ? "Sí (sin conflicto)" : "No").append("\n");
        sb.append("Propuesta: ").append(propuesta == null ? "No registrada" : "\n" + propuesta);
        if (observacion != null) {
            sb.append("\nObservación: ").append(observacion);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%-9s %-30s %-14s %s", solicitud.getCodigo(),
                SolicitudConsulta.recortar(solicitud.getNombreSolicitante(), 30),
                solicitud.getEstado(), observacion == null ? "" : observacion);
    }
}
