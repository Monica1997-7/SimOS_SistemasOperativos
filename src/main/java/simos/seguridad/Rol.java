package simos.seguridad;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * Roles del simulador y los permisos que cada uno concede.
 */
public enum Rol {
    ADMINISTRADOR("Administrador", EnumSet.allOf(Permiso.class)),
    USUARIO("Usuario", EnumSet.of(
            Permiso.CPU,
            Permiso.CONCURRENCIA,
            Permiso.MEMORIA,
            Permiso.ALMACENAMIENTO));

    private final String descripcion;
    private final Set<Permiso> permisos;

    Rol(String descripcion, Set<Permiso> permisos) {
        this.descripcion = descripcion;
        this.permisos = Collections.unmodifiableSet(permisos);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public boolean tienePermiso(Permiso permiso) {
        return permisos.contains(permiso);
    }

    public static Rol desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El rol está vacío");
        }
        return switch (texto.trim().toUpperCase(Locale.ROOT)) {
            case "ADMIN", "ADMINISTRADOR" -> ADMINISTRADOR;
            case "USUARIO", "USER" -> USUARIO;
            default -> throw new IllegalArgumentException("Rol desconocido: " + texto);
        };
    }
}
