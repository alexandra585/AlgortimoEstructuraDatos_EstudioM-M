package servicio;

import estructuras.ArbolBinarioBusqueda;
import estructuras.Cola;
import estructuras.Nodo;
import java.io.Serializable;
import java.time.LocalDate;
import modelo.Cliente;
import modelo.Expediente;
import modelo.FichaConsulta;
import modelo.SolicitudConsulta;
import modelo.TipoMovimiento;

/**
 * Administra los expedientes y la base de Clientes y Contrapartes
 * mediante dos ÁRBOLES BINARIOS DE BÚSQUEDA:
 *  - arbolPorCodigo:  clave = código de expediente (EXP-2026-0001)
 *  - arbolPorCliente: clave = DNI/RUC del cliente o contraparte
 */
public class GestorExpedientes implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArbolBinarioBusqueda<String, Expediente> arbolPorCodigo;
    private ArbolBinarioBusqueda<String, Cliente> arbolPorCliente;
    private int contador;

    public GestorExpedientes() {
        arbolPorCodigo = new ArbolBinarioBusqueda<>();
        arbolPorCliente = new ArbolBinarioBusqueda<>();
        contador = 0;
    }

    /** Registra (o actualiza) un cliente o contraparte en el árbol de clientes. */
    public Cliente registrarCliente(String id, String nombre, String contacto, boolean esContraparte) {
        Cliente c = arbolPorCliente.buscar(id);
        if (c == null) {
            c = new Cliente(id, nombre, contacto, esContraparte);
            arbolPorCliente.agregar(id, c);
        }
        return c;
    }

    /**
     * RF-08: abre el expediente a partir de la ficha aceptada.
     * Inserta al cliente (y a la contraparte) en el árbol de clientes y el
     * expediente en el árbol por código. Crea su historial con el hito de apertura.
     */
    public Expediente abrirExpediente(FichaConsulta ficha, LocalDate fechaApertura) {
        SolicitudConsulta s = ficha.getSolicitud();
        Cliente cliente = registrarCliente(s.getIdSolicitante(), s.getNombreSolicitante(), s.getContacto(), false);
        Cliente contraparte = null;
        if (ficha.tieneContraparte()) {
            contraparte = registrarCliente(ficha.getIdContraparte(), ficha.getNombreContraparte(), "-", true);
        }
        contador++;
        String codigo = String.format("EXP-%d-%04d", fechaApertura.getYear(), contador);
        Expediente exp = new Expediente(codigo, cliente, contraparte, s.getEspecialidad(), s.getMotivo(), fechaApertura);
        exp.registrarMovimiento(fechaApertura, TipoMovimiento.OTRO,
                "Apertura de expediente. Contrato de representación firmado", "CONTRATO-" + codigo);
        cliente.agregarExpediente();
        if (contraparte != null) {
            contraparte.agregarExpediente();
        }
        arbolPorCodigo.agregar(codigo, exp);
        return exp;
    }

    /** RF-14: búsqueda por código en el ABB. O(log n) promedio. */
    public Expediente buscarPorCodigo(String codigo) {
        return arbolPorCodigo.buscar(codigo.trim().toUpperCase());
    }

    /** Búsqueda de un cliente/contraparte por DNI/RUC en el ABB. */
    public Cliente buscarCliente(String id) {
        return arbolPorCliente.buscar(id.trim());
    }

    /**
     * RF-15: expedientes de un cliente. Primero se valida el cliente en el árbol
     * de clientes y luego se recorre el árbol de expedientes en inorden.
     */
    public Cola<Expediente> buscarPorCliente(String id) {
        Cola<Expediente> resultado = new Cola<>();
        if (buscarCliente(id) == null) {
            return resultado;
        }
        Nodo<Expediente> aux = arbolPorCodigo.mostrarElementos().getFrente();
        while (aux != null) {
            Expediente e = aux.getDato();
            boolean esCliente = e.getCliente().getId().equals(id.trim());
            boolean esContraparte = e.getContraparte() != null && e.getContraparte().getId().equals(id.trim());
            if (esCliente || esContraparte) {
                resultado.agregar(e);
            }
            aux = aux.getSiguiente();
        }
        return resultado;
    }

    /** RF-16: expedientes ordenados por código (recorrido inorden). */
    public Cola<Expediente> listarOrdenados() {
        return arbolPorCodigo.mostrarElementos();
    }

    /** Clientes y contrapartes ordenados por DNI/RUC (recorrido inorden). */
    public Cola<Cliente> listarClientes() {
        return arbolPorCliente.mostrarElementos();
    }

    public ArbolBinarioBusqueda<String, Expediente> getArbolPorCodigo() { return arbolPorCodigo; }
    public ArbolBinarioBusqueda<String, Cliente> getArbolPorCliente() { return arbolPorCliente; }
}
