package servicio;

import java.io.Serializable;
import java.time.LocalDate;
import modelo.Expediente;
import modelo.MovimientoLegal;
import modelo.TipoMovimiento;

/**
 * Proceso 2 - Historial del expediente (PILA).
 * Opera sobre el expediente que se obtiene del árbol por código.
 */
public class GestorHistorial implements Serializable {

    private static final long serialVersionUID = 1L;

    private GestorExpedientes gestorExpedientes;

    public GestorHistorial(GestorExpedientes gestorExpedientes) {
        this.gestorExpedientes = gestorExpedientes;
    }

    public Expediente obtenerExpediente(String codigo) {
        return gestorExpedientes.buscarPorCodigo(codigo);
    }

    /** RF-09 / RF-13: registra un movimiento (lo ubica en orden cronológico). */
    public MovimientoLegal agregarMovimiento(Expediente exp, LocalDate fecha, TipoMovimiento tipo,
            String descripcion, String documento) {
        return exp.registrarMovimiento(fecha, tipo, descripcion, documento);
    }

    /** RF-10: último movimiento = cima de la pila. */
    public MovimientoLegal consultarUltimo(Expediente exp) {
        return exp.obtenerUltimoMovimiento();
    }

    /** RF-11: anulación lógica del último movimiento. */
    public MovimientoLegal revertirUltimo(Expediente exp) {
        return exp.anularUltimoMovimiento();
    }

    /** RF-13: modifica un movimiento vigente. */
    public boolean modificarMovimiento(Expediente exp, int id, TipoMovimiento tipo,
            String descripcion, String documento) {
        MovimientoLegal m = exp.buscarMovimiento(id);
        if (m == null) {
            return false;
        }
        m.modificar(tipo, descripcion, documento);
        return true;
    }

    /** RF-12: historial vigente, del más reciente al más antiguo. */
    public MovimientoLegal[] listarHistorial(Expediente exp) {
        return exp.obtenerHistorial();
    }
}
