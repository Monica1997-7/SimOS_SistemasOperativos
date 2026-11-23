package simos; // Define que esta clase pertenece al paquete principal del simulador.

// --- IMPORTACIONES ---
// Aquí se le dice a Java qué herramientas externas va a necesitar esta clase para funcionar.
import java.util.Optional; // Herramienta para manejar valores que pueden existir o estar vacíos (evita errores de "null").
import java.util.Scanner; // Herramienta para leer lo que el usuario escribe por teclado.
// --- CONEXIÓN CON EL MÓDULO 1 (AGREGADO EN ESTA FASE) ---
// ModuloCPU contiene el menú de planificación de CPU. Al importarlo, el Main puede
// crear un objeto de ese módulo cuando el usuario elige la opción 1 del menú principal.
import simos.cpu.ModuloCPU; // Trae la interfaz del menú de planificación de CPU (Módulo 1).
import simos.seguridad.Bitacora; // Trae la clase que guarda el historial de acciones.
import simos.seguridad.GestorUsuarios; // Trae la clase que administra los usuarios.
import simos.seguridad.ModuloSeguridad;// Trae la interfaz del menú de seguridad (Módulo 5).
import simos.seguridad.Permiso; // Trae la lista de permisos (CPU, Memoria, etc.).
import simos.seguridad.Usuario; // Trae el molde para crear objetos de tipo Usuario.

/**
 * Clase principal del programa (Main).
 * Su función es: mostrar el login, arrancar el menú y controlar los permisos.
 *
 * Recibe: los datos que escribe el usuario por teclado (nombre, clave y
 * opciones de menú).
 * Internamente:
 * 1. Crea el lector de teclado, el gestor de usuarios y la bitácora.
 * 2. Pide usuario y contraseña (máximo 3 intentos).
 * 3. Muestra el menú principal y revisa permisos antes de abrir cada módulo.
 * 4. Para la opción 1, delega el trabajo al nuevo Módulo 1 (ModuloCPU).
 * Devuelve: nada desde sus métodos principales; cuando termina, el programa
 * finaliza.
 */
public final class Main {

    // --- CONSTANTES DE CONFIGURACIÓN ---
    // 'static final' significa que es un valor fijo que no cambia y que pertenece a
    // toda la clase.
    private static final int MAX_INTENTOS_LOGIN = 3; // Límite de intentos de contraseña.
    private static final String RUTA_BITACORA = "bitacora.txt"; // El nombre del archivo donde se guardará el historial.

    // --- VARIABLES GLOBALES DE LA CLASE ---
    // El mismo Scanner se pasa a los módulos (Seguridad y CPU) para que todos lean
    // del mismo teclado: si cada uno creara el suyo, podrían perderse datos entre
    // ellos.
    private final Scanner sc; // Para leer el teclado.
    private final GestorUsuarios gestorUsuarios; // Para validar contraseñas.
    private final Bitacora bitacora; // Para escribir en el archivo de texto.

    /**
     * Constructor de la clase.
     * Se ejecuta automáticamente cuando se crea un objeto de tipo Main.
     * Recibe el Scanner y prepara el gestor de usuarios y la bitácora.
     *
     * Recibe: el Scanner creado en main(), que sirve para leer todo lo que teclea
     * el usuario.
     * Internamente: guarda ese Scanner en el atributo 'sc', crea el GestorUsuarios
     * (que carga usuarios.txt) y crea la Bitacora (donde se anotarán las acciones).
     * Devuelve: nada; los constructores no llevan tipo de retorno.
     */
    private Main(Scanner sc) {
        this.sc = sc;
        this.gestorUsuarios = new GestorUsuarios(); // Carga el archivo usuarios.txt
        this.bitacora = new Bitacora(RUTA_BITACORA); // Prepara el archivo bitacora.txt
    }

    /**
     * Método 'main'. Es el punto de entrada del programa (donde Java empieza a
     * ejecutar).
     *
     * Recibe: args, los argumentos de la línea de comandos (aquí no se usan).
     * Internamente: crea el Scanner sobre System.in, llama a ejecutar() y,
     * gracias al 'finally', cierra el Scanner pase lo que pase.
     * Devuelve: nada (es 'void').
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // Prepara el lector de teclado.
        try {
            // Crea una nueva instancia de Main y llama al método 'ejecutar'.
            new Main(sc).ejecutar();
        } finally {
            // 'finally' asegura que, pase lo que pase, el Scanner se cierre al final para
            // liberar memoria.
            sc.close();
        }
    }

    /**
     * Método principal que controla el flujo del programa.
     *
     * Recibe: nada (usa los atributos de la clase).
     * Internamente: imprime el encabezado, intenta iniciar sesión y, si funciona,
     * entra al menú principal. Si falla el login, termina con 'return'.
     * Devuelve: nada.
     */
    private void ejecutar() {
        imprimirEncabezado(); // Muestra el título bonito de SimOS.
        Usuario usuario = iniciarSesion(); // Llama al login. Devuelve un Usuario si tuvo éxito, o 'null' si falló.

        // Si el usuario es null (falló 3 veces), muestra error y termina el programa
        // con 'return'.
        if (usuario == null) {
            System.out.println("Se agotaron los intentos. El programa finalizará.");
            return;
        }

        // Si el login fue exitoso, entra al menú principal pasándole el usuario
        // logueado.
        menuPrincipal(usuario);
    }

    /**
     * Imprime el cartel de bienvenida en la consola.
     */
    private void imprimirEncabezado() {
        System.out.println("==========================================");
        System.out.println("   SimOS - v1.0");
        System.out.println("==========================================");
    }

    /**
     * Maneja la lógica de inicio de sesión.
     * Retorna el objeto Usuario si se loguea bien, o null si se acaban los
     * intentos.
     */
    private Usuario iniciarSesion() {
        // Ciclo for: empieza en el intento 1, sigue mientras sea <= a 3, y suma 1 en
        // cada vuelta.
        for (int intento = 1; intento <= MAX_INTENTOS_LOGIN; intento++) {
            System.out.println();
            System.out.print("Usuario: ");
            String nombre = leerLinea();

            // Si el usuario presiona Ctrl+Z o se corta la entrada, devuelve null para
            // cerrar.
            if (nombre == null) {
                return null;
            }

            System.out.print("Clave: ");
            String clave = leerClave();
            if (clave == null) {
                return null;
            }

            // Le pide al GestorUsuarios que verifique si el nombre y la clave existen.
            // 'Optional' es una caja que puede tener un Usuario adentro, o estar vacía.
            Optional<Usuario> autenticado = gestorUsuarios.autenticar(nombre, clave);

            // Si la caja NO está vacía (las credenciales son correctas):
            if (autenticado.isPresent()) {
                Usuario usuario = autenticado.get(); // Saca el usuario de la caja.

                // Escribe en la bitácora: "El usuario X inició sesión con éxito (PERMITIDO)".
                bitacora.registrar(usuario.getNombre(), "LOGIN", true);

                // Imprime mensaje de bienvenida con su rol.
                System.out.println("Bienvenido(a), " + usuario.getNombre()
                        + " (" + usuario.getRol().getDescripcion() + ").");
                return usuario; // Retorna el usuario y rompe el ciclo for.
            }

            // Si la clave está mal, escribe en la bitácora el intento fallido (DENEGADO).
            bitacora.registrar(nombre, "LOGIN", false);

            // Hace la resta para mostrar cuántos intentos quedan (Ej: 3 - 1 = 2).
            System.out.println("Credenciales inválidas. Intentos restantes: "
                    + (MAX_INTENTOS_LOGIN - intento));
        }
        return null; // Si el ciclo for termina (se acabaron los 3 intentos), retorna null.
    }

    /**
     * Muestra el menú de opciones una vez el usuario ya inició sesión.
     *
     * Recibe: el Usuario que acaba de loguearse, para saber su rol y sus permisos.
     * Internamente: repite el menú en un bucle mientras no se elija salir.
     * Cada opción llama a abrirModulo(...), que revisa el permiso antes de
     * ejecutar.
     * La opción 1 es la que conecta con el Módulo 1 de CPU recién implementado.
     * Devuelve: nada; termina con 'return' al elegir 0 o al cerrarse la entrada.
     */
    private void menuPrincipal(Usuario usuario) {
        // while(true) crea un ciclo infinito. El menú se repetirá hasta que se use un
        // 'return'.
        while (true) {
            System.out.println();
            System.out.println("=== MENÚ PRINCIPAL ===");
            // Muestra quién está usando el sistema.
            System.out.println("Usuario: " + usuario.getNombre()
                    + " | Rol: " + usuario.getRol().getDescripcion());
            System.out.println("1. Planificación de CPU");
            System.out.println("2. Concurrencia");
            System.out.println("3. Memoria");
            System.out.println("4. Almacenamiento");
            System.out.println("5. Seguridad");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = leerLinea(); // Lee lo que el usuario escribió.

            // Si no hay entrada (null), cierra sesión y sale del menú.
            if (opcion == null) {
                System.out.println();
                System.out.println("Entrada finalizada. Cerrando sesión...");
                bitacora.registrar(usuario.getNombre(), "CERRAR SESION", true);
                return;
            }

            // Estructura 'switch': evalúa la opción y ejecuta el caso correspondiente.
            // (La flechita '->' es una forma moderna en Java de escribir los casos sin usar
            // 'break').
            // --- CONEXIÓN DEL MENÚ CON EL MÓDULO 1 ---
            // Si el usuario escribe "1", seguimos esta cadena:
            // abrirModulo(..., Permiso.CPU, ...) revisa que el rol tenga permiso de CPU;
            // si lo tiene, ejecuta la "lambda" (el bloque () -> ...);
            // la lambda crea un ModuloCPU y llama a su método ejecutar(sc);
            // dentro de ModuloCPU aparece el submenú: teclado, archivo o volver.
            // Le pasamos 'sc' para que el módulo lea del MISMO teclado que el Main.
            switch (opcion) {

                // Esta flecha '->' define una lambda: guarda una acción para ejecutarla
                // después.
                case "1" -> abrirModulo(usuario, Permiso.CPU,
                        "MÓDULO 1 - CPU",
                        () -> new ModuloCPU().ejecutar(sc));
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
                        // Aquí sí entra al Módulo 5 real llamando a su clase.
                        () -> new ModuloSeguridad(gestorUsuarios, bitacora).ejecutar(sc));
                case "0" -> {
                    // Si elige 0, registra el cierre, imprime mensaje y termina el menú.
                    bitacora.registrar(usuario.getNombre(), "CERRAR SESION", true);
                    System.out.println("Sesión finalizada. Hasta pronto.");
                    return;
                }
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    /**
     * Método vital para la seguridad: revisa si el usuario tiene permiso antes de
     * dejarlo entrar a un módulo.
     * Usa una 'lambda' (Runnable accion) que es como una cajita que guarda el
     * código del módulo a ejecutar.
     *
     * Recibe:
     * - usuario: quien inició sesión (de él sacamos el rol).
     * - permiso: el permiso necesario (Permiso.CPU para el Módulo 1).
     * - nombreModulo: texto usado en la bitácora y en el mensaje de acceso.
     * - accion: la lambda con el código del módulo; para CPU es
     * () -> new ModuloCPU().ejecutar(sc).
     * Internamente: pregunta al rol si tiene el permiso; si no, registra el intento
     * denegado y se detiene; si sí, registra el permitido y ejecuta la acción.
     * Devuelve: nada. Ojo: la acción NO se ejecuta si el permiso falta.
     */
    private void abrirModulo(Usuario usuario, Permiso permiso, String nombreModulo, Runnable accion) {
        // Si el rol del usuario NO tiene el permiso requerido...
        if (!usuario.getRol().tienePermiso(permiso)) {
            // 1. Lo anota en la bitácora como denegado.
            bitacora.registrar(usuario.getNombre(), "ABRIR " + nombreModulo, false);
            // 2. Muestra el mensaje de error en consola.
            System.out.println("Acceso denegado: el rol " + usuario.getRol().getDescripcion()
                    + " no tiene permiso para entrar a " + nombreModulo + ".");
            return; // 3. Sale del método sin ejecutar el módulo.
        }
        // Si sí tiene permiso, lo anota en la bitácora como permitido y ejecuta la
        // acción.
        // Para la opción 1, 'accion.run()' es lo que hace aparecer el menú de
        // ModuloCPU.
        bitacora.registrar(usuario.getNombre(), "ABRIR " + nombreModulo, true);
        accion.run();
    }

    /**
     * Un método temporal para avisar que los módulos 2, 3 y 4 aún no están
     * programados.
     *
     * Recibe: el nombre del módulo que todavía no existe.
     * Internamente: imprime dos avisos en consola. El Módulo 1 (CPU) ya NO usa este
     * método: ahora abre directamente ModuloCPU desde el case "1" del menú.
     * Devuelve: nada.
     */
    private void moduloPendiente(String nombre) {
        System.out.println();
        System.out.println(nombre + ": módulo aún no implementado.");
        System.out.println("Se habilitará en una fase posterior del proyecto.");
    }

    /**
     * Método auxiliar para leer una línea de texto del teclado.
     * Si el usuario no escribe nada y presiona Enter, devuelve null.
     */
    private String leerLinea() {
        if (!sc.hasNextLine()) {
            return null;
        }
        return sc.nextLine().trim(); // .trim() borra los espacios en blanco al inicio y al final.
    }

    /**
     * Método auxiliar para leer la contraseña.
     * Es igual a leerLinea() pero SIN usar .trim(), porque una contraseña podría
     * tener espacios a propósito.
     */
    private String leerClave() {
        if (!sc.hasNextLine()) {
            return null;
        }
        return sc.nextLine();
    }
}