package simos.concurrencia;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class BufferCompartido {

    // Buffer que almacenara los elementos
    private ArrayList<Integer> buffer = new ArrayList<>();

    // Capacidad maxima del buffer
    private final int capacidad = 5;

    // Semaforos
    private Semaphore espaciosDisponibles = new Semaphore(capacidad);
    private Semaphore elementosDisponibles = new Semaphore(0);
    private Semaphore mutex = new Semaphore(1);

    // Metodo para agregar elementos al buffer
    public void producir(int elemento, String nombreProductor)
            throws InterruptedException {

        // Espera hasta que exista un espacio libre
        if (espaciosDisponibles.availablePermits() == 0) {
            System.out.println("\n[ESPERA] " + nombreProductor
                    + ": buffer lleno, esperando espacio disponible...");
        }
        espaciosDisponibles.acquire();

        // Solicita acceso exclusivo al buffer
        mutex.acquire();

        // Agrega el elemento
        buffer.add(elemento);

        System.out.println("\n[" + nombreProductor.toUpperCase() + "]");
        System.out.println("  Elemento producido : " + elemento);
        System.out.println("  Elementos en buffer: " + buffer.size());
        System.out.println("  Contenido del buffer: " + buffer);
        System.out.println("----------------------------------------");

        // Libera el acceso al buffer
        mutex.release();

        // Avisa que existe un elemento disponible
        elementosDisponibles.release();
    }

    // Metodo para retirar elementos del buffer
    public int consumir(String nombreConsumidor)
            throws InterruptedException {

        // Espera hasta que exista un elemento
        if (elementosDisponibles.availablePermits() == 0) {
            System.out.println("\n[ESPERA] " + nombreConsumidor
                    + ": buffer vacio, esperando elementos...");
        }
        elementosDisponibles.acquire();

        // Solicita acceso exclusivo al buffer
        mutex.acquire();

        // Retira el primer elemento
        int elemento = buffer.remove(0);

        System.out.println("\n[" + nombreConsumidor.toUpperCase() + "]");
        System.out.println("  Elemento consumido : " + elemento);
        System.out.println("  Elementos en buffer: " + buffer.size());
        System.out.println("  Contenido del buffer: " + buffer);
        System.out.println("----------------------------------------");

        // Libera el acceso al buffer
        mutex.release();

        // Avisa que existe un espacio libre
        espaciosDisponibles.release();

        return elemento;
    }
}
