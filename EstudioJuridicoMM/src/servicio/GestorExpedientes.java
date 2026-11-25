package servicio;

import estructuras.ArbolBinarioBusqueda;
import estructuras.Cola;
import estructuras.Criterio;
import estructuras.ListaEnlazada;
import estructuras.Ordenamiento;
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
        cliente.agregarExpediente(exp);
        if (contraparte != null) {
            contraparte.agregarExpediente(exp);
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
     * RF-15: expedientes de un cliente o contraparte. Se busca a la persona en el
     * árbol de clientes y se devuelve su lista enlazada de expedientes.
     */
    public ListaEnlazada<Expediente> buscarPorCliente(String id) {
        Cliente c = buscarCliente(id);
        return c == null ? new ListaEnlazada<>() : c.getExpedientes();
    }

    /**
     * RF-19: expedientes ordenados por un criterio. Se copian a una lista enlazada
     * (recorrido en orden del árbol) y se ordenan con merge sort.
     */
    public ListaEnlazada<Expediente> listarOrdenadosPor(Criterio<Expediente> criterio) {
        ListaEnlazada<Expediente> lista = new ListaEnlazada<>();
        Nodo<Expediente> aux = arbolPorCodigo.mostrarElementos().getFrente();
        while (aux != null) {
            lista.agregarFinal(aux.getDato());
            aux = aux.getSiguiente();
        }
        Ordenamiento.mergeSort(lista, criterio);
        return lista;
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
