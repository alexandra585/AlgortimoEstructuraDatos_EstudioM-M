package estructuras;

import java.io.Serializable;

/**
 * Pila dinámica (LIFO: el último en entrar es el primero en salir).
 * Sigue la estructura de la Pila dinámica vista en la Semana 11
 * (push, pop, peek, estaVacia, cantidadElementos, mostrarPila, limpiarPila),
 * pero es genérica (T) para poder guardar registros como MovimientoLegal.
 * Todas las operaciones se hacen sobre la cima, por eso son O(1).
 *
 * @param <T> tipo de los elementos
 */
public class Pila<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Nodo<T> cima;
    private int tamaño;

    public Pila() {
        this.cima = null;
        this.tamaño = 0;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    /** Apilar: coloca un elemento en la cima. O(1) */
    public void push(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        nuevoNodo.setSiguiente(cima);
        cima = nuevoNodo;
        tamaño++;
    }

    /** Desapilar: retira y devuelve el elemento de la cima. Devuelve null si está vacía. O(1) */
    public T pop() {
        if (!estaVacia()) {
            T elemento = cima.getDato();
            cima = cima.getSiguiente();
            tamaño--;
            return elemento;
        } else {
            return null;
        }
    }

    /** Ver cima: devuelve el elemento de la cima sin retirarlo. O(1) */
    public T peek() {
        if (!estaVacia()) {
            return cima.getDato();
        } else {
            return null;
        }
    }

    public int cantidadElementos() {
        return tamaño;
    }

    /** Devuelve el contenido de la pila, de la cima a la base. */
    public String mostrarPila() {
        if (!estaVacia()) {
            StringBuilder contenido = new StringBuilder("Contenido de la pila:\n");
            Nodo<T> actual = cima;
            while (actual != null) {
                contenido.append(actual.getDato()).append("\n");
                actual = actual.getSiguiente();
            }
            return contenido.toString();
        } else {
            return "La pila está vacía.";
        }
    }

    public void limpiarPila() {
        cima = null;
        tamaño = 0;
    }

    /** Nodo de la cima; se usa para recorrer la pila sin modificarla. */
    public Nodo<T> getCima() {
        return cima;
    }
}
