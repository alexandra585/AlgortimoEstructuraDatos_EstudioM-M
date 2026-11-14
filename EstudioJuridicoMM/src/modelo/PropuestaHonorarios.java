package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/** Cotización de servicios presentada al cliente. El cliente tiene 5 días para responder. */
public class PropuestaHonorarios implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int PLAZO_DIAS = 5;

    private double monto;
    private String descripcion;
    private LocalDate fechaEmision;
    private int plazoDias = PLAZO_DIAS;
    private Boolean aceptada;          // null = sin respuesta
    private LocalDate fechaRespuesta;

    public PropuestaHonorarios(double monto, String descripcion, LocalDate fechaEmision) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.fechaEmision = fechaEmision;
        this.aceptada = null;
    }

    public LocalDate getFechaLimite() {
        return fechaEmision.plusDays(plazoDias);
    }

    /** Indica si a la fecha dada ya venció el plazo de respuesta. */
    public boolean estaVencida(LocalDate fecha) {
        return fecha.isAfter(getFechaLimite());
    }

    public boolean estaVencida() {
        return estaVencida(LocalDate.now());
    }

    public void registrarDecision(boolean acepta, LocalDate fecha) {
        this.aceptada = acepta;
        this.fechaRespuesta = fecha;
    }

    public double getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public int getPlazoDias() { return plazoDias; }
    public Boolean getAceptada() { return aceptada; }
    public LocalDate getFechaRespuesta() { return fechaRespuesta; }

    @Override
    public String toString() {
        String dec = (aceptada == null) ? "Sin respuesta" : (aceptada ? "ACEPTADA" : "NO ACEPTADA");
        return String.format("S/ %.2f - %s%nEmitida: %s | Vence: %s | Decisión: %s",
                monto, descripcion, Fechas.texto(fechaEmision), Fechas.texto(getFechaLimite()), dec);
    }
}
