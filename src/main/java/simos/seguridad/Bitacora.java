package simos.seguridad;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro append-only de las acciones del simulador.
 * Formato de cada línea: fecha/hora;usuario;acción;resultado
 */
public final class Bitacora {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path archivo;

    public Bitacora(String ruta) {
        this.archivo = Paths.get(ruta);
    }

    public Path getArchivo() {
        return archivo;
    }

    public synchronized void registrar(String usuario, String accion, boolean permitido) {
        registrar(usuario, accion, permitido ? "PERMITIDO" : "DENEGADO");
    }

    public synchronized void registrar(String usuario, String accion, String resultado) {
        String linea = LocalDateTime.now().format(FORMATO_FECHA)
                + ";" + limpiar(usuario)
                + ";" + limpiar(accion)
                + ";" + limpiar(resultado);

        try {
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir la bitácora: " + e.getMessage());
        }
    }

    /**
     * Devuelve las últimas {@code cantidad} líneas registradas, de la más antigua a la más reciente.
     */
    public synchronized List<String> leerUltimos(int cantidad) {
        if (cantidad <= 0 || !Files.exists(archivo)) {
            return List.of();
        }
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            int desde = Math.max(0, lineas.size() - cantidad);
            return new ArrayList<>(lineas.subList(desde, lineas.size()));
        } catch (IOException e) {
            System.err.println("No se pudo leer la bitácora: " + e.getMessage());
            return List.of();
        }
    }

    // Evita que un valor con ';' o saltos de línea rompa el formato de la bitácora.
    private static String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return "(vacío)";
        }
        return valor.replace(';', ',')
                .replace('\n', ' ')
                .replace('\r', ' ')
                .trim();
    }
}
