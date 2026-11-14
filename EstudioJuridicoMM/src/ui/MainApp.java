package ui;

import persistencia.ServicioPersistencia;
import servicio.SistemaEstudio;

/**
 * Punto de entrada del aplicativo del Estudio Jurídico Magallanes - Montero (M&M).
 * Carga los datos desde el archivo local (o los datos iniciales si no existe)
 * y abre el menú principal.
 */
public class MainApp {

    public static final String ARCHIVO_DATOS = "datos_estudio_mm.dat";

    public static void main(String[] args) {
        ServicioPersistencia persistencia = new ServicioPersistencia(ARCHIVO_DATOS);
        SistemaEstudio sistema = persistencia.cargarTodo();
        VentanaPrincipalController ventana = new VentanaPrincipalController(sistema, persistencia);
        ventana.iniciar();
        Dialogos.cerrar();
        System.exit(0);
    }
}
