package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Formatos de fecha usados en todo el sistema (dd/MM/yyyy). */
public final class Fechas {

    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Fechas() {
    }

    public static String texto(LocalDate f) {
        return f == null ? "-" : f.format(FECHA);
    }

    public static String texto(LocalDateTime f) {
        return f == null ? "-" : f.format(FECHA_HORA);
    }

    /** Convierte un texto dd/MM/yyyy a fecha. Devuelve null si el formato es inválido. */
    public static LocalDate leer(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FECHA);
        } catch (Exception e) {
            return null;
        }
    }
}
