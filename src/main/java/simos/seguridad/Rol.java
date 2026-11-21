package simos.seguridad; // Indica que esta clase pertenece al paquete de seguridad.

// --- IMPORTACIONES ---
import java.util.Collections; // Herramienta para hacer que una lista/colección sea de "solo lectura" (no se pueda modificar).
import java.util.EnumSet;     // Una herramienta súper eficiente de Java para crear listas de valores fijos (como los permisos).
import java.util.Locale;      // Herramienta para manejar textos en mayúsculas/minúsculas sin importar el idioma del PC.
import java.util.Set;         // Interfaz que representa un conjunto de elementos (sin duplicados).

/**
 * ENUM (Enumeración) Rol.
 * Un 'enum' es un tipo de dato especial que sirve para definir una lista fija de opciones.
 * Aquí definimos que en el sistema SOLO pueden existir dos roles: ADMINISTRADOR y USUARIO.
 * Además, cada rol "nace" con un conjunto de permisos asignados.
 */
public enum Rol {
    
    // --- DEFINICIÓN DE LOS ROLES Y SUS PERMISOS ---
    // El ADMINISTRADOR tiene TODOS los permisos. 'EnumSet.allOf(Permiso.class)' agarra todos los permisos que existan y se los asigna.
    ADMINISTRADOR("Administrador", EnumSet.allOf(Permiso.class)),
    
    // El USUARIO normal tiene permisos LIMITADOS. Solo puede entrar a CPU, Concurrencia, Memoria y Almacenamiento.
    // Nota que NO tiene el permiso de 'SEGURIDAD', por lo tanto, el sistema le bloqueará la entrada al Módulo 5.
    USUARIO("Usuario", EnumSet.of(
            Permiso.CPU,
            Permiso.CONCURRENCIA,
            Permiso.MEMORIA,
            Permiso.ALMACENAMIENTO));

    // --- ATRIBUTOS DEL ROL ---
    // Cada rol tendrá un nombre para mostrar (ej. "Administrador") y una lista de permisos.
    private final String descripcion;
    private final Set<Permiso> permisos;

    /**
     * CONSTRUCTOR del Enum.
     * Cada vez que se crea un Rol (arriba), automáticamente se llama a este método para 
     * asignarle su descripción y su lista de permisos.
     */
    Rol(String descripcion, Set<Permiso> permisos) {
        this.descripcion = descripcion;
        // 'Collections.unmodifiableSet' es una medida de seguridad. 
        // Hace que la lista de permisos sea "de solo lectura": nadie puede añadirle o quitarle permisos a un rol después de crearlo.
        this.permisos = Collections.unmodifiableSet(permisos);
    }

    /**
     * Devuelve el nombre del rol para mostrarlo en pantalla.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Devuelve la lista completa de permisos que tiene ese rol.
     */
    public Set<Permiso> getPermisos() {
        return permisos;
    }

    /**
     * MÉTODO VITAL PARA LA SEGURIDAD: tienePermiso.
     * Es el que se usa en el Main.java para saber si te deja entrar a un módulo o no.
     * 
     * @param permiso El permiso que se quiere verificar (ej. Permiso.SEGURIDAD).
     * @return true si el rol tiene ese permiso, false si no lo tiene.
     */
    public boolean tienePermiso(Permiso permiso) {
        // Busca si el permiso está dentro de la lista de permisos del rol.
        return permisos.contains(permiso);
    }

    /**
     * MÉTODO ESTÁTICO: desdeTexto.
     * Sirve para convertir un texto (que viene de un archivo, como "ADMINISTRADOR" o "USUARIO") 
     * en el objeto Rol real de Java.
     * 
     * Ejemplo de uso: Cuando se lee el archivo usuarios.txt, viene el texto "USUARIO". 
     * Este método lo convierte en el objeto Rol.USUARIO.
     */
    public static Rol desdeTexto(String texto) {
        // Si el texto es nulo o está vacío, lanza un error.
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El rol está vacío");
        }
        
        // Estructura 'switch' moderna:
        // Toma el texto, le quita los espacios (.trim()) y lo pone todo en mayúsculas (.toUpperCase()).
        return switch (texto.trim().toUpperCase(Locale.ROOT)) {
            // Si el texto es "ADMIN" o "ADMINISTRADOR", devuelve el rol ADMINISTRADOR.
            case "ADMIN", "ADMINISTRADOR" -> ADMINISTRADOR;
            // Si el texto es "USUARIO" o "USER", devuelve el rol USUARIO.
            case "USUARIO", "USER" -> USUARIO;
            // Si es cualquier otra cosa, lanza un error diciendo que el rol no existe.
            default -> throw new IllegalArgumentException("Rol desconocido: " + texto);
        };
    }
}