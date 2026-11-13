package simos;

import java.util.Optional;
import java.util.Scanner;
import simos.seguridad.Bitacora;
import simos.seguridad.GestorUsuarios;
import simos.seguridad.ModuloSeguridad;
import simos.seguridad.Permiso;
import simos.seguridad.Usuario;

/**
 * Punto de entrada: login, menú principal y control de permisos por rol.
 */
public final class Main {

    private static final int MAX_INTENTOS_LOGIN = 3;
    private static final String RUTA_BITACORA = "bitacora.txt";

    private final Scanner sc;
    private final GestorUsuarios gestorUsuarios;
    private final Bitacora bitacora;

    private Main(Scanner sc) {
        this.sc = sc;
        this.gestorUsuarios = new GestorUsuarios();
        this.bitacora = new Bitacora(RUTA_BITACORA);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            new Main(sc).ejecutar();
        } finally {
            sc.close();
        }
    }

    private void ejecutar() {
        imprimirEncabezado();
        Usuario usuario = iniciarSesion();
        if (usuario == null) {
            System.out.println("Se agotaron los intentos. El programa finalizará.");
            return;
        }
        menuPrincipal(usuario);
    }

    private void imprimirEncabezado() {
        System.out.println("==========================================");
        System.out.println("   SimOS - Simulador de Sistemas Operativos");
        System.out.println("==========================================");
    }

    private Usuario iniciarSesion() {
        for (int intento = 1; intento <= MAX_INTENTOS_LOGIN; intento++) {
            System.out.println();
            System.out.print("Usuario: ");
            String nombre = leerLinea();
            if (nombre == null) {
                return null;
            }
            System.out.print("Clave: ");
            String clave = leerClave();
            if (clave == null) {
                return null;
            }

            Optional<Usuario> autenticado = gestorUsuarios.autenticar(nombre, clave);
            if (autenticado.isPresent()) {
                Usuario usuario = autenticado.get();
                bitacora.registrar(usuario.getNombre(), "LOGIN", true);
                System.out.println("Bienvenido(a), " + usuario.getNombre()
                        + " (" + usuario.getRol().getDescripcion() + ").");
                return usuario;
            }

            bitacora.registrar(nombre, "LOGIN", false);
            System.out.println("Credenciales inválidas. Intentos restantes: "
                    + (MAX_INTENTOS_LOGIN - intento));
        }
        return null;
    }

    private void menuPrincipal(Usuario usuario) {
        while (true) {
            System.out.println();
            System.out.println("=== MENÚ PRINCIPAL ===");
            System.out.println("Sesión: " + usuario.getNombre()
                    + " | Rol: " + usuario.getRol().getDescripcion());
            System.out.println("1. Planificación de CPU");
            System.out.println("2. Concurrencia");
            System.out.println("3. Memoria");
            System.out.println("4. Almacenamiento");
            System.out.println("5. Seguridad");
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            String opcion = leerLinea();
            if (opcion == null) {
                System.out.println();
                System.out.println("Entrada finalizada. Cerrando sesión...");
                bitacora.registrar(usuario.getNombre(), "CERRAR SESION", true);
                return;
            }

            switch (opcion) {
                case "1" -> abrirModulo(usuario, Permiso.CPU,
                        "MÓDULO 1 - CPU",
                        () -> moduloPendiente("Planificación de CPU"));
                case "2" -> abrirModulo(usuario, Permiso.CONCURRENCIA,
                        "MÓDULO 2 - CONCURRENCIA",
                        () -> moduloPendiente("Concurrencia"));
                case "3" -> abrirModulo(usuario, Permiso.MEMORIA,
                        "MÓDULO 3 - MEMORIA",
                        () -> moduloPendiente("Memoria"));
                case "4" -> abrirModulo(usuario, Permiso.ALMACENAMIENTO,
                        "MÓDULO 4 - ALMACENAMIENTO",
                        () -> moduloPendiente("Almacenamiento"));
                case "5" -> abrirModulo(usuario, Permiso.SEGURIDAD,
                        "MÓDULO 5 - SEGURIDAD",
                        () -> new ModuloSeguridad(gestorUsuarios, bitacora).ejecutar(sc));
                case "0" -> {
                    bitacora.registrar(usuario.getNombre(), "CERRAR SESION", true);
                    System.out.println("Sesión finalizada. Hasta pronto.");
                    return;
                }
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    /**
     * Verifica el permiso antes de ejecutar el módulo. Un intento denegado
     * se registra en la bitácora y el módulo no se abre.
     */
    private void abrirModulo(Usuario usuario, Permiso permiso, String nombreModulo, Runnable accion) {
        if (!usuario.getRol().tienePermiso(permiso)) {
            bitacora.registrar(usuario.getNombre(), "ABRIR " + nombreModulo, false);
            System.out.println("Acceso denegado: el rol " + usuario.getRol().getDescripcion()
                    + " no tiene permiso para entrar a " + nombreModulo + ".");
            return;
        }
        bitacora.registrar(usuario.getNombre(), "ABRIR " + nombreModulo, true);
        accion.run();
    }

    private void moduloPendiente(String nombre) {
        System.out.println();
        System.out.println(nombre + ": módulo aún no implementado.");
        System.out.println("Se habilitará en una fase posterior del proyecto.");
    }

    private String leerLinea() {
        if (!sc.hasNextLine()) {
            return null;
        }
        return sc.nextLine().trim();
    }

    private String leerClave() {
        if (!sc.hasNextLine()) {
            return null;
        }
        return sc.nextLine();
    }
}
