package simos.seguridad; // Indica que esta clase pertenece al paquete de seguridad.

import java.util.List;    // Herramienta para manejar listas de elementos (como las líneas de la bitácora).
import java.util.Scanner; // Herramienta para leer lo que el usuario escribe por teclado.

/**
 * Clase que representa la interfaz de consola del Módulo 5 (Seguridad).
 * Su función es mostrar el menú interno de seguridad, listar los usuarios 
 * y mostrar el historial de la bitácora.
 */
public final class ModuloSeguridad {

    // Constante que define cuántos registros de la bitácora se van a mostrar en pantalla (los últimos 20).
    private static final int ULTIMOS_REGISTROS = 20;

    // --- ATRIBUTOS ---
    // Referencias a las clases que contienen los datos reales.
    private final GestorUsuarios gestorUsuarios; // Para obtener la lista de usuarios.
    private final Bitacora bitacora;             // Para leer los registros de auditoría.

    /**
     * CONSTRUCTOR.
     * Recibe el gestor de usuarios y la bitácora desde la clase Main.
     * ¿Por qué los recibe en lugar de crearlos aquí? Porque en la programación orientada a objetos, 
     * es mejor reutilizar los objetos que ya existen (inyección de dependencias) a crear unos nuevos cada vez.
     */
    public ModuloSeguridad(GestorUsuarios gestorUsuarios, Bitacora bitacora) {
        this.gestorUsuarios = gestorUsuarios;
        this.bitacora = bitacora;
    }

    /**
     * MÉTODO PRINCIPAL DEL MÓDULO: ejecutar.
     * Es el que llama el Main.java cuando eliges la opción 5.
     * Contiene un bucle para que el menú de seguridad se repita hasta que el usuario decida salir.
     */
    public void ejecutar(Scanner sc) {
        boolean volver = false; // Bandera para saber cuándo salir del bucle y volver al menú principal.
        
        // Mientras 'volver' sea falso, el menú se sigue mostrando.
        while (!volver) {
            imprimirMenu(); // Muestra las opciones en pantalla (1. Usuarios, 2. Bitácora, 3. Salir).
            String opcion = leerLinea(sc); // Lee la opción del teclado.
            
            // Si la entrada es nula (ej. se cerró la consola), salimos.
            if (opcion == null) {
                volver = true;
                continue;
            }
            
            // Evalúa la opción elegida.
            switch (opcion) {
                case "1" -> listarUsuarios();   // Llama al método que imprime la tabla de usuarios.
                case "2" -> mostrarBitacora();  // Llama al método que imprime el historial.
                case "3", "0" -> volver = true; // Si elige 3 o 0, cambia la bandera para salir del bucle.
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
        // Cuando sale del bucle, imprime este mensaje y termina el método (volviendo al Main).
        System.out.println("Volviendo al menú principal...");
    }

    /**
     * Imprime las opciones del submenú de Seguridad.
     */
    private void imprimirMenu() {
        System.out.println();
        System.out.println("=== MÓDULO 5: SEGURIDAD ===");
        System.out.println("1. Listar usuarios registrados");
        System.out.println("2. Consultar los últimos registros de la bitácora");
        System.out.println("3. Volver al menú principal");
        System.out.print("Opción: ");
    }

    /**
     * Muestra en pantalla una tabla con todos los usuarios, sus roles y sus permisos.
     */
    private void listarUsuarios() {
        System.out.println();
        // Imprime el encabezado de la tabla. 
        // '%-14s' significa: imprime un texto ocupando un ancho de 14 caracteres, alineado a la izquierda.
        System.out.printf("%-14s | %-14s | %s%n", "USUARIO", "ROL", "PERMISOS");
        System.out.println("-".repeat(78)); // Imprime una línea divisoria de 78 guiones.
        
        // Recorre la lista de usuarios que nos da el GestorUsuarios.
        for (Usuario usuario : gestorUsuarios.listar()) {
            System.out.printf("%-14s | %-14s | %s%n",
                    usuario.getNombre(),
                    usuario.getRol().getDescripcion(),
                    permisosComoTexto(usuario.getRol())); // Llama al método auxiliar de abajo.
        }
        System.out.println();
    }

    /**
     * Muestra los últimos 20 registros de la bitácora en una tabla.
     */
    private void mostrarBitacora() {
        System.out.println();
        // Muestra la ruta absoluta del archivo bitacora.txt (útil para saber dónde se está guardando).
        System.out.println("Archivo: " + bitacora.getArchivo().toAbsolutePath());
        
        // Pide a la clase Bitacora las últimas 20 líneas.
        List<String> lineas = bitacora.leerUltimos(ULTIMOS_REGISTROS);
        
        // Si la lista está vacía, muestra un mensaje y termina.
        if (lineas.isEmpty()) {
            System.out.println("No hay registros en la bitácora.");
            System.out.println();
            return;
        }

        // Imprime el encabezado de la tabla de auditoría.
        System.out.printf("%-19s | %-14s | %-32s | %s%n",
                "FECHA/HORA", "USUARIO", "ACCIÓN", "RESULTADO");
        System.out.println("-".repeat(84));
        
        // Recorre cada línea de la bitácora.
        for (String linea : lineas) {
            // Las líneas de la bitácora tienen el formato: fecha;usuario;accion;resultado
            // Aquí usamos .split(";") para cortar el texto en pedazos cada vez que encuentre un punto y coma.
            String[] partes = linea.split(";", -1);
            
            // Si el corte dio exactamente 4 pedazos (fecha, usuario, accion, resultado), imprime la fila bonita.
            if (partes.length == 4) {
                System.out.printf("%-19s | %-14s | %-32s | %s%n",
                        partes[0], partes[1], partes[2], partes[3]);
            } else {
                // Si por alguna razón la línea está corrupta o no tiene 4 partes, la imprime tal cual.
                System.out.println(linea);
            }
        }
        System.out.println();
    }

    /**
     * MÉTODO AUXILIAR: permisosComoTexto.
     * Convierte una lista de permisos (que es un conjunto) en un solo texto separado por comas.
     * Ejemplo: Convierte [Permiso.CPU, Permiso.MEMORIA] en "Planificación de CPU, Memoria".
     */
    private static String permisosComoTexto(Rol rol) {
        // Usamos StringBuilder porque es más eficiente que concatenar strings con '+' dentro de un bucle.
        StringBuilder texto = new StringBuilder();
        
        // Recorre la lista de permisos del rol.
        for (Permiso permiso : rol.getPermisos()) {
            // Si el texto ya tiene algo adentro, le agrega una coma y un espacio antes del siguiente permiso.
            if (texto.length() > 0) {
                texto.append(", ");
            }
            // Agrega la descripción del permiso (ej. "Memoria").
            texto.append(permiso.getDescripcion());
        }
        // Retorna el texto final completo.
        return texto.toString();
    }

    /**
     * MÉTODO AUXILIAR: leerLinea.
     * Es igual al que vimos en el Main. Lee lo que el usuario escribe y le borra los espacios sobrantes.
     * Si el usuario no escribió nada (o se cerró la consola), devuelve null.
     */
    private static String leerLinea(Scanner sc) {
        if (!sc.hasNextLine()) {
            return null;
        }
        return sc.nextLine().trim();
    }
}