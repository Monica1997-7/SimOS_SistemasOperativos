package simos.concurrencia;

public class ContadorCompartido {

    // Variable compartida por los hilos
    static int contador = 0;

    // Incremento con sincronizacion
    public static synchronized void incrementarSeguro() {
        contador++;
    }

    // Ejecutar contador sin sincronizacion
    public static void ejecutarSinSincronizacion(int cantidadHilos, int incrementosPorHilo) {

        contador = 0;
        Thread[] hilos = new Thread[cantidadHilos];

        System.out.println("\n--- CONTADOR SIN SINCRONIZACION ---");

        for (int i = 0; i < cantidadHilos; i++) {
            hilos[i] = new Thread(() -> {
                for (int j = 0; j < incrementosPorHilo; j++) {
                    contador++;
                }
            });

            hilos[i].start();
        }

        esperarHilos(hilos);

        int esperado = cantidadHilos * incrementosPorHilo;
        int perdidos = esperado - contador;

        System.out.println("\n========================================");
        System.out.println("      RESULTADOS SIN SINCRONIZACION");
        System.out.println("========================================");
        System.out.println("Hilos ejecutados       : " + cantidadHilos);
        System.out.println("Incrementos por hilo   : " + incrementosPorHilo);
        System.out.println("Resultado esperado     : " + esperado);
        System.out.println("Resultado obtenido     : " + contador);
        System.out.println("Incrementos perdidos   : " + perdidos);
        System.out.println("========================================");
    }

    // Ejecutar contador con sincronizacion
    public static void ejecutarConSincronizacion(int cantidadHilos, int incrementosPorHilo) {

        contador = 0;
        Thread[] hilos = new Thread[cantidadHilos];

        System.out.println("\n--- CONTADOR CON SINCRONIZACION ---");

        for (int i = 0; i < cantidadHilos; i++) {
            hilos[i] = new Thread(() -> {
                for (int j = 0; j < incrementosPorHilo; j++) {
                    incrementarSeguro();
                }
            });

            hilos[i].start();
        }

        esperarHilos(hilos);

        int esperado = cantidadHilos * incrementosPorHilo;
        int perdidos = esperado - contador;

        System.out.println("\n========================================");
        System.out.println("      RESULTADOS CON SINCRONIZACION");
        System.out.println("========================================");
        System.out.println("Hilos ejecutados       : " + cantidadHilos);
        System.out.println("Incrementos por hilo   : " + incrementosPorHilo);
        System.out.println("Resultado esperado     : " + esperado);
        System.out.println("Resultado obtenido     : " + contador);
        System.out.println("Incrementos perdidos   : " + perdidos);
        System.out.println("========================================");
    }

    // Esperar a que todos los hilos terminen
    public static void esperarHilos(Thread[] hilos) {

        for (int i = 0; i < hilos.length; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                System.out.println("Un hilo fue interrumpido.");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
