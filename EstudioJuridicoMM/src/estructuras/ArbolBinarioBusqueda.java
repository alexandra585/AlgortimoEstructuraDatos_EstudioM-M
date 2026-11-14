package estructuras;

import java.io.Serializable;

/**
 * Árbol binario de búsqueda (ABB).
 * Sigue la estructura de la clase Arbol vista en la sesión de Árbol Binario:
 * agregar / agregarRecursivo, buscar / buscarRecursivo, eliminar / eliminarRecursivo,
 * encontrarMinimo y mostrarElementos / recorrerEnOrden.
 *
 * Diferencias con el ejemplo de clase (que usa números int):
 *  - Es genérico: ordena por una clave K (código de expediente o DNI/RUC) y guarda un valor V.
 *  - buscar devuelve el objeto encontrado (o null) en lugar de true/false.
 *  - El recorrido en orden se guarda en nuestra Cola, porque el proyecto no usa java.util.
 *
 * Regla: las claves menores van a la izquierda y las mayores a la derecha.
 * Buscar, agregar y eliminar cuestan O(log n) en promedio y O(n) en el peor caso.
 *
 * @param <K> tipo de la clave (debe ser comparable)
 * @param <V> tipo del valor guardado
 */
public class ArbolBinarioBusqueda<K extends Comparable<K>, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private NodoArbol<K, V> raiz;
    private int cantidad;

    public ArbolBinarioBusqueda() {
        this.raiz = null;
        this.cantidad = 0;
    }

    // ----------------------------------------------------------------- agregar
    /** Agrega una clave con su valor. Si la clave ya existe, no se agrega (igual que en clase). */
    public void agregar(K clave, V valor) {
        raiz = agregarRecursivo(raiz, clave, valor);
    }

    private NodoArbol<K, V> agregarRecursivo(NodoArbol<K, V> nodo, K clave, V valor) {
        if (nodo == null) {
            cantidad++;
            return new NodoArbol<>(clave, valor);
        }
        if (clave.compareTo(nodo.getClave()) < 0) {
            nodo.setIzquierda(agregarRecursivo(nodo.getIzquierda(), clave, valor));
        } else if (clave.compareTo(nodo.getClave()) > 0) {
            nodo.setDerecha(agregarRecursivo(nodo.getDerecha(), clave, valor));
        }
        return nodo;
    }

    // ------------------------------------------------------------------ buscar
    /** Devuelve el valor guardado con esa clave, o null si no existe. */
    public V buscar(K clave) {
        return buscarRecursivo(raiz, clave);
    }

    private V buscarRecursivo(NodoArbol<K, V> nodo, K clave) {
        if (nodo == null) {
            return null;
        }
        if (clave.compareTo(nodo.getClave()) == 0) {
            return nodo.getValor();
        }
        return clave.compareTo(nodo.getClave()) < 0
                ? buscarRecursivo(nodo.getIzquierda(), clave)
                : buscarRecursivo(nodo.getDerecha(), clave);
    }

    /** true si la clave está en el árbol (equivale al buscar booleano de clase). */
    public boolean existe(K clave) {
        return buscar(clave) != null;
    }

    // ---------------------------------------------------------------- eliminar
    public void eliminar(K clave) {
        if (existe(clave)) {
            raiz = eliminarRecursivo(raiz, clave);
            cantidad--;
        }
    }

    private NodoArbol<K, V> eliminarRecursivo(NodoArbol<K, V> nodo, K clave) {
        if (nodo == null) {
            return null;
        }
        if (clave.compareTo(nodo.getClave()) < 0) {
            nodo.setIzquierda(eliminarRecursivo(nodo.getIzquierda(), clave));
        } else if (clave.compareTo(nodo.getClave()) > 0) {
            nodo.setDerecha(eliminarRecursivo(nodo.getDerecha(), clave));
        } else {
            if (nodo.getIzquierda() == null) {
                return nodo.getDerecha();
            } else if (nodo.getDerecha() == null) {
                return nodo.getIzquierda();
            }
            // Dos hijos: se reemplaza por el menor del subárbol derecho
            NodoArbol<K, V> minimo = encontrarMinimo(nodo.getDerecha());
            nodo.setClave(minimo.getClave());
            nodo.setValor(minimo.getValor());
            nodo.setDerecha(eliminarRecursivo(nodo.getDerecha(), minimo.getClave()));
        }
        return nodo;
    }

    private NodoArbol<K, V> encontrarMinimo(NodoArbol<K, V> nodo) {
        return nodo.getIzquierda() == null ? nodo : encontrarMinimo(nodo.getIzquierda());
    }

    // ------------------------------------------------------ recorrido en orden
    /** Recorrido en orden (izquierda - raíz - derecha): valores ordenados por clave. */
    public Cola<V> mostrarElementos() {
        Cola<V> lista = new Cola<>();
        recorrerEnOrden(raiz, lista);
        return lista;
    }

    private void recorrerEnOrden(NodoArbol<K, V> nodo, Cola<V> lista) {
        if (nodo != null) {
            recorrerEnOrden(nodo.getIzquierda(), lista);
            lista.agregar(nodo.getValor());
            recorrerEnOrden(nodo.getDerecha(), lista);
        }
    }

    // ------------------------------------------------------------ información
    /** Altura del árbol (árbol vacío = 0). */
    public int altura() {
        return alturaRecursiva(raiz);
    }

    private int alturaRecursiva(NodoArbol<K, V> nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(alturaRecursiva(nodo.getIzquierda()), alturaRecursiva(nodo.getDerecha()));
    }

    public int cantidadElementos() {
        return cantidad;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public NodoArbol<K, V> getRaiz() {
        return raiz;
    }
}
