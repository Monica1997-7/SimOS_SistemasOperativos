package simos.seguridad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Carga los usuarios desde {@code /usuarios.txt} y valida credenciales.
 */
public final class GestorUsuarios {

    private static final String RECURSO = "/usuarios.txt";

    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();

    public GestorUsuarios() {
        cargar();
    }

    private void cargar() {
        try (InputStream entrada = GestorUsuarios.class.getResourceAsStream(RECURSO)) {
            if (entrada == null) {
                System.err.println("No se encontró el recurso " + RECURSO + " en el classpath.");
                return;
            }
            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
                String linea;
                int numeroLinea = 0;
                while ((linea = lector.readLine()) != null) {
                    numeroLinea++;
                    linea = linea.trim();
                    if (linea.isEmpty() || linea.startsWith("#")) {
                        continue;
                    }
                    cargarLinea(linea, numeroLinea);
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer " + RECURSO + ": " + e.getMessage());
        }
    }

    private void cargarLinea(String linea, int numeroLinea) {
        String[] partes = linea.split(";", -1);
        if (partes.length != 3) {
            System.err.println("usuarios.txt línea " + numeroLinea
                    + ": se esperaban 3 campos (usuario;hash;rol).");
            return;
        }
        try {
            Usuario usuario = new Usuario(partes[0], partes[1], Rol.desdeTexto(partes[2]));
            usuarios.put(clave(usuario.getNombre()), usuario);
        } catch (IllegalArgumentException e) {
            System.err.println("usuarios.txt línea " + numeroLinea + ": " + e.getMessage());
        }
    }

    public Optional<Usuario> autenticar(String nombre, String clave) {
        if (nombre == null || clave == null) {
            return Optional.empty();
        }
        Usuario usuario = usuarios.get(clave(nombre));
        if (usuario != null && usuario.verificarClave(clave)) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    public Collection<Usuario> listar() {
        return Collections.unmodifiableCollection(usuarios.values());
    }

    private static String clave(String nombre) {
        return nombre.trim().toLowerCase(Locale.ROOT);
    }
}
