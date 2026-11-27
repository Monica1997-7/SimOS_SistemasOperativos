package simos.almacenamiento;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ModuloAlmacenamiento {

    public static void ejecutar(Scanner sc) {
        System.out.println("\n===== Planificación de disco =====");
        System.out.println("1. Usar el caso de prueba");
        System.out.println("2. Ingresar datos");
        System.out.println("0. Volver");
        int opcion = leerEntero(sc, "Opción: ", 0, 2);

        if (opcion == 1) {
            simular(70, 0, 199, List.of(130, 25, 160, 45, 90, 10, 185, 75), true);
        } else if (opcion == 2) {
            int min = leerEntero(sc, "Cilindro mínimo: ", 0, Integer.MAX_VALUE - 1);
            int max = leerEntero(sc, "Cilindro máximo: ", min + 1, Integer.MAX_VALUE);
            int cabezal = leerEntero(sc, "Posición inicial del cabezal: ", min, max);
            List<Integer> cola = leerCola(sc, min, max);
            int direccion = leerEntero(sc, "Dirección de SCAN (1 = hacia los mayores, 2 = hacia los menores): ", 1, 2);
            simular(cabezal, min, max, cola, direccion == 1);
        }
    }

    private static void simular(int cabezal, int min, int max, List<Integer> cola, boolean sube) {
        System.out.println("\nCilindros: " + min + " a " + max);
        System.out.println("Cabezal inicial: " + cabezal);
        System.out.println("Cola: " + cola);
        System.out.println("Dirección de SCAN: hacia los " + (sube ? "mayores" : "menores"));

        String[] nombres = {"FCFS", "SSTF", "SCAN"};
        List<List<Integer>> recorridos = List.of(
                fcfs(cola),
                sstf(cabezal, cola),
                scan(cabezal, min, max, cola, sube));
        long[] totales = new long[nombres.length];

        for (int i = 0; i < nombres.length; i++) {
            System.out.println("\n--- " + nombres[i] + " ---");
            totales[i] = mostrarRecorrido(cabezal, recorridos.get(i), cola);
        }

        long menor = Math.min(totales[0], Math.min(totales[1], totales[2]));
        List<String> mejores = new ArrayList<>();
        System.out.println("\n--- Comparación ---");
        for (int i = 0; i < nombres.length; i++) {
            System.out.printf("%-6s %d cilindros%n", nombres[i], totales[i]);
            if (totales[i] == menor) {
                mejores.add(nombres[i]);
            }
        }
        System.out.println("Mejor: " + String.join(" y ", mejores) + " (" + menor + " cilindros)");
    }

    // Atiende las solicitudes en el orden en que llegaron
    static List<Integer> fcfs(List<Integer> cola) {
        return new ArrayList<>(cola);
    }

    // Atiende siempre la solicitud más cercana al cabezal.
    // En caso de empate gana la que llegó primero.
    static List<Integer> sstf(int cabezal, List<Integer> cola) {
        List<Integer> pendientes = new ArrayList<>(cola);
        List<Integer> orden = new ArrayList<>();
        int actual = cabezal;

        while (!pendientes.isEmpty()) {
            int cercano = 0;
            for (int i = 1; i < pendientes.size(); i++) {
                if (Math.abs(pendientes.get(i) - actual) < Math.abs(pendientes.get(cercano) - actual)) {
                    cercano = i;
                }
            }
            actual = pendientes.remove(cercano);
            orden.add(actual);
        }
        return orden;
    }

    // Recorre en una dirección hasta el extremo del disco y luego se devuelve.
    // Solo va hasta el extremo si quedan solicitudes del otro lado.
    static List<Integer> scan(int cabezal, int min, int max, List<Integer> cola, boolean sube) {
        List<Integer> mayores = new ArrayList<>();
        List<Integer> menores = new ArrayList<>();

        for (int c : cola) {
            // una solicitud en la posición del cabezal se atiende de inmediato
            if (c > cabezal || (c == cabezal && sube)) {
                mayores.add(c);
            } else {
                menores.add(c);
            }
        }
        mayores.sort(null);
        menores.sort((a, b) -> b - a);

        List<Integer> primero = sube ? mayores : menores;
        List<Integer> segundo = sube ? menores : mayores;
        int extremo = sube ? max : min;

        List<Integer> recorrido = new ArrayList<>(primero);
        if (!segundo.isEmpty()) {
            if (!primero.contains(extremo)) {
                recorrido.add(extremo);
            }
            recorrido.addAll(segundo);
        }
        return recorrido;
    }

    // Imprime cada movimiento del cabezal y retorna el desplazamiento total.
    // Los puntos del recorrido que no están en la cola son el extremo del disco.
    private static long mostrarRecorrido(int cabezal, List<Integer> recorrido, List<Integer> cola) {
        List<Integer> atendidas = new ArrayList<>();
        int actual = cabezal;
        long total = 0;

        for (int destino : recorrido) {
            int distancia = Math.abs(destino - actual);
            total += distancia;
            String nota = "";
            if (cola.contains(destino)) {
                atendidas.add(destino);
            } else {
                nota = "  (extremo del disco)";
            }
            System.out.printf("  %d -> %d : %d%s%n", actual, destino, distancia, nota);
            actual = destino;
        }

        System.out.println("Orden de atención: " + atendidas);
        System.out.println("Desplazamiento total: " + total + " cilindros");
        return total;
    }

    private static int leerEntero(Scanner sc, String mensaje, int min, int max) {
        while (true) {
            System.out.print(mensaje);
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                if (valor >= min && valor <= max) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // se vuelve a pedir el dato
            }
            System.out.println("Valor no válido. Debe estar entre " + min + " y " + max + ".");
        }
    }

    private static List<Integer> leerCola(Scanner sc, int min, int max) {
        while (true) {
            System.out.print("Cola de solicitudes (separadas por coma o espacio): ");
            String linea = sc.nextLine().trim();
            List<Integer> cola = new ArrayList<>();
            try {
                for (String parte : linea.split("[,\\s]+")) {
                    int c = Integer.parseInt(parte);
                    if (c < min || c > max) {
                        throw new NumberFormatException();
                    }
                    cola.add(c);
                }
                return cola;
            } catch (NumberFormatException e) {
                System.out.println("Cola no válida. Use números entre " + min + " y " + max + ".");
            }
        }
    }
}
