package ui;

import persistencia.ServicioPersistencia;
import servicio.SistemaEstudio;

/** Menú principal: navegación entre los módulos del sistema y mensajes globales. */
public class VentanaPrincipalController {

    private SistemaEstudio sistema;
    private ServicioPersistencia persistencia;

    public VentanaPrincipalController(SistemaEstudio sistema, ServicioPersistencia persistencia) {
        this.sistema = sistema;
        this.persistencia = persistencia;
    }

    public void iniciar() {
        int opcion;
        do {
            String menu = "ESTUDIO JURÍDICO MAGALLANES - MONTERO (M&M)\n"
                    + Tema.t("Solicitudes en cola: ", "Solicitudes pendientes: ") + sistema.getGestorSolicitudes().cantidadPendientes()
                    + "   |   En evaluación: " + sistema.getEvaluadorCasos().getEnEvaluacion().cantidadElementos()
                    + "   |   Expedientes: " + sistema.getGestorExpedientes().getArbolPorCodigo().cantidadElementos()
                    + "\n\nSeleccione una opción:\n"
                    + "1. Solicitudes de consulta" + Tema.t(" (Cola)", "") + "\n"
                    + "2. Evaluación y apertura de caso" + Tema.t(" (Árbol)", "") + "\n"
                    + "3. Expediente e historial" + Tema.t(" (Pila)", "") + "\n"
                    + "4. Búsqueda de expedientes" + Tema.t(" (Árbol)", "") + "\n"
                    + "5. Guardar datos\n"
                    + "6. Restaurar datos iniciales de prueba\n"
                    + "7. Salir";
            opcion = Dialogos.leerOpcion(menu, Dialogos.TITULO);
            switch (opcion) {
                case 1:
                    new PanelSolicitudesController(sistema).mostrar();
                    guardarSilencioso();
                    break;
                case 2:
                    new PanelEvaluacionController(sistema).mostrar();
                    guardarSilencioso();
                    break;
                case 3:
                    new PanelExpedienteController(sistema).mostrar();
                    guardarSilencioso();
                    break;
                case 4:
                    new PanelBusquedaController(sistema).mostrar();
                    guardarSilencioso();
                    break;
                case 5:
                    if (persistencia.guardarTodo(sistema)) {
                        Dialogos.info("Datos guardados en:\n" + persistencia.getRutaArchivos(), Dialogos.TITULO);
                    } else {
                        Dialogos.error("No se pudieron guardar los datos.");
                    }
                    break;
                case 6:
                    if (Dialogos.confirmar("Se perderán los cambios y se cargarán los datos de prueba.\n¿Continuar?",
                            Dialogos.TITULO)) {
                        sistema = persistencia.restaurarDatosIniciales();
                        Dialogos.info("Datos iniciales restaurados.", Dialogos.TITULO);
                    }
                    break;
                case 7:
                case -1:
                    if (Dialogos.confirmar("¿Desea salir del sistema?", Dialogos.TITULO)) {
                        persistencia.guardarTodo(sistema);
                        Dialogos.info("Datos guardados. Cerrando el aplicativo...", Dialogos.TITULO);
                        opcion = 7;
                    } else {
                        opcion = 0;
                    }
                    break;
                default:
                    Dialogos.error("Opción no válida.");
            }
        } while (opcion != 7);
    }

    private void guardarSilencioso() {
        persistencia.guardarTodo(sistema);
    }
}
