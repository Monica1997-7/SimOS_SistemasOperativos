package simos.seguridad; // Indica que esta clase pertenece al paquete de seguridad.

/**
 * ENUM (Enumeración) Permiso.
 * Un 'enum' es una estructura que define una lista fija y constante de opciones.
 * En este caso, define cuáles son los únicos permisos que existen en todo el sistema SimOS.
 * Si mañana se agrega el módulo de Entrada/Salida, solo se añade una línea aquí.
 */
public enum Permiso {
    
    // --- LISTA DE PERMISOS DISPONIBLES ---
    // Cada permiso tiene un nombre (en mayúsculas, por convención de Java) 
    // y un texto entre paréntesis que es la descripción legible para el humano.
    CPU("Planificación de CPU"),
    CONCURRENCIA("Concurrencia"),
    MEMORIA("Memoria"),
    ALMACENAMIENTO("Almacenamiento"),
    SEGURIDAD("Seguridad");

    // --- ATRIBUTO ---
    // Cada permiso guardará su descripción (ej. "Planificación de CPU").
    private final String descripcion;

    /**
     * CONSTRUCTOR del Enum.
     * Se ejecuta automáticamente cuando se crean los permisos de arriba.
     * Asigna el texto descriptivo a cada permiso.
     */
    Permiso(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * GETTER.
     * Devuelve el texto descriptivo del permiso para poder imprimirlo en pantalla
     * (por ejemplo, en la tabla de usuarios del Módulo 5).
     */
    public String getDescripcion() {
        return descripcion;
    }
}