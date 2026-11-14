package modelo;

import estructuras.Nodo;
import estructuras.Pila;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Expediente de un caso. Su historial de movimientos se guarda en una PILA:
 * la cima es siempre el movimiento más reciente (consulta en O(1)).
 * Los movimientos anulados no se eliminan: pasan a una segunda pila de anulados.
 */
public class Expediente implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private Cliente cliente;
    private Cliente contraparte;
    private String especialidad;
    private String materia;
    private LocalDate fechaApertura;
    private EstadoExpediente estado;
    private Pila<MovimientoLegal> historial;   // vigentes, cima = más reciente
    private Pila<MovimientoLegal> anulados;    // anulados, cima = último anulado
    private int contadorMovimientos;

    public Expediente(String codigo, Cliente cliente, Cliente contraparte, String especialidad,
            String materia, LocalDate fechaApertura) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.contraparte = contraparte;
        this.especialidad = especialidad;
        this.materia = materia;
        this.fechaApertura = fechaApertura;
        this.estado = EstadoExpediente.ABIERTO;
        this.historial = new Pila<>();
        this.anulados = new Pila<>();
        this.contadorMovimientos = 0;
    }

    /**
     * Registra un movimiento. Si su fecha es igual o posterior a la cima, se apila
     * directamente (O(1)). Si es anterior (movimiento intermedio que se registró tarde),
     * se usa una pila auxiliar para ubicarlo en su posición cronológica y luego se
     * devuelven los elementos, de modo que la cima siga siendo el más reciente.
     */
    public MovimientoLegal registrarMovimiento(LocalDate fecha, TipoMovimiento tipo,
            String descripcion, String documento) {
        contadorMovimientos++;
        MovimientoLegal mov = new MovimientoLegal(contadorMovimientos, fecha, tipo, descripcion, documento);
        Pila<MovimientoLegal> auxiliar = new Pila<>();
        while (!historial.estaVacia() && historial.peek().getFecha().isAfter(fecha)) {
            auxiliar.push(historial.pop());
        }
        historial.push(mov);
        while (!auxiliar.estaVacia()) {
            historial.push(auxiliar.pop());
        }
        return mov;
    }

    /** Indica si un movimiento con esa fecha quedaría en una posición intermedia. */
    public boolean esFechaIntermedia(LocalDate fecha) {
        return !historial.estaVacia() && historial.peek().getFecha().isAfter(fecha);
    }

    /** Movimiento más reciente (cima de la pila). O(1) */
    public MovimientoLegal obtenerUltimoMovimiento() {
        return historial.peek();
    }

    /** Desapila el último movimiento, lo marca como ANULADO y lo guarda en la pila de anulados. */
    public MovimientoLegal anularUltimoMovimiento() {
        MovimientoLegal mov = historial.pop();
        if (mov != null) {
            mov.anular();
            anulados.push(mov);
        }
        return mov;
    }

    /** Busca un movimiento vigente por su id recorriendo la pila desde la cima. */
    public MovimientoLegal buscarMovimiento(int id) {
        Nodo<MovimientoLegal> aux = historial.getCima();
        while (aux != null) {
            if (aux.getDato().getId() == id) {
                return aux.getDato();
            }
            aux = aux.getSiguiente();
        }
        return null;
    }

    /** Historial vigente como arreglo, de la cima (más reciente) a la base (más antiguo). */
    public MovimientoLegal[] obtenerHistorial() {
        MovimientoLegal[] arreglo = new MovimientoLegal[historial.cantidadElementos()];
        Nodo<MovimientoLegal> aux = historial.getCima();
        int i = 0;
        while (aux != null) {
            arreglo[i++] = aux.getDato();
            aux = aux.getSiguiente();
        }
        return arreglo;
    }

    public void cerrar() {
        estado = EstadoExpediente.CERRADO;
    }

    public void reabrir() {
        estado = EstadoExpediente.ABIERTO;
    }

    public String getCodigo() { return codigo; }
    public Cliente getCliente() { return cliente; }
    public Cliente getContraparte() { return contraparte; }
    public String getEspecialidad() { return especialidad; }
    public String getMateria() { return materia; }
    public LocalDate getFechaApertura() { return fechaApertura; }
    public EstadoExpediente getEstado() { return estado; }
    public Pila<MovimientoLegal> getHistorial() { return historial; }
    public Pila<MovimientoLegal> getAnulados() { return anulados; }

    public String cabecera() {
        return "Expediente: " + codigo + "   [" + estado + "]"
                + "\nCliente: " + cliente.getNombre() + " (" + cliente.getId() + ")"
                + "\nContraparte: " + (contraparte == null ? "-" : contraparte.getNombre() + " (" + contraparte.getId() + ")")
                + "\nEspecialidad: " + especialidad + "   Apertura: " + Fechas.texto(fechaApertura)
                + "\nMateria: " + materia;
    }

    @Override
    public String toString() {
        return String.format("%-14s %-28s %-11s %s  %-8s", codigo,
                SolicitudConsulta.recortar(cliente.getNombre(), 28), especialidad,
                Fechas.texto(fechaApertura), estado);
    }
}
