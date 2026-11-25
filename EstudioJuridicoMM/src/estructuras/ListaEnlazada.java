package estructuras;

import java.io.Serializable;

/**
 * Lista enlazada simple genérica (Semanas 7 y 8).
 * Cada nodo guarda un dato y la referencia al siguiente. Se mantienen las
 * referencias al inicio y al fin para agregar al final en O(1).
 * Se usa para los expedientes de cada cliente y como "cubeta" de la tabla hash.
 *
 * @param <T> tipo de los elementos
 */
public class ListaEnlazada<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Nodo<T> inicio;
    private Nodo<T> fin;
    private int cantidad;

    public ListaEnlazada() {
        this.inicio = null;
        this.fin = null;
        this.cantidad = 0;
    }

    /** Agrega un elemento al inicio de la lista. O(1) */
    public void agregarInicio(T dato) {
        inicio = new Nodo<>(dato, inicio);
        if (fin == null) {
            fin = inicio;
        }
        cantidad++;
    }

    /** Agrega un elemento al final de la lista. O(1) */
    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            inicio = fin = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            fin = nuevo;
        }
        cantidad++;
    }

    /** Devuelve el elemento de la posición indicada (0 = primero), o null. O(n) */
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return null;
        }
        Nodo<T> aux = inicio;
        for (int i = 0; i < posicion; i++) {
            aux = aux.getSiguiente();
        }
        return aux.getDato();
    }

    /** Búsqueda secuencial: true si el dato está en la lista. O(n) */
    public boolean buscar(T dato) {
        Nodo<T> aux = inicio;
        while (aux != null) {
            if (aux.getDato().equals(dato)) {
                return true;
            }
            aux = aux.getSiguiente();
        }
        return false;
    }

    /** Elimina la primera aparición del dato. Devuelve true si lo eliminó. O(n) */
    public boolean eliminar(T dato) {
        Nodo<T> anterior = null;
        Nodo<T> aux = inicio;
        while (aux != null && !aux.getDato().equals(dato)) {
            anterior = aux;
            aux = aux.getSiguiente();
        }
        if (aux == null) {
            return false;
        }
        if (anterior == null) {
            inicio = aux.getSiguiente();
        } else {
            anterior.setSiguiente(aux.getSiguiente());
        }
        if (aux == fin) {
            fin = anterior;
        }
        cantidad--;
        return true;
    }

    public boolean estaVacia() {
        return inicio == null;
    }

    public int cantidadElementos() {
        return cantidad;
    }

    /** Primer nodo de la lista; se usa para recorrerla. */
    public Nodo<T> getInicio() {
        return inicio;
    }

    /** Reemplaza el primer nodo (lo usa el algoritmo de ordenamiento). */
    void setInicio(Nodo<T> nuevoInicio) {
        inicio = nuevoInicio;
        fin = nuevoInicio;
        if (fin != null) {
            while (fin.getSiguiente() != null) {
                fin = fin.getSiguiente();
            }
        }
    }

    /** Devuelve el contenido de la lista como texto. */
    public String mostrarLista() {
        if (estaVacia()) {
            return "La lista está vacía.";
        }
        StringBuilder sb = new StringBuilder();
        Nodo<T> aux = inicio;
        while (aux != null) {
            sb.append(aux.getDato()).append("\n");
            aux = aux.getSiguiente();
        }
        return sb.toString();
    }
}
