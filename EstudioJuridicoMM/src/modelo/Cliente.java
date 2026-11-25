package modelo;

import estructuras.ListaEnlazada;
import java.io.Serializable;

/**
 * Persona natural o empresa registrada en la base de Clientes y Contrapartes.
 * Si esContraparte = true, es la parte contraria de algún caso del estudio.
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = 2L;

    private String id;          // DNI o RUC
    private String nombre;
    private String contacto;
    private boolean esContraparte;
    private ListaEnlazada<Expediente> expedientes;   // expedientes donde participa

    public Cliente(String id, String nombre, String contacto, boolean esContraparte) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.esContraparte = esContraparte;
        this.expedientes = new ListaEnlazada<>();
    }

    /** Agrega el expediente a la lista enlazada del cliente (al final). */
    public void agregarExpediente(Expediente expediente) {
        expedientes.agregarFinal(expediente);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public boolean isEsContraparte() { return esContraparte; }
    public int getCantidadExpedientes() { return expedientes.cantidadElementos(); }
    public ListaEnlazada<Expediente> getExpedientes() { return expedientes; }

    @Override
    public String toString() {
        return String.format("%-12s %-35s %-11s %d exp.", id, SolicitudConsulta.recortar(nombre, 35),
                esContraparte ? "CONTRAPARTE" : "CLIENTE", getCantidadExpedientes());
    }
}
