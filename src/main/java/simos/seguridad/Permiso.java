package simos.seguridad;

/**
 * Acciones que un rol puede realizar dentro del simulador.
 */
public enum Permiso {
    CPU("Planificación de CPU"),
    CONCURRENCIA("Concurrencia"),
    MEMORIA("Memoria"),
    ALMACENAMIENTO("Almacenamiento"),
    SEGURIDAD("Seguridad");

    private final String descripcion;

    Permiso(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
