package simos.seguridad; // Indica que esta clase pertenece al paquete de seguridad.

// --- IMPORTACIONES ---
import java.io.IOException;             // Para manejar errores si el archivo no se puede escribir o leer.
import java.nio.charset.StandardCharsets; // Para guardar el texto en UTF-8 (soporta tildes y eñes).
import java.nio.file.Files;             // Herramienta moderna de Java para leer y escribir archivos fácilmente.
import java.nio.file.Path;              // Representa la ruta de un archivo en el disco duro.
import java.nio.file.Paths;             // Para crear rutas a partir de textos (ej. "bitacora.txt").
import java.nio.file.StandardOpenOption; // Para decirle a Java "crea el archivo si no existe" y "añade al final sin borrar".
import java.time.LocalDateTime;         // Para obtener la fecha y hora actual del computador.
import java.time.format.DateTimeFormatter; // Para darle formato bonito a la fecha (ej. Año-Mes-Día Hora:Minuto:Segundo).
import java.util.ArrayList;             // Para crear una lista modificable de líneas.
import java.util.List;                  // Para manejar listas de textos (las líneas del archivo).

/**
 * Clase Bitacora.
 * Se encarga de guardar un registro histórico (append-only) de todo lo que pasa en el sistema.
 * Formato de cada línea: fecha;hora;usuario;accion;resultado
 */
public final class Bitacora {

    // Define el formato exacto en que se guardará la fecha y hora en el archivo.
    // Ejemplo: "2026-11-13 16:45:02"
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Atributo que guarda la ruta física del archivo (ej. "bitacora.txt").
    private final Path archivo;

    /**
     * CONSTRUCTOR.
     * Recibe la ruta del archivo como texto (ej. "bitacora.txt") y la convierte en un objeto Path.
     */
    public Bitacora(String ruta) {
        this.archivo = Paths.get(ruta);
    }

    /**
     * GETTER.
     * Devuelve la ruta del archivo (útil para mostrarla en el Módulo 5).
     */
    public Path getArchivo() {
        return archivo;
    }

    /**
     * MÉTODO REGISTRAR (Versión 1 - Recibe un booleano).
     * Es un método "puente". Recibe 'true' o 'false' y lo convierte en el texto "PERMITIDO" o "DENEGADO".
     * 
     * La palabra 'synchronized' es MUY importante en Sistemas Operativos:
     * significa que si dos hilos intentan escribir en la bitácora al mismo tiempo, uno debe esperar a que el otro termine.
     * Esto evita que el archivo se corrompa (es un mutex).
     */
    public synchronized void registrar(String usuario, String accion, boolean permitido) {
        // Llama al otro método 'registrar' pasándole el texto ya convertido.
        registrar(usuario, accion, permitido ? "PERMITIDO" : "DENEGADO");
    }

    /**
     * MÉTODO REGISTRAR (Versión 2 - Recibe el texto del resultado).
     * Aquí es donde realmente se escribe en el archivo.
     */
    public synchronized void registrar(String usuario, String accion, String resultado) {
        // 1. Arma la línea completa juntando la fecha, el usuario, la acción y el resultado separados por ';'.
        String linea = LocalDateTime.now().format(FORMATO_FECHA)
                + ";" + limpiar(usuario)
                + ";" + limpiar(accion)
                + ";" + limpiar(resultado);

        try {
            // 2. Escribe en el archivo.
            // - 'Files.writeString' escribe texto.
            // - 'StandardCharsets.UTF_8' asegura la codificación de caracteres.
            // - 'StandardOpenOption.CREATE' crea el archivo si es la primera vez que se abre.
            // - 'StandardOpenOption.APPEND' añade al final (NO borra lo que ya estaba escrito).
            // - 'System.lineSeparator()' inserta un salto de línea compatible con Windows/Linux.
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Si el disco está lleno o no hay permisos, muestra el error (pero no cierra el programa).
            System.err.println("No se pudo escribir la bitácora: " + e.getMessage());
        }
    }

    /**
     * MÉTODO LEER ÚLTIMOS.
     * Lee el archivo y devuelve una lista con las últimas 'cantidad' líneas.
     * Lo usa el ModuloSeguridad para imprimir la tabla en pantalla.
     */
    public synchronized List<String> leerUltimos(int cantidad) {
        // Si piden leer 0 o menos líneas, o si el archivo aún no existe, devuelve una lista vacía.
        if (cantidad <= 0 || !Files.exists(archivo)) {
            return List.of();
        }
        
        try {
            // Lee TODAS las líneas del archivo y las mete en una lista.
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            
            // Calcula desde qué índice empezar a cortar.
            // Ejemplo: Si el archivo tiene 50 líneas y pedimos las últimas 20, 'desde' será 30 (50 - 20).
            // 'Math.max(0, ...)' evita que salga un número negativo si el archivo tiene menos líneas de las pedidas.
            int desde = Math.max(0, lineas.size() - cantidad);
            
            // Devuelve una nueva lista solo con el pedazo que nos interesa (desde el índice 'desde' hasta el final).
            return new ArrayList<>(lineas.subList(desde, lineas.size()));
            
        } catch (IOException e) {
            System.err.println("No se pudo leer la bitácora: " + e.getMessage());
            return List.of(); // Si falla, devuelve una lista vacía.
        }
    }

    /**
     * MÉTODO AUXILIAR: limpiar.
     * Es una medida de seguridad. Si el usuario escribe un punto y coma (;) o presiona Enter,
     * podría dañar el formato de la bitácora (que usa ; como separador).
     * Este método reemplaza los ; por , y los saltos de línea por espacios.
     */
    private static String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return "(vacío)";
        }
        return valor.replace(';', ',') // Cambia ; por ,
                .replace('\n', ' ')     // Cambia saltos de línea por espacios
                .replace('\r', ' ')     // Cambia retornos de carro por espacios
                .trim();                // Borra espacios al inicio y al final
    }
}