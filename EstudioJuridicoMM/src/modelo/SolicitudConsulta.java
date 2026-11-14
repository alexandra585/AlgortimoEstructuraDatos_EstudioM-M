package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Solicitud de consulta que llega desde la Landing Page y se encola en orden de llegada. */
public class SolicitudConsulta implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombreSolicitante;
    private String idSolicitante;     // DNI (8 dígitos) o RUC (11 dígitos)
    private String contacto;
    private String motivo;
    private String especialidad;
    private LocalDateTime fechaHoraRegistro;
    private EstadoSolicitud estado;

    public SolicitudConsulta(String codigo, String nombreSolicitante, String idSolicitante,
            String contacto, String motivo, String especialidad, LocalDateTime fechaHoraRegistro) {
        this.codigo = codigo;
        this.nombreSolicitante = nombreSolicitante;
        this.idSolicitante = idSolicitante;
        this.contacto = contacto;
        this.motivo = motivo;
        this.especialidad = especialidad;
        this.fechaHoraRegistro = fechaHoraRegistro;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    public void cambiarEstado(EstadoSolicitud nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String getCodigo() { return codigo; }
    public String getNombreSolicitante() { return nombreSolicitante; }
    public String getIdSolicitante() { return idSolicitante; }
    public String getContacto() { return contacto; }
    public String getMotivo() { return motivo; }
    public String getEspecialidad() { return especialidad; }
    public LocalDateTime getFechaHoraRegistro() { return fechaHoraRegistro; }
    public EstadoSolicitud getEstado() { return estado; }

    /** Detalle completo de la solicitud. */
    public String detalle() {
        return "Código: " + codigo
                + "\nSolicitante: " + nombreSolicitante
                + "\nDNI/RUC: " + idSolicitante
                + "\nContacto: " + contacto
                + "\nMotivo: " + motivo
                + "\nEspecialidad: " + especialidad
                + "\nRegistro: " + Fechas.texto(fechaHoraRegistro)
                + "\nEstado: " + estado;
    }

    @Override
    public String toString() {
        return String.format("%-9s %-30s %-16s %s", codigo, recortar(nombreSolicitante, 30),
                Fechas.texto(fechaHoraRegistro), recortar(motivo, 40));
    }

    static String recortar(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
