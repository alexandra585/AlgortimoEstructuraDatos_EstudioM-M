package ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.JFrame;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.Fechas;

/**
 * Métodos de apoyo para la interfaz con JOptionPane:
 * lectura validada de datos y presentación de listados.
 */
public final class Dialogos {

    public static final String TITULO = "Estudio Jurídico M&M";

    /**
     * true: los cuadros de diálogo siempre aparecen delante de las demás ventanas
     * (evita que el menú quede escondido detrás de NetBeans).
     * Cambie a false si necesita trabajar con otras ventanas encima.
     */
    public static final boolean SIEMPRE_ADELANTE = true;

    private static JFrame ventanaPadre;

    // Botones de los JOptionPane en español
    static {
        javax.swing.UIManager.put("OptionPane.yesButtonText", "Sí");
        javax.swing.UIManager.put("OptionPane.noButtonText", "No");
        javax.swing.UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        javax.swing.UIManager.put("OptionPane.okButtonText", "Aceptar");
    }

    private Dialogos() {
    }

    /**
     * Ventana "dueña" de todos los JOptionPane. Es casi invisible (1x1 píxel, en el
     * centro de la pantalla), pero aparece en la barra de tareas con el nombre del
     * sistema y hace que los cuadros de diálogo se muestren adelante.
     */
    public static Frame padre() {
        if (ventanaPadre == null) {
            ventanaPadre = new JFrame(TITULO);
            ventanaPadre.setUndecorated(true);
            ventanaPadre.setSize(1, 1);
            ventanaPadre.setLocationRelativeTo(null);
            ventanaPadre.setAlwaysOnTop(SIEMPRE_ADELANTE);
            ventanaPadre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventanaPadre.setVisible(true);
        }
        ventanaPadre.toFront();
        return ventanaPadre;
    }

    /** Cierra la ventana dueña al salir del sistema. */
    public static void cerrar() {
        if (ventanaPadre != null) {
            ventanaPadre.dispose();
        }
    }

    /** Muestra un menú numerado y devuelve la opción elegida. -1 si se cancela, 0 si es inválida. */
    public static int leerOpcion(String menu, String titulo) {
        String r = JOptionPane.showInputDialog(padre(), menu, titulo, JOptionPane.QUESTION_MESSAGE);
        if (r == null) {
            return -1;
        }
        try {
            return Integer.parseInt(r.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** Lee un texto. Devuelve null si el usuario cancela. */
    public static String leerTexto(String mensaje, String titulo, boolean obligatorio) {
        return leerTexto(mensaje, titulo, obligatorio, "");
    }

    public static String leerTexto(String mensaje, String titulo, boolean obligatorio, String valorInicial) {
        while (true) {
            Object r = JOptionPane.showInputDialog(padre(), mensaje, titulo,
                    JOptionPane.QUESTION_MESSAGE, null, null, valorInicial);
            if (r == null) {
                return null;
            }
            String t = r.toString().trim();
            if (!obligatorio || !t.isEmpty()) {
                return t;
            }
            error("Este dato es obligatorio.");
        }
    }

    /** Lee un DNI (8 dígitos) o RUC (11 dígitos). Si no es obligatorio acepta vacío. */
    public static String leerIdentificador(String mensaje, String titulo, boolean obligatorio) {
        while (true) {
            String t = leerTexto(mensaje + "\n(DNI 8 dígitos o RUC 11 dígitos)", titulo, obligatorio);
            if (t == null) {
                return null;
            }
            if (t.isEmpty() && !obligatorio) {
                return t;
            }
            if (t.matches("\\d{8}") || t.matches("\\d{11}")) {
                return t;
            }
            error("Identificador inválido. Debe tener 8 dígitos (DNI) o 11 dígitos (RUC).");
        }
    }

    /** Lee una fecha dd/MM/yyyy. Devuelve null si se cancela. */
    public static LocalDate leerFecha(String mensaje, String titulo, LocalDate porDefecto) {
        while (true) {
            String t = leerTexto(mensaje + "\n(formato dd/MM/yyyy)", titulo, true, Fechas.texto(porDefecto));
            if (t == null) {
                return null;
            }
            LocalDate f = Fechas.leer(t);
            if (f != null) {
                return f;
            }
            error("Fecha inválida. Use el formato dd/MM/yyyy, por ejemplo 03/11/2026.");
        }
    }

    /** Lee un monto mayor que cero. Devuelve -1 si se cancela. */
    public static double leerMonto(String mensaje, String titulo) {
        while (true) {
            String t = leerTexto(mensaje, titulo, true);
            if (t == null) {
                return -1;
            }
            try {
                double m = Double.parseDouble(t.replace(",", "."));
                if (m > 0) {
                    return m;
                }
            } catch (NumberFormatException e) {
                // se vuelve a pedir
            }
            error("Ingrese un monto numérico mayor que 0.");
        }
    }

    /** Muestra una lista desplegable y devuelve el elemento elegido (null si cancela). */
    public static Object elegir(String mensaje, String titulo, Object[] opciones) {
        if (opciones.length == 0) {
            return null;
        }
        return JOptionPane.showInputDialog(padre(), mensaje, titulo, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
    }

    public static boolean confirmar(String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(padre(), mensaje, titulo,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static void info(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(padre(), mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(String mensaje) {
        JOptionPane.showMessageDialog(padre(), mensaje, TITULO, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(String mensaje) {
        JOptionPane.showMessageDialog(padre(), mensaje, TITULO, JOptionPane.ERROR_MESSAGE);
    }

    /** Muestra un texto largo (listados y tablas) en un área con desplazamiento. */
    public static void mostrarTexto(String titulo, String texto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(820, 360));
        JOptionPane.showMessageDialog(padre(), scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    /** Línea separadora para los listados. */
    public static String linea() {
        return "-------------------------------------------------------------------------------------------\n";
    }
}
