package simos.cpu;

// IOException: se puede producir al leer el archivo de procesos.
// List: guardamos la lista de procesos y las listas del resultado.
// Locale: lo usamos para que los decimales se impriman con punto (11.00 y no 11,00).
// Scanner: es el mismo teclado que usa el Main.
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Interfaz de consola del Módulo 1 (Planificación de CPU).
 * Permite cargar procesos, ejecutar FCFS y Round Robin y comparar sus promedios de espera.
 *
 * Esta clase es la "parte visible" del módulo: muestra menús, pide datos,
 * llama al PlanificadorCPU para que calcule y luego imprime el diagrama de Gantt,
 * la tabla de resultados y la conclusión final.
 */
public final class ModuloCPU {

    // Ancho de las líneas divisoras del reporte (62 caracteres).
    // Se usa con "=".repeat(ANCHO) y "-".repeat(ANCHO).
    private static final int ANCHO = 62;

    // El planificador es el que hace los cálculos.
    private final PlanificadorCPU planificador = new PlanificadorCPU();

    // El lector es el que trae los procesos del teclado o del archivo.
    private final LectorProcesos lector = new LectorProcesos();

    /**
     * Menú interno; se llama desde Main al elegir la opción 1.
     *
     * Recibe: el Scanner del Main, para leer las respuestas del usuario.
     * Internamente: muestra el menú, lee una opción y ejecuta la acción correspondiente
     * hasta que el usuario elige volver.
     * Devuelve: nada; al terminar imprime "Volviendo al menú principal...".
     */
    public void ejecutar(Scanner sc) {
        // Bandera: mientras sea false seguimos dentro del menú del módulo.
        boolean volver = false;
        while (!volver) {
            // Mostramos las opciones del módulo.
            imprimirMenu();
            // Lee la opción escrita por el usuario.
            String opcion = leerLinea(sc);
            // Si se cerró la entrada de datos, salimos del bucle.
            if (opcion == null) {
                volver = true;
                continue;
            }
            // Según la opción: cargar por teclado, cargar por archivo o salir.
            switch (opcion) {
                case "1" -> simular(sc, true);
                case "2" -> simular(sc, false);
                case "3", "0" -> volver = true;
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
        // Avisamos al usuario de que regresamos al menú del Main.
        System.out.println("Volviendo al menú principal...");
    }

    /**
     * Dibuja las opciones del submenú de CPU en la consola.
     * Recibe: nada. Internamente solo imprime texto. Devuelve: nada.
     */
    private void imprimirMenu() {
        //println() sin texto deja una línea en blanco para separar menús.
        System.out.println();
        System.out.println("=== MÓDULO 1: PLANIFICACIÓN DE CPU ===");
        System.out.println("1. Ingresar procesos por teclado");
        System.out.println("2. Leer procesos desde procesos.txt");
        System.out.println("3. Volver al menú principal");
        // print() (sin 'ln') deja el cursor en la misma línea para que el usuario escriba.
        System.out.print("Opción: ");
    }

    /**
     * Ejecuta una simulación completa: carga procesos, pide el quantum,
     * calcula FCFS y Round Robin y muestra los resultados.
     *
     * Recibe:
     *  - sc: el teclado compartido.
     *  - desdeTeclado: true si los datos vienen del usuario, false si vienen del archivo.
     * Internamente: elige el lector, valida los datos, llama a los dos algoritmos
     * y manda imprimir los tres bloques.
     * Devuelve: nada; si algo falla, muestra un mensaje y vuelve al menú.
     */
    private void simular(Scanner sc, boolean desdeTeclado) {
        // Aquí guardaremos los procesos cargados.
        List<Proceso> procesos;
        try {
            // Operador ternario: si desdeTeclado es true llama a leerTeclado, si no a leerArchivo.
            procesos = desdeTeclado ? lector.leerTeclado(sc) : lector.leerArchivo();
        } catch (IOException | IllegalArgumentException e) {
            //Si el archivo no existe o los datos están mal, avisamos y salimos del método.
            System.out.println("Error: " + e.getMessage());
            return;
        }

        // No tiene sentido simular sin procesos.
        if (procesos.isEmpty()) {
            System.out.println("No hay procesos para simular.");
            return;
        }

        // Pedimos el quantum al usuario; se usa solo para Round Robin.
        int quantum = pedirQuantum(sc);
        if (quantum <= 0) {
            // Un quantum inválido también detiene la simulación.
            System.out.println("El quantum debe ser mayor que cero.");
            return;
        }

        // Mostramos qué procesos se van a simular, usando el toString de Proceso.
        System.out.println();
        System.out.println("Procesos: " + procesos);

        // Calculamos los dos algoritmos con la MISMA lista de procesos (comparación justa).
        ResultadoCPU fcfs = planificador.fcfs(procesos);
        ResultadoCPU roundRobin = planificador.roundRobin(procesos, quantum);

        // Imprimimos cada resultado y luego la comparación entre ambos.
        imprimirResultado(fcfs);
        imprimirResultado(roundRobin);
        imprimirConclusion(fcfs, roundRobin);
    }

    /**
     * Pide el quantum para Round Robin.
     * Recibe: el Scanner del teclado.
     * Internamente: imprime la pregunta, lee una línea y la convierte a entero.
     * Devuelve: el quantum, o 0 si el usuario no escribió un número válido
     * (así simular() puede mostrar el mensaje de error).
     */
    private static int pedirQuantum(Scanner sc) {
        System.out.print("Quantum para Round Robin: ");
        String linea = leerLinea(sc);
        if (linea == null) {
            // Entrada cortada: devolvemos 0.
            return 0;
        }
        try {
            // Convertimos el texto "3" en el número 3.
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            // Si escribió letras, devolvemos 0.
            return 0;
        }
    }

    /**
     * Imprime el reporte completo de un algoritmo: título, Gantt, tabla y promedio.
     * Recibe: el ResultadoCPU ya calculado.
     * Internamente: usa System.out.printf para alinear columnas con anchos fijos.
     * Devuelve: nada.
     */
    private static void imprimirResultado(ResultadoCPU resultado) {
        System.out.println();
        // "=".repeat(ANCHO) repite el signo 62 veces para hacer una raya de título.
        System.out.println("=".repeat(ANCHO));
        // El nombre del algoritmo viene dentro del resultado.
        System.out.println(resultado.getAlgoritmo());
        System.out.println("=".repeat(ANCHO));

        // --- DIAGRAMA DE GANTT ---
        System.out.println();
        System.out.println("Diagrama de Gantt:");
        // construirGantt arma la cadena "| P1 | P2 | ... |" y la imprime de una vez.
        System.out.println(construirGantt(resultado));

        // --- TABLA DE RESULTADOS ---
        System.out.println();
        System.out.println("Tabla de resultados:");
        // printf con %-8s alinea texto a la izquierda en 8 caracteres;
        // %8s y %8d alinean a la derecha en 8 caracteres (números y encabezados).
        System.out.printf("%-8s %8s %8s %8s %9s %8s%n",
                "Nombre", "Llegada", "Ráfaga", "Fin", "Retorno", "Espera");
        System.out.println("-".repeat(ANCHO));
        // Recorremos cada fila del resultado y pintamos sus seis datos.
        for (ResultadoCPU.Fila fila : resultado.getFilas()) {
            System.out.printf("%-8s %8d %8d %8d %9d %8d%n",
                    fila.getNombre(), fila.getLlegada(), fila.getRafaga(),
                    fila.getFin(), fila.getRetorno(), fila.getEspera());
        }
        System.out.println("-".repeat(ANCHO));
        // %.2f imprime dos decimales. Locale.ROOT asegura que salga un PUNTO decimal
        // aunque la computadora esté configurada en español (donde se usaría una coma).
        System.out.printf(Locale.ROOT, "Promedio de espera: %.2f%n", resultado.getPromedioEspera());
    }

    /**
     * Construye el diagrama de Gantt en texto.
     * Recibe: el resultado con la lista de segmentos.
     * Internamente: por cada segmento escribe "| nombre " y al final cierra con "|".
     * Devuelve: una cadena como "| P1 | P2 | P1 |".
     *
     * Ejemplo: los segmentos P1(0-3), P2(3-6), P1(6-8) producen
     * "| P1 | P2 | P1 |", que es la forma clásica de dibujar el Gantt en consola.
     */
    private static String construirGantt(ResultadoCPU resultado) {
        // StringBuilder es como un texto "en construcción" al que se le agrega partes.
        StringBuilder gantt = new StringBuilder();
        // Recorremos los segmentos en el orden en que la CPU los usó.
        for (ResultadoCPU.Segmento segmento : resultado.getSegmentos()) {
            // Agregamos la barra inicial, un espacio, el nombre y otro espacio.
            gantt.append("| ").append(segmento.getNombre()).append(" ");
        }
        // Al final agregamos la barra de cierre y convertimos todo a String.
        return gantt.append("|").toString();
    }

    /**
     * Compara los promedios de espera de los dos algoritmos y escribe la conclusión.
     * Recibe: los dos resultados ya calculados.
     * Internamente: compara con < y > y decide cuál de los tres mensajes imprimir.
     * Devuelve: nada.
     */
    private static void imprimirConclusion(ResultadoCPU fcfs, ResultadoCPU roundRobin) {
        // Guardamos los promedios en variables más cortas para compararlos.
        double esperaFcfs = fcfs.getPromedioEspera();
        double esperaRr = roundRobin.getPromedioEspera();

        // Encabezado de la sección final.
        System.out.println();
        System.out.println("=".repeat(ANCHO));
        System.out.println("CONCLUSIÓN");
        System.out.println("=".repeat(ANCHO));

        // Caso 1: FCFS espera menos -> es el mejor para estos datos.
        if (esperaFcfs < esperaRr) {
            System.out.printf(Locale.ROOT,
                    "FCFS es mejor: promedio de espera %.2f frente a %.2f de Round Robin.%n",
                    esperaFcfs, esperaRr);
        // Caso 2: Round Robin espera menos -> gana Round Robin.
        } else if (esperaRr < esperaFcfs) {
            System.out.printf(Locale.ROOT,
                    "Round Robin es mejor: promedio de espera %.2f frente a %.2f de FCFS.%n",
                    esperaRr, esperaFcfs);
        // Caso 3: son iguales -> hay empate.
        } else {
            System.out.printf(Locale.ROOT,
                    "Ambos algoritmos empatan con un promedio de espera de %.2f.%n", esperaFcfs);
        }
    }

    /**
     * Lee una línea del teclado, le quita espacios sobrantes y devuelve null
     * si ya no hay más entrada. Es el mismo patrón que usa el Main.
     */
    private static String leerLinea(Scanner sc) {
        return sc.hasNextLine() ? sc.nextLine().trim() : null;
    }
}
