package estructuras;

import java.io.Serializable;

/**
 * Par clave - valor que se guarda en las cubetas de la tabla hash.
 *
 * @param <K> tipo de la clave
 * @param <V> tipo del valor
 */
public class EntradaHash<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private K clave;
    private V valor;

    public EntradaHash(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public K getClave() {
        return clave;
    }

    public V getValor() {
        return valor;
    }

    public void setValor(V valor) {
        this.valor = valor;
    }
}
