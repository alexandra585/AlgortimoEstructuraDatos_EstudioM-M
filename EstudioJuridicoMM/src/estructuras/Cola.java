package estructuras;

import java.io.Serializable;

/**
 * Cola dinámica (FIFO: el primero en entrar es el primero en salir).
 * Sigue la estructura de la Cola dinámica vista en la Semana 12
 * (agregar, poll, peek, cantidadElementos, estaVacia, mostrarCola, limpiarCola),
 * pero es genérica (T) para poder guardar registros como SolicitudConsulta.
 * Con las referencias frente y fin, agregar, poll y peek son O(1).
 *
 * @param <T> tipo de los elementos
 */
public class Cola<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int cantidad;

    public Cola() {
        frente = null;
        fin = null;
        cantidad = 0;
    }

    /** Encolar: agrega un elemento al final de la cola. O(1) */
    public void agregar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            frente = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
        }
        fin = nuevoNodo;
        cantidad++;
    }

    /** Desencolar: retira y devuelve el elemento del frente. Devuelve null si está vacía. O(1) */
    public T poll() {
        if (!estaVacia()) {
            T elemento = frente.getDato();
            frente = frente.getSiguiente();
            cantidad--;
            if (frente == null) {
                fin = null; // si la cola queda vacía, también se actualiza el fin
            }
            return elemento;
        } else {
            return null;
        }
    }

    /** Ver frente: devuelve el elemento del frente sin retirarlo. O(1) */
    public T peek() {
        if (!estaVacia()) {
            return frente.getDato();
        } else {
            return null;
        }
    }

    public int cantidadElementos() {
        return cantidad;
    }

    public boolean estaVacia() {
        return cantidad == 0;
    }

    /** Devuelve el contenido de la cola, del frente al fin. */
    public String mostrarCola() {
        if (estaVacia()) {
            return "La cola está vacía.";
        }
        StringBuilder sb = new StringBuilder("Elementos en la cola:\n");
        Nodo<T> actual = frente;
        while (actual != null) {
            sb.append(actual.getDato()).append("\n");
            actual = actual.getSiguiente();
        }
        return sb.toString();
    }

    public void limpiarCola() {
        frente = null;
        fin = null;
        cantidad = 0;
    }

    /** Primer nodo de la cola; se usa para recorrerla sin modificarla. */
    public Nodo<T> getFrente() {
        return frente;
    }
}
