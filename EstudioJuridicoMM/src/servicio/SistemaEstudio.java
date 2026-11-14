package servicio;

import java.io.Serializable;

/**
 * Agrupa todos los gestores del sistema. Es el objeto que se guarda y
 * se carga desde el archivo local (RF-17).
 */
public class SistemaEstudio implements Serializable {

    private static final long serialVersionUID = 1L;

    private GestorSolicitudes gestorSolicitudes;
    private GestorExpedientes gestorExpedientes;
    private EvaluadorCasos evaluadorCasos;
    private GestorHistorial gestorHistorial;

    public SistemaEstudio() {
        gestorSolicitudes = new GestorSolicitudes();
        gestorExpedientes = new GestorExpedientes();
        evaluadorCasos = new EvaluadorCasos(gestorExpedientes);
        gestorHistorial = new GestorHistorial(gestorExpedientes);
    }

    public GestorSolicitudes getGestorSolicitudes() { return gestorSolicitudes; }
    public GestorExpedientes getGestorExpedientes() { return gestorExpedientes; }
    public EvaluadorCasos getEvaluadorCasos() { return evaluadorCasos; }
    public GestorHistorial getGestorHistorial() { return gestorHistorial; }
}
