package simos.seguridad;

/**
 * Usuario del simulador. La clave solo se conserva como hash SHA-256.
 */
public final class Usuario {

    private final String nombre;
    private final String hashClave;
    private final Rol rol;

    public Usuario(String nombre, String hashClave, Rol rol) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (hashClave == null || hashClave.isBlank()) {
            throw new IllegalArgumentException("El hash de la clave es obligatorio");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        this.nombre = nombre.trim();
        this.hashClave = hashClave.trim();
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean verificarClave(String clave) {
        return clave != null && hashClave.equalsIgnoreCase(HashSHA256.calcular(clave));
    }

    @Override
    public String toString() {
        return nombre + " (" + rol.getDescripcion() + ")";
    }
}
