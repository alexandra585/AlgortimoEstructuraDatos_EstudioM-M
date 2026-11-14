package modelo;

import java.io.Serializable;
import java.time.LocalDate;

/** Movimiento (hito) del expediente: audiencia, escrito, resolución, notificación, etc. */
public class MovimientoLegal implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDate fecha;
    private TipoMovimiento tipo;
    private String descripcion;
    private String documentoReferencia;
    private boolean anulado;

    public MovimientoLegal(int id, LocalDate fecha, TipoMovimiento tipo, String descripcion,
            String documentoReferencia) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.documentoReferencia = documentoReferencia;
        this.anulado = false;
    }

    /** Anulación lógica: el movimiento no se borra, solo se marca. */
    public void anular() {
        this.anulado = true;
    }

    /** Modifica los datos del movimiento (la fecha no cambia para no alterar la cronología). */
    public void modificar(TipoMovimiento tipo, String descripcion, String documentoReferencia) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.documentoReferencia = documentoReferencia;
    }

    public int getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public TipoMovimiento getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public String getDocumentoReferencia() { return documentoReferencia; }
    public boolean isAnulado() { return anulado; }

    public String detalle() {
        return "ID: " + id
                + "\nFecha: " + Fechas.texto(fecha)
                + "\nTipo: " + tipo
                + "\nDescripción: " + descripcion
                + "\nDocumento: " + (documentoReferencia.isEmpty() ? "-" : documentoReferencia)
                + (anulado ? "\n*** ANULADO ***" : "");
    }

    @Override
    public String toString() {
        return String.format("#%-3d %s  %-12s %s%s", id, Fechas.texto(fecha), tipo,
                SolicitudConsulta.recortar(descripcion, 50),
                documentoReferencia.isEmpty() ? "" : " [" + documentoReferencia + "]");
    }
}
