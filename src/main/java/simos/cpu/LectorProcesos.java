package simos.cpu;

// --- IMPORTACIONES PARA LEER DATOS ---
// BufferedReader: lee un archivo línea por línea de forma eficiente.
// IOException: es el error que puede salir si el archivo no existe o no se puede leer.
// InputStream e InputStreamReader: permiten leer el archivo que está dentro del proyecto.
// StandardCharsets: indica que el texto está en UTF-8 (para que los acentos salgan bien).
// Files y Path: sirven para buscar un archivo en el disco.
// ArrayList y List: para guardar la lista de procesos.
// Scanner: para leer lo que escribe el usuario en el teclado.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Obtiene la lista de procesos desde el teclado o desde el archivos.txt.
 * Formato de cada línea del archivo: nombre;llegada;rafaga
 * Las líneas vacías y las que empiezan con # se ignoran.
 *
 * Esta clase es la "entrada de datos" del módulo: no calcula nada, solo convierte
 * lo que escribe el usuario (o lo que hay en un archivo) en objetos Proceso.
 */
public final class LectorProcesos {

    // Nombre del archivo que se buscará en el directorio de ejecución.
    public static final String ARCHIVO = "procesos.txt";

    // Ruta interna del archivo dentro del proyecto (src/main/resources/procesos.txt).
    // La barra inicial "/" indica "desde la raíz de los recursos de Java".
    private static final String RECURSO = "/procesos.txt";

    /**
     * Pide los procesos uno por uno por teclado.
     *
     * Recibe: el Scanner compartido con el Main (para usar el mismo teclado).
     * Internamente: pregunta la cantidad, luego para cada proceso pide nombre,
     * llegada y ráfaga, crea el objeto y lo agrega a la lista.
     * Devuelve: la lista de procesos. Si la entrada se corta, devuelve una lista vacía.
     */
    public List<Proceso> leerTeclado(Scanner sc) {
        // Mostramos la pregunta y leemos un número entero con nuestro método auxiliar.
        System.out.print("Cantidad de procesos: ");
        int cantidad = leerEntero(sc);
        // Si no es un número positivo, avisamos y devolvemos una lista vacía.
        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser mayor que cero.");
            return List.of();
        }

        // Aquí guardaremos los procesos que vaya tecleando el usuario.
        List<Proceso> procesos = new ArrayList<>();
        // Bucle para pedir cada proceso, del 1 al número indicado.
        for (int i = 1; i <= cantidad; i++) {
            // Cartelito para que el usuario sepa cuál proceso está cargando.
            System.out.println("--- Proceso " + i + " de " + cantidad + " ---");

            // Pedimos el nombre como texto.
            System.out.print("Nombre: ");
            String nombre = leerLinea(sc);
            // Si se cerró la entrada de datos, devolvemos lo que llevamos (vacío).
            if (nombre == null) {
                return List.of();
            }

            // Pedimos la llegada y la ráfaga como números enteros.
            System.out.print("Llegada: ");
            int llegada = leerEntero(sc);

            System.out.print("Ráfaga: ");
            int rafaga = leerEntero(sc);

            // intentamos crear el Proceso. Si los datos están mal, el constructor lanza
            // IllegalArgumentException y aquí la atrapamos para no cortar el programa.
            try {
                procesos.add(new Proceso(nombre, llegada, rafaga));
            } catch (IllegalArgumentException e) {
                // Mostramos el motivo y seguimos con el siguiente proceso.
                System.out.println("Proceso omitido: " + e.getMessage());
            }
        }
        // Devolvemos la lista completa ya cargada.
        return procesos;
    }

    /**
     * Lee procesos.txt. Primero busca el recurso empaquetado en el proyecto y,
     * si no existe, un archivos.txt en el directorio de ejecución.
     *
     * Recibe: nada (sabe qué archivo buscar por las constantes de arriba).
     * Internamente: abre el archivo, recorre sus líneas, descarta comentarios y
     * convierte cada línea válida en un Proceso.
     * Devuelve: la lista de procesos leídos. Lanza IOException si no hay archivo
     * o si el formato de una línea es incorrecto.
     */
    public List<Proceso> leerArchivo() throws IOException {
        // getResourceAsStream busca el archivo dentro del proyecto compilado.
        InputStream recurso = LectorProcesos.class.getResourceAsStream(RECURSO);
        // 'lector' será nuestro lector de líneas; 'origen' solo guarda un texto para mensajes.
        BufferedReader lector;
        String origen;

        if (recurso != null) {
            // Encontramos el archivo en los recursos del proyecto (lo más habitual).
            lector = new BufferedReader(new InputStreamReader(recurso, StandardCharsets.UTF_8));
            origen = "src/main/resources/procesos.txt";
        } else {
            // Si no está en los recursos, buscamos un procesos.txt en la carpeta actual.
            Path ruta = Path.of(ARCHIVO);
            if (!Files.exists(ruta)) {
                // Si tampoco está, avisamos con un error claro.
                throw new IOException("No se encontró " + ARCHIVO + " ni en los recursos ni en "
                        + ruta.toAbsolutePath());
            }
            lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8);
            origen = ruta.toAbsolutePath().toString();
        }

        // Lista donde guardaremos los procesos leídos.
        List<Proceso> procesos = new ArrayList<>();
        // try-with-resources: al terminar el bloque, el archivo se cierra automáticamente.
        try (lector) {
            String linea;
            // Contador de línea para poder decir "la línea 4 está mal".
            int numero = 0;
            // readLine() devuelve null cuando ya no hay más líneas.
            while ((linea = lector.readLine()) != null) {
                numero++;
                // Quitamos espacios sobrantes al principio y al final.
                String texto = linea.trim();
                // Ignoramos líneas en blanco y las que empiezan con # (comentarios).
                if (texto.isEmpty() || texto.startsWith("#")) {
                    continue;
                }
                // Línea válida: la convertimos en Proceso y la agregamos.
                procesos.add(parsearLinea(texto, numero));
            }
        }

        // Si después de leer todo no hay procesos, avisamos.
        if (procesos.isEmpty()) {
            throw new IOException(origen + " no contiene procesos válidos.");
        }
        // Devolvemos la lista lista para simular.
        return procesos;
    }

    /**
     * Convierte una línea como "P1;0;8" en un objeto Proceso.
     *
     * Recibe: la línea de texto y su número (para el mensaje de error).
     * Internamente: separa por ';', exige exactamente 3 partes y convierte
     * las dos últimas a números enteros.
     * Devuelve: el Proceso creado, o lanza IOException si la línea está mal.
     */
    private static Proceso parsearLinea(String linea, int numero) throws IOException {
        // split(";", -1) corta por punto y coma. El -1 conserva campos vacíos
        // para poder detectar que faltó algún dato.
        String[] partes = linea.split(";", -1);
        // Deben ser exactamente 3: nombre, llegada y ráfaga.
        if (partes.length != 3) {
            throw new IOException("Línea " + numero + " inválida (se espera nombre;llegada;rafaga): " + linea);
        }
        try {
            // partes[0] es texto; partes[1] y partes[2] se convierten con parseInt.
            // El constructor de Proceso vuelve a validar que los números sean correctos.
            return new Proceso(partes[0].trim(),
                    Integer.parseInt(partes[1].trim()),
                    Integer.parseInt(partes[2].trim()));
        } catch (IllegalArgumentException e) {
            // NumberFormatException también es IllegalArgumentException, así que se atrapa aquí.
            throw new IOException("Línea " + numero + " inválida: " + linea + " (" + e.getMessage() + ")");
        }
    }

    /**
     * Lee una línea del teclado y le quita los espacios sobrantes.
     * Recibe: el Scanner.
     * Devuelve: el texto escrito, o null si ya no hay más entrada (por ejemplo, Ctrl+Z).
     */
    private static String leerLinea(Scanner sc) {
        return sc.hasNextLine() ? sc.nextLine().trim() : null;
    }

    /** Lee un entero; devuelve 0 si la entrada termina o el valor no es numérico. */
    private static int leerEntero(Scanner sc) {
        // Reutilizamos leerLinea para obtener el texto.
        String linea = leerLinea(sc);
        if (linea == null) {
            // Si no hay entrada, devolvemos 0 para que quien llama decida qué hacer.
            return 0;
        }
        try {
            // Convertimos el texto a número entero.
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            // Si el usuario escribió letras en vez de un número, también devolvemos 0.
            return 0;
        }
    }
}
