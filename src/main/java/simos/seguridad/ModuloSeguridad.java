package simos.seguridad;

import java.util.List;
import java.util.Scanner;

/**
 * Módulo 5: consulta de usuarios y de la bitácora.
 * Solo se invoca cuando el rol de la sesión tiene el permiso de seguridad.
 */
public final class ModuloSeguridad {

    private static final int ULTIMOS_REGISTROS = 20;

    private final GestorUsuarios gestorUsuarios;
    private final Bitacora bitacora;

    public ModuloSeguridad(GestorUsuarios gestorUsuarios, Bitacora bitacora) {
        this.gestorUsuarios = gestorUsuarios;
        this.bitacora = bitacora;
    }

    public void ejecutar(Scanner sc) {
        boolean volver = false;
        while (!volver) {
            imprimirMenu();
            String opcion = leerLinea(sc);
            if (opcion == null) {
                volver = true;
                continue;
            }
            switch (opcion) {
                case "1" -> listarUsuarios();
                case "2" -> mostrarBitacora();
                case "3", "0" -> volver = true;
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
        System.out.println("Volviendo al menú principal...");
    }

    private void imprimirMenu() {
        System.out.println();
        System.out.println("=== MÓDULO 5: SEGURIDAD ===");
        System.out.println("1. Listar usuarios registrados");
        System.out.println("2. Consultar los últimos registros de la bitácora");
        System.out.println("3. Volver al menú principal");
        System.out.print("Opción: ");
    }

    private void listarUsuarios() {
        System.out.println();
        System.out.printf("%-14s | %-14s | %s%n", "USUARIO", "ROL", "PERMISOS");
        System.out.println("-".repeat(78));
        for (Usuario usuario : gestorUsuarios.listar()) {
            System.out.printf("%-14s | %-14s | %s%n",
                    usuario.getNombre(),
                    usuario.getRol().getDescripcion(),
                    permisosComoTexto(usuario.getRol()));
        }
        System.out.println();
    }

    private void mostrarBitacora() {
        System.out.println();
        System.out.println("Archivo: " + bitacora.getArchivo().toAbsolutePath());
        List<String> lineas = bitacora.leerUltimos(ULTIMOS_REGISTROS);
        if (lineas.isEmpty()) {
            System.out.println("No hay registros en la bitácora.");
            System.out.println();
            return;
        }

        System.out.printf("%-19s | %-14s | %-32s | %s%n",
                "FECHA/HORA", "USUARIO", "ACCIÓN", "RESULTADO");
        System.out.println("-".repeat(84));
        for (String linea : lineas) {
            String[] partes = linea.split(";", -1);
            if (partes.length == 4) {
                System.out.printf("%-19s | %-14s | %-32s | %s%n",
                        partes[0], partes[1], partes[2], partes[3]);
            } else {
                System.out.println(linea);
            }
        }
        System.out.println();
    }

    private static String permisosComoTexto(Rol rol) {
        StringBuilder texto = new StringBuilder();
        for (Permiso permiso : rol.getPermisos()) {
            if (texto.length() > 0) {
                texto.append(", ");
            }
            texto.append(permiso.getDescripcion());
        }
        return texto.toString();
    }

    private static String leerLinea(Scanner sc) {
        if (!sc.hasNextLine()) {
            return null;
        }
        return sc.nextLine().trim();
    }
}
