package estructuras;

/**
 * Criterio de comparación para ordenar: devuelve un número negativo si a va
 * antes que b, cero si son equivalentes y positivo si a va después de b.
 * (Equivale a un comparador, sin usar java.util.)
 *
 * @param <T> tipo de los elementos a comparar
 */
public interface Criterio<T> {

    int comparar(T a, T b);
}
