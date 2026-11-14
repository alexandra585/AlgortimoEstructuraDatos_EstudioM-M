package persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import servicio.SistemaEstudio;

/**
 * RF-17: guarda y carga toda la información en un archivo local
 * (serialización de objetos Java, sin motor de base de datos).
 */
public class ServicioPersistencia {

    private String rutaArchivos;

    public ServicioPersistencia(String rutaArchivos) {
        this.rutaArchivos = rutaArchivos;
    }

    public boolean existeArchivo() {
        return new File(rutaArchivos).exists();
    }

    /** Guarda el estado completo del sistema. Devuelve true si se guardó. */
    public boolean guardarTodo(SistemaEstudio sistema) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivos))) {
            out.writeObject(sistema);
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga el estado del sistema desde el archivo. Si el archivo no existe o
     * no se puede leer, crea un sistema nuevo con los datos iniciales cargados.
     */
    public SistemaEstudio cargarTodo() {
        if (existeArchivo()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(rutaArchivos))) {
                return (SistemaEstudio) in.readObject();
            } catch (Exception e) {
                System.err.println("No se pudo leer " + rutaArchivos + ": " + e.getMessage());
            }
        }
        return DatosIniciales.crearSistema();
    }

    /** Borra el archivo y devuelve un sistema con los datos iniciales. */
    public SistemaEstudio restaurarDatosIniciales() {
        new File(rutaArchivos).delete();
        SistemaEstudio s = DatosIniciales.crearSistema();
        guardarTodo(s);
        return s;
    }

    public String getRutaArchivos() {
        return new File(rutaArchivos).getAbsolutePath();
    }
}
