package simos.concurrencia;

import java.util.Scanner;

public class ModuloConcurrencia {

    public static void ejecutar(Scanner scanner) {

        int opcion;

        do {
            System.out.println("\n====================================");
            System.out.println("       MODULO 2 - CONCURRENCIA");
            System.out.println("====================================");
            System.out.println("1. Contador sin sincronizacion");
            System.out.println("2. Contador con sincronizacion");
            System.out.println("3. Productor - Consumidor");
            System.out.println("4. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");

            String entrada = scanner.nextLine().trim();

            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {

                case 1:
                    ContadorCompartido.ejecutarSinSincronizacion();
                    break;

                case 2:
                    ContadorCompartido.ejecutarConSincronizacion();
                    break;

                case 3:
                    ejecutarProductorConsumidor();
                    break;

                case 4:
                    System.out.println("Saliendo del modulo 2...");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 4);
    }

    // Ejecutar simulacion productor-consumidor
    public static void ejecutarProductorConsumidor() {

        System.out.println("\n--- PRODUCTOR - CONSUMIDOR ---");

        BufferCompartido buffer = new BufferCompartido();

        Productor productor = new Productor(
                buffer, 20, "Productor 1");

        Consumidor consumidor = new Consumidor(
                buffer, 20, "Consumidor 1");

        productor.start();
        consumidor.start();

        try {
            productor.join();
            consumidor.join();

        } catch (InterruptedException e) {
            System.out.println("Simulacion interrumpida.");
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println("\nSimulacion finalizada.");
        System.out.println("Se procesaron los 20 elementos.");
    }
}