package simos.seguridad; // Indica que esta clase pertenece al paquete de seguridad.

// --- IMPORTACIONES ---
import java.io.BufferedReader;      // Para leer texto de un archivo de forma eficiente (línea por línea).
import java.io.IOException;         // Para manejar errores de entrada/salida (ej. si el archivo no existe).
import java.io.InputStream;         // Para leer archivos que están dentro del proyecto (en la carpeta resources).
import java.io.InputStreamReader;   // Para convertir los bytes del archivo en texto legible.
import java.nio.charset.StandardCharsets; // Para leer el archivo en UTF-8 y no dañar tildes.
import java.util.Collection;        // Para devolver una colección (lista) de usuarios.
import java.util.Collections;       // Para hacer que las listas sean de "solo lectura" (protegidas).
import java.util.LinkedHashMap;     // Un diccionario que mantiene el orden en que se insertan los datos.
import java.util.Locale;            // Para manejar textos en minúsculas/mayúsculas.
import java.util.Map;               // La interfaz del diccionario (Llave -> Valor).
import java.util.Optional;          // Una caja que puede tener un valor o estar vacía (evita errores de null).

/**
 * Clase que administra a los usuarios.
 * Su trabajo es leer el archivo usuarios.txt, guardarlos en memoria y validar el login.
 */
public final class GestorUsuarios {

    // Ruta del archivo. La barra '/' al inicio significa que está en la carpeta 'resources' de Java.
    private static final String RECURSO = "/usuarios.txt";

    // Diccionario donde guardaremos a los usuarios.
    // La llave (String) será el nombre en minúsculas (ej. "admin").
    // El valor (Usuario) será el objeto completo con su hash y su rol.
    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();

    /**
     * CONSTRUCTOR.
     * Cuando alguien hace un 'new GestorUsuarios()', automáticamente llama al método 'cargar()'.
     */
    public GestorUsuarios() {
        cargar();
    }

    /**
     * MÉTODO: cargar.
     * Lee el archivo usuarios.txt y mete a todos los usuarios en el diccionario.
     */
    private void cargar() {
        // 'try-with-resources' abre el archivo y asegura que se cierre automáticamente al terminar.
        try (InputStream entrada = GestorUsuarios.class.getResourceAsStream(RECURSO)) {
            
            // Si la entrada es nula, significa que el archivo no se encontró. Muestra el error y sale.
            if (entrada == null) {
                System.err.println("No se encontró el recurso " + RECURSO + " en el classpath.");
                return;
            }

            // Prepara el lector de texto (BufferedReader) pasándole la entrada del archivo.
            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
                
                String linea;
                int numeroLinea = 0;
                
                // Lee el archivo línea por línea hasta que se acabe (readLine() devuelve null).
                while ((linea = lector.readLine()) != null) {
                    numeroLinea++;
                    linea = linea.trim(); // Borra espacios sobrantes.
                    
                    // Si la línea está vacía o empieza con '#' (un comentario), la ignora y pasa a la siguiente.
                    if (linea.isEmpty() || linea.startsWith("#")) {
                        continue;
                    }
                    
                    // Si la línea tiene datos, llama al método para procesarla.
                    cargarLinea(linea, numeroLinea);
                }
            }
        } catch (IOException e) {
            // Si hay un error de lectura (ej. el archivo está bloqueado), lo muestra en consola.
            System.err.println("No se pudo leer " + RECURSO + ": " + e.getMessage());
        }
    }

    /**
     * MÉTODO AUXILIAR: cargarLinea.
     * Procesa una sola línea del archivo (ej. "admin;240be518...;ADMINISTRADOR").
     */
    private void cargarLinea(String linea, int numeroLinea) {
        // Corta la línea usando el punto y coma como separador.
        // El '-1' asegura que si hay campos vacíos al final, no se pierdan.
        String[] partes = linea.split(";", -1);
        
        // Si no hay exactamente 3 partes (usuario, hash, rol), muestra error y no carga la línea.
        if (partes.length != 3) {
            System.err.println("usuarios.txt línea " + numeroLinea
                    + ": se esperaban 3 campos (usuario;hash;rol).");
            return;
        }
        
        try {
            // Crea el objeto Usuario usando las 3 partes.
            // partes[0] = nombre, partes[1] = hash, partes[2] = rol (convertido con Rol.desdeTexto).
            Usuario usuario = new Usuario(partes[0], partes[1], Rol.desdeTexto(partes[2]));
            
            // Lo guarda en el diccionario. Usa el método 'clave()' para guardar el nombre en minúsculas.
            usuarios.put(clave(usuario.getNombre()), usuario);
            
        } catch (IllegalArgumentException e) {
            // Si el usuario tiene datos inválidos (ej. rol desconocido), muestra el error.
            System.err.println("usuarios.txt línea " + numeroLinea + ": " + e.getMessage());
        }
    }

    /**
     * MÉTODO PRINCIPAL: autenticar.
     * Es el que se llama en el login para verificar si la clave es correcta.
     * 
     * @param nombre El nombre que escribió el usuario.
     * @param clave La contraseña en texto plano que escribió el usuario.
     * @return Un Optional con el Usuario si es correcto, o un Optional vacío si falló.
     */
    public Optional<Usuario> autenticar(String nombre, String clave) {
        // Si el nombre o la clave son nulos, retorna una caja vacía (falló).
        if (nombre == null || clave == null) {
            return Optional.empty();
        }
        
        // Busca al usuario en el diccionario pasando su nombre a minúsculas.
        Usuario usuario = usuarios.get(clave(nombre));
        
        // Si el usuario existe Y su contraseña es correcta...
        if (usuario != null && usuario.verificarClave(clave)) {
            // Retorna una caja con el usuario adentro (éxito).
            return Optional.of(usuario);
        }
        
        // Si el usuario no existe o la clave está mal, retorna una caja vacía.
        return Optional.empty();
    }

    /**
     * Devuelve todos los usuarios cargados.
     * Los envuelve en 'unmodifiableCollection' para que nadie pueda borrar usuarios por accidente.
     */
    public Collection<Usuario> listar() {
        return Collections.unmodifiableCollection(usuarios.values());
    }

    /**
     * MÉTODO AUXILIAR: clave.
     * Convierte un nombre a minúsculas y le quita espacios.
     * Sirve para que "Admin", "ADMIN" y "admin" se traten como el mismo usuario.
     */
    private static String clave(String nombre) {
        return nombre.trim().toLowerCase(Locale.ROOT);
    }
}