package estructuras;

import java.io.Serializable;

/**
 * Tabla hash genérica con encadenamiento separado.
 * Cada posición del arreglo (cubeta) es una ListaEnlazada de entradas clave-valor;
 * las claves que caen en la misma posición (colisiones) se encadenan en esa lista.
 * Cuando el factor de carga supera 0,75 se duplica la capacidad (rehash).
 * Insertar y buscar cuestan O(1) en promedio.
 *
 * @param <K> tipo de la clave
 * @param <V> tipo del valor
 */
public class TablaHash<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final double FACTOR_CARGA_MAXIMO = 0.75;

    private ListaEnlazada<EntradaHash<K, V>>[] cubetas;
    private int cantidad;

    public TablaHash() {
        this(16);
    }

    public TablaHash(int capacidadInicial) {
        cubetas = crearCubetas(Math.max(capacidadInicial, 4));
        cantidad = 0;
    }

    @SuppressWarnings("unchecked")
    private ListaEnlazada<EntradaHash<K, V>>[] crearCubetas(int capacidad) {
        ListaEnlazada<EntradaHash<K, V>>[] nuevas = new ListaEnlazada[capacidad];
        for (int i = 0; i < capacidad; i++) {
            nuevas[i] = new ListaEnlazada<>();
        }
        return nuevas;
    }

    /** Función hash: convierte la clave en una posición válida del arreglo. */
    private int posicion(K clave, int capacidad) {
        return (clave.hashCode() & 0x7fffffff) % capacidad;
    }

    /** Busca la entrada de una clave dentro de su cubeta (búsqueda secuencial). */
    private EntradaHash<K, V> buscarEntrada(K clave) {
        Nodo<EntradaHash<K, V>> aux = cubetas[posicion(clave, cubetas.length)].getInicio();
        while (aux != null) {
            if (aux.getDato().getClave().equals(clave)) {
                return aux.getDato();
            }
            aux = aux.getSiguiente();
        }
        return null;
    }

    /** Inserta la clave con su valor. Si la clave ya existe, actualiza el valor. */
    public void insertar(K clave, V valor) {
        EntradaHash<K, V> existente = buscarEntrada(clave);
        if (existente != null) {
            existente.setValor(valor);
            return;
        }
        cubetas[posicion(clave, cubetas.length)].agregarFinal(new EntradaHash<>(clave, valor));
        cantidad++;
        if ((double) cantidad / cubetas.length > FACTOR_CARGA_MAXIMO) {
            redimensionar();
        }
    }

    /** Devuelve el valor asociado a la clave, o null si no existe. O(1) promedio */
    public V buscar(K clave) {
        EntradaHash<K, V> e = buscarEntrada(clave);
        return e == null ? null : e.getValor();
    }

    public boolean contiene(K clave) {
        return buscarEntrada(clave) != null;
    }

    /** Elimina la clave de la tabla. Devuelve true si existía. */
    public boolean eliminar(K clave) {
        EntradaHash<K, V> e = buscarEntrada(clave);
        if (e == null) {
            return false;
        }
        cubetas[posicion(clave, cubetas.length)].eliminar(e);
        cantidad--;
        return true;
    }

    /** Rehash: duplica la capacidad y redistribuye todas las entradas. */
    private void redimensionar() {
        ListaEnlazada<EntradaHash<K, V>>[] anteriores = cubetas;
        cubetas = crearCubetas(anteriores.length * 2);
        for (ListaEnlazada<EntradaHash<K, V>> cubeta : anteriores) {
            Nodo<EntradaHash<K, V>> aux = cubeta.getInicio();
            while (aux != null) {
                EntradaHash<K, V> e = aux.getDato();
                cubetas[posicion(e.getClave(), cubetas.length)].agregarFinal(e);
                aux = aux.getSiguiente();
            }
        }
    }

    public int cantidadElementos() {
        return cantidad;
    }

    public int getCapacidad() {
        return cubetas.length;
    }

    /** Cantidad de cubetas con más de una entrada (colisiones encadenadas). */
    public int cubetasConColision() {
        int n = 0;
        for (ListaEnlazada<EntradaHash<K, V>> c : cubetas) {
            if (c.cantidadElementos() > 1) {
                n++;
            }
        }
        return n;
    }
}
