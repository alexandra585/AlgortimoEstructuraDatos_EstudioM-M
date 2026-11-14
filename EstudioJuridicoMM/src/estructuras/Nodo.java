package estructuras;

import java.io.Serializable;

/**
 * Nodo genérico de enlace simple. Es la unidad de almacenamiento
 * de la Cola y de la Pila dinámicas.
 *
 * @param <T> tipo del dato que guarda el nodo
 */
public class Nodo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T dato;
    private Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Nodo(T dato, Nodo<T> siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
