
package simos.concurrencia;

import java.util.Scanner;

// Clase encargada de mostrar el menu y ejecutar las simulaciones de concurrencia
public class ModuloConcurrencia {

    // Muestra el menu del modulo 2 y recibe la opcion del usuario
    public static void ejecutar(Scanner scanner) {

        int opcion;

        // Repite el menu hasta que el usuario seleccione la opcion de salir
        do {
            System.out.println("\n====================================");
            System.out.println("       MODULO 2 - CONCURRENCIA");
            System.out.println("====================================");
            System.out.println("1. Contador sin sincronizacion");
            System.out.println("2. Contador con sincronizacion");
            System.out.println("3. Productor - Consumidor");
            System.out.println("4. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");

            // Lee la opcion ingresada por el usuario
            String entrada = scanner.nextLine().trim();

            // Convierte la entrada a numero y controla valores no validos
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            // Ejecuta la simulacion correspondiente a la opcion seleccionada
            switch (opcion) {

                case 1:
                    ejecutarContador(scanner, false);
                    break;

                case 2:
                    ejecutarContador(scanner, true);
                    break;

                case 3:
                    ejecutarProductorConsumidor(scanner);
                    break;

                case 4:
                    System.out.println("Saliendo del modulo 2...");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 4);
    }

    // Configura y ejecuta la simulacion de productor-consumidor
    public static void ejecutarProductorConsumidor(Scanner scanner) {

        System.out.println("\n========================================");
        System.out.println("      SIMULACION PRODUCTOR-CONSUMIDOR");
        System.out.println("========================================");

        System.out.print("Cantidad de elementos a procesar: ");

        int cantidad;

        // Lee la cantidad de elementos y verifica que sea un numero entero
        try {
            cantidad = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un numero entero.");
            return;
        }

        // Evita ejecutar la simulacion con cantidades menores o iguales a cero
        if (cantidad <= 0) {
            System.out.println("Error: la cantidad debe ser mayor que cero.");
            return;
        }

        System.out.println("\nCapacidad del buffer: 5");
        System.out.println("Elementos a procesar: " + cantidad);
        System.out.println("----------------------------------------");

        // Crea el buffer que compartiran el productor y el consumidor
        BufferCompartido buffer = new BufferCompartido();

        // Crea el hilo productor con la cantidad de elementos indicada
        Productor productor = new Productor(
                buffer, cantidad, "Productor 1");

        // Crea el hilo consumidor para retirar los elementos producidos
        Consumidor consumidor = new Consumidor(
                buffer, cantidad, "Consumidor 1");

        // Inicia la ejecucion de ambos hilos
        productor.start();
        consumidor.start();

        // Espera a que los dos hilos terminen antes de finalizar la simulacion
        try {
            productor.join();
            consumidor.join();

        } catch (InterruptedException e) {
            System.out.println("Simulacion interrumpida.");
            Thread.currentThread().interrupt();
            return;
        }

        // Muestra el mensaje final de la simulacion
        System.out.println("\n========================================");
        System.out.println("          SIMULACION FINALIZADA");
        System.out.println("========================================");
        System.out.println("Elementos procesados: " + cantidad);
    }

    // Solicita los datos necesarios para ejecutar el contador compartido
    public static void ejecutarContador(Scanner scanner, boolean sincronizado) {

        System.out.println("\n========================================");
        System.out.println("       CONFIGURACION DEL CONTADOR");
        System.out.println("========================================");

        System.out.print("Cantidad de hilos: ");
        int cantidadHilos;

        // Lee y valida la cantidad de hilos ingresada
        try {
            cantidadHilos = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero entero.");
            return;
        }

        System.out.print("Incrementos por hilo: ");
        int incrementosPorHilo;

        // Lee y valida la cantidad de incrementos por hilo
        try {
            incrementosPorHilo = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero entero.");
            return;
        }

        // Verifica que los valores ingresados sean mayores que cero
        if (cantidadHilos <= 0 || incrementosPorHilo <= 0) {
            System.out.println("Error: ambos valores deben ser mayores que cero.");
            return;
        }

        // Evita que el resultado esperado supere el limite del tipo int
        if ((long) cantidadHilos * incrementosPorHilo > Integer.MAX_VALUE) {
            System.out.println("Error: la cantidad total de incrementos es demasiado grande.");
            return;
        }

        // Ejecuta el contador con o sin sincronizacion segun la opcion elegida
        if (sincronizado) {
            ContadorCompartido.ejecutarConSincronizacion(
                    cantidadHilos, incrementosPorHilo);
        } else {
            ContadorCompartido.ejecutarSinSincronizacion(
                    cantidadHilos, incrementosPorHilo);
        }
    }
}
