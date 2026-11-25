package servicio;

import estructuras.Criterio;
import java.io.Serializable;
import modelo.Expediente;
import modelo.MovimientoLegal;

/** Criterios para ordenar los expedientes con merge sort (RF-19). */
public final class CriteriosOrden {

    private CriteriosOrden() {
    }

    /** Nombres que se muestran en la interfaz, en el mismo orden que porIndice(). */
    public static final String[] NOMBRES = {
        "Actividad más reciente (fecha del último movimiento)",
        "Fecha de apertura (más antiguos primero)",
        "Nombre del cliente (A-Z)",
        "Especialidad (A-Z)"
    };

    public static Criterio<Expediente> porIndice(int i) {
        switch (i) {
            case 0: return POR_ULTIMO_MOVIMIENTO;
            case 1: return POR_FECHA_APERTURA;
            case 2: return POR_CLIENTE;
            default: return POR_ESPECIALIDAD;
        }
    }

    /** Más reciente primero; un expediente sin movimientos va al final. */
    public static final Criterio<Expediente> POR_ULTIMO_MOVIMIENTO = new CriterioSerializable() {
        @Override
        public int comparar(Expediente a, Expediente b) {
            MovimientoLegal ma = a.obtenerUltimoMovimiento();
            MovimientoLegal mb = b.obtenerUltimoMovimiento();
            if (ma == null || mb == null) {
                return ma == null ? (mb == null ? 0 : 1) : -1;
            }
            return mb.getFecha().compareTo(ma.getFecha());
        }
    };

    public static final Criterio<Expediente> POR_FECHA_APERTURA = new CriterioSerializable() {
        @Override
        public int comparar(Expediente a, Expediente b) {
            return a.getFechaApertura().compareTo(b.getFechaApertura());
        }
    };

    public static final Criterio<Expediente> POR_CLIENTE = new CriterioSerializable() {
        @Override
        public int comparar(Expediente a, Expediente b) {
            return a.getCliente().getNombre().compareToIgnoreCase(b.getCliente().getNombre());
        }
    };

    public static final Criterio<Expediente> POR_ESPECIALIDAD = new CriterioSerializable() {
        @Override
        public int comparar(Expediente a, Expediente b) {
            return a.getEspecialidad().compareToIgnoreCase(b.getEspecialidad());
        }
    };

    private abstract static class CriterioSerializable implements Criterio<Expediente>, Serializable {
        private static final long serialVersionUID = 1L;
    }
}
