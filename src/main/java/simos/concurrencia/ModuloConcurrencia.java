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

    // Ejecutar simulacion productor-consumidor
    public static void ejecutarProductorConsumidor(Scanner scanner) {

        System.out.println("\n========================================");
        System.out.println("      SIMULACION PRODUCTOR-CONSUMIDOR");
        System.out.println("========================================");

        System.out.print("Cantidad de elementos a procesar: ");

        int cantidad;

        try {
            cantidad = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: debe ingresar un numero entero.");
            return;
        }

        if (cantidad <= 0) {
            System.out.println("Error: la cantidad debe ser mayor que cero.");
            return;
        }

        System.out.println("\nCapacidad del buffer: 5");
        System.out.println("Elementos a procesar: " + cantidad);
        System.out.println("----------------------------------------");

        BufferCompartido buffer = new BufferCompartido();

        Productor productor = new Productor(
                buffer, cantidad, "Productor 1");

        Consumidor consumidor = new Consumidor(
                buffer, cantidad, "Consumidor 1");

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

        System.out.println("\n========================================");
        System.out.println("          SIMULACION FINALIZADA");
        System.out.println("========================================");
        System.out.println("Elementos procesados: " + cantidad);
    }

    public static void ejecutarContador(Scanner scanner, boolean sincronizado) {

        System.out.println("\n========================================");
        System.out.println("       CONFIGURACION DEL CONTADOR");
        System.out.println("========================================");

        System.out.print("Cantidad de hilos: ");
        int cantidadHilos;

        try {
            cantidadHilos = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero entero.");
            return;
        }

        System.out.print("Incrementos por hilo: ");
        int incrementosPorHilo;

        try {
            incrementosPorHilo = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error: ingrese un numero entero.");
            return;
        }

        if (cantidadHilos <= 0 || incrementosPorHilo <= 0) {
            System.out.println("Error: ambos valores deben ser mayores que cero.");
            return;
        }

        if ((long) cantidadHilos * incrementosPorHilo > Integer.MAX_VALUE) {
            System.out.println("Error: la cantidad total de incrementos es demasiado grande.");
            return;
        }

        if (sincronizado) {
            ContadorCompartido.ejecutarConSincronizacion(
                    cantidadHilos, incrementosPorHilo);
        } else {
            ContadorCompartido.ejecutarSinSincronizacion(
                    cantidadHilos, incrementosPorHilo);
        }
    }
}