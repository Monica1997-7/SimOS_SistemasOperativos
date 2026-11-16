package simos.concurrencia;

public class ContadorCompartido {

    // Variable compartida por los hilos
    static int contador = 0;

    // Incremento con sincronizacion
    public static synchronized void incrementarSeguro() {
        contador++;
    }

    // Ejecutar contador sin sincronizacion
    public static void ejecutarSinSincronizacion() {

        contador = 0;
        Thread[] hilos = new Thread[10];

        System.out.println("\n--- CONTADOR SIN SINCRONIZACION ---");

        for (int i = 0; i < 10; i++) {
            hilos[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    contador++;
                }
            });

            hilos[i].start();
        }

        esperarHilos(hilos);

        System.out.println("Resultado obtenido: " + contador);
        System.out.println("Resultado esperado: 10000");
    }

    // Ejecutar contador con sincronizacion
    public static void ejecutarConSincronizacion() {

        contador = 0;
        Thread[] hilos = new Thread[10];

        System.out.println("\n--- CONTADOR CON SINCRONIZACION ---");

        for (int i = 0; i < 10; i++) {
            hilos[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    incrementarSeguro();
                }
            });

            hilos[i].start();
        }

        esperarHilos(hilos);

        System.out.println("Resultado obtenido: " + contador);
        System.out.println("Resultado esperado: 10000");
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
