package servicio;

import estructuras.Cola;
import java.io.Serializable;
import java.time.LocalDateTime;
import modelo.EstadoSolicitud;
import modelo.SolicitudConsulta;

/**
 * Proceso 1 - Recepción de Consultas.
 * Administra la COLA de solicitudes pendientes: se atienden estrictamente
 * en orden de llegada (FIFO).
 */
public class GestorSolicitudes implements Serializable {

    private static final long serialVersionUID = 1L;

    private Cola<SolicitudConsulta> cola;
    private int contador;

    public GestorSolicitudes() {
        cola = new Cola<>();
        contador = 0;
    }

    /** RF-01: crea la solicitud con código y fecha/hora y la encola al final. */
    public SolicitudConsulta registrarSolicitud(String nombre, String id, String contacto,
            String motivo, String especialidad) {
        return registrarSolicitud(nombre, id, contacto, motivo, especialidad, LocalDateTime.now());
    }

    /** Variante con fecha/hora explícita (se usa para la carga de datos iniciales). */
    public SolicitudConsulta registrarSolicitud(String nombre, String id, String contacto,
            String motivo, String especialidad, LocalDateTime fechaHora) {
        contador++;
        String codigo = String.format("SOL-%04d", contador);
        SolicitudConsulta s = new SolicitudConsulta(codigo, nombre, id, contacto, motivo, especialidad, fechaHora);
        cola.agregar(s);
        return s;
    }

    /** RF-03: retira únicamente la solicitud del frente y la pasa a EN_EVALUACION. */
    public SolicitudConsulta atenderSiguiente() {
        SolicitudConsulta s = cola.poll();
        if (s != null) {
            s.cambiarEstado(EstadoSolicitud.EN_EVALUACION);
        }
        return s;
    }

    /** RF-04: consulta la solicitud del frente sin retirarla. */
    public SolicitudConsulta verSiguiente() {
        return cola.peek();
    }

    /** RF-02: listado de pendientes en orden de llegada. */
    public Cola<SolicitudConsulta> listarPendientes() {
        return cola;
    }

    public int cantidadPendientes() {
        return cola.cantidadElementos();
    }
}
