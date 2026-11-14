package modelo;

import java.io.Serializable;

/**
 * Persona natural o empresa registrada en la base de Clientes y Contrapartes.
 * Si esContraparte = true, es la parte contraria de algún caso del estudio.
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;          // DNI o RUC
    private String nombre;
    private String contacto;
    private boolean esContraparte;
    private int cantidadExpedientes;

    public Cliente(String id, String nombre, String contacto, boolean esContraparte) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.esContraparte = esContraparte;
        this.cantidadExpedientes = 0;
    }

    /** Suma un expediente al contador del cliente. */
    public void agregarExpediente() {
        cantidadExpedientes++;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public boolean isEsContraparte() { return esContraparte; }
    public int getCantidadExpedientes() { return cantidadExpedientes; }

    @Override
    public String toString() {
        return String.format("%-12s %-35s %-11s %d exp.", id, SolicitudConsulta.recortar(nombre, 35),
                esContraparte ? "CONTRAPARTE" : "CLIENTE", cantidadExpedientes);
    }
}
