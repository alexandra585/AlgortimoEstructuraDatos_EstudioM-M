package estructuras;

/**
 * Algoritmo de ordenamiento por mezcla (merge sort) sobre la ListaEnlazada.
 * Es recursivo: divide la lista en dos mitades, ordena cada mitad y luego
 * las mezcla en orden. Cuesta O(n log n) y es estable (respeta el orden
 * original de los elementos iguales). Trabaja moviendo nodos, sin arreglos auxiliares.
 */
public final class Ordenamiento {

    private Ordenamiento() {
    }

    /** Ordena la lista según el criterio indicado. */
    public static <T> void mergeSort(ListaEnlazada<T> lista, Criterio<T> criterio) {
        lista.setInicio(ordenar(lista.getInicio(), criterio));
    }

    /** Paso recursivo: caso base = 0 o 1 nodo. */
    private static <T> Nodo<T> ordenar(Nodo<T> cabeza, Criterio<T> criterio) {
        if (cabeza == null || cabeza.getSiguiente() == null) {
            return cabeza;
        }
        Nodo<T> mitad = obtenerMitad(cabeza);
        Nodo<T> segunda = mitad.getSiguiente();
        mitad.setSiguiente(null);               // divide la lista en dos
        Nodo<T> izquierda = ordenar(cabeza, criterio);
        Nodo<T> derecha = ordenar(segunda, criterio);
        return mezclar(izquierda, derecha, criterio);
    }

    /** Encuentra el nodo central con dos punteros (lento avanza 1, rápido avanza 2). */
    private static <T> Nodo<T> obtenerMitad(Nodo<T> cabeza) {
        Nodo<T> lento = cabeza;
        Nodo<T> rapido = cabeza.getSiguiente();
        while (rapido != null && rapido.getSiguiente() != null) {
            lento = lento.getSiguiente();
            rapido = rapido.getSiguiente().getSiguiente();
        }
        return lento;
    }

    /** Mezcla dos listas ya ordenadas en una sola lista ordenada. */
    private static <T> Nodo<T> mezclar(Nodo<T> a, Nodo<T> b, Criterio<T> criterio) {
        Nodo<T> centinela = new Nodo<>(null);
        Nodo<T> cola = centinela;
        while (a != null && b != null) {
            if (criterio.comparar(a.getDato(), b.getDato()) <= 0) {
                cola.setSiguiente(a);
                a = a.getSiguiente();
            } else {
                cola.setSiguiente(b);
                b = b.getSiguiente();
            }
            cola = cola.getSiguiente();
        }
        cola.setSiguiente(a != null ? a : b);
        return centinela.getSiguiente();
    }
}
