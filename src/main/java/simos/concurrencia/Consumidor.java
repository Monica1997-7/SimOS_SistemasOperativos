package simos.concurrencia;

public class Consumidor extends Thread {

    private BufferCompartido buffer;
    private int cantidadElementos;
    private String nombre;

    // Constructor
    public Consumidor(BufferCompartido buffer, int cantidadElementos, String nombre) {
        this.buffer = buffer;
        this.cantidadElementos = cantidadElementos;
        this.nombre = nombre;
    }

    @Override
    public void run() {

        // Consumir los elementos
        for (int i = 1; i <= cantidadElementos; i++) {

            try {
                buffer.consumir(nombre);

            } catch (InterruptedException e) {
                System.out.println(nombre + " fue interrumpido.");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}