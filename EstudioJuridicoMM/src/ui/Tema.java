package ui;

/**
 * Controla si la interfaz muestra dónde se aplica cada tema del curso
 * (Cola, Pila, Árbol binario de búsqueda).
 *
 *  - MOSTRAR_TEMAS = true  : versión de estudio / sustentación técnica
 *                            (indica cola, pila, cima, recorrido en orden, etc.).
 *  - MOSTRAR_TEMAS = false : versión limpia para el usuario final y la presentación.
 *
 * Solo hay que cambiar este valor; el resto del programa es el mismo.
 */
public final class Tema {

    public static final boolean MOSTRAR_TEMAS = true;

    private Tema() {
    }

    /** Devuelve el texto técnico si MOSTRAR_TEMAS es true, o el texto limpio si es false. */
    public static String t(String conTema, String sinTema) {
        return MOSTRAR_TEMAS ? conTema : sinTema;
    }
}
