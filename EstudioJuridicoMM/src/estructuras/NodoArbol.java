package estructuras;

import java.io.Serializable;

/**
 * Nodo del árbol binario de búsqueda (igual al Nodo de la sesión de Árbol Binario:
 * un valor y las referencias izquierda y derecha). Se agrega una clave para
 * ordenar, porque el valor guardado es un objeto (Expediente o Cliente).
 *
 * @param <K> tipo de la clave (código de expediente o DNI/RUC)
 * @param <V> tipo del valor guardado
 */
public class NodoArbol<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private K clave;
    private V valor;
    private NodoArbol<K, V> izquierda;
    private NodoArbol<K, V> derecha;

    public NodoArbol(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
        this.izquierda = null;
        this.derecha = null;
    }

    public K getClave() {
        return clave;
    }

    public void setClave(K clave) {
        this.clave = clave;
    }

    public V getValor() {
        return valor;
    }

    public void setValor(V valor) {
        this.valor = valor;
    }

    public NodoArbol<K, V> getIzquierda() {
        return izquierda;
    }

    public void setIzquierda(NodoArbol<K, V> izquierda) {
        this.izquierda = izquierda;
    }

    public NodoArbol<K, V> getDerecha() {
        return derecha;
    }

    public void setDerecha(NodoArbol<K, V> derecha) {
        this.derecha = derecha;
    }
}
