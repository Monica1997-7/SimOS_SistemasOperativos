package simos.cpu;

// --- IMPORTACIONES ---
// ArrayDeque y Deque: nos dan una COLA de procesos (entran por el final, salen por el frente).
// ArrayList: una lista donde podemos agregar elementos mientras simulamos.
// Comparator: nos permite ordenar los procesos por su instante de llegada.
// HashMap y Map: guardan parejas "nombre del proceso -> instante en que terminó".
// HashSet y Set: ayudan a detectar nombres de procesos repetidos.
// List: es el tipo de dato que recibimos y devolvemos.
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementa los algoritmos de planificación FCFS y Round Robin.
 * Retorno = fin - llegada; Espera = retorno - ráfaga.
 *
 * Esta clase es el "motor" del módulo: recibe una lista de procesos y devuelve
 * un ResultadoCPU con el diagrama de Gantt, la tabla y el promedio de espera.
 * No muestra nada en pantalla: solo calcula. Quien imprime es ModuloCPU.
 */
public final class PlanificadorCPU {

    /**
     * FCFS (First Come, First Served = "el primero que llega es el primero que se atiende").
     *
     * Recibe: una lista de procesos.
     * Internamente:
     *  1. Valida la lista.
     *  2. La ordena por llegada (si dos llegan igual, respeta el orden de entrada).
     *  3. Recorre la lista en ese orden y va avanzando un reloj llamado 'tiempo'.
     *  4. Guarda cada trozo de uso de CPU como un segmento del Gantt.
     * Devuelve: un ResultadoCPU con los cálculos de este algoritmo.
     *
     * Si hay un hueco de CPU se registra como un segmento IDLE.
     */
    public ResultadoCPU fcfs(List<Proceso> procesos) {
        // Paso 1: revisamos que haya procesos y que los nombres no se repitan.
        validar(procesos);

        // Paso 2: copiamos la lista para NO modificar la original que vino del menú.
        // List.sort es estable: los procesos con la misma llegada conservan el orden de entrada.
        List<Proceso> ordenados = new ArrayList<>(procesos);
        ordenados.sort(Comparator.comparingInt(Proceso::getLlegada));

        // Paso 3: preparamos los datos que vamos llenando.
        // 'segmentos' guarda los trozos del diagrama de Gantt.
        List<ResultadoCPU.Segmento> segmentos = new ArrayList<>();
        // 'finPorNombre' guarda, para cada proceso, el segundo en que terminó.
        Map<String, Integer> finPorNombre = new HashMap<>();
        // 'tiempo' es el reloj de la simulación: empieza en 0.
        int tiempo = 0;

        // Paso 4: recorremos la cola de listos en orden de llegada.
        for (Proceso proceso : ordenados) {
            // Si este proceso llega más tarde que el reloj actual, la CPU estuvo libre.
            if (proceso.getLlegada() > tiempo) {
                // Marcamos ese hueco como IDLE y movemos el reloj hasta la llegada.
                segmentos.add(new ResultadoCPU.Segmento("IDLE", tiempo, proceso.getLlegada()));
                tiempo = proceso.getLlegada();
            }
            // Guardamos el segundo exacto en que empieza a usar la CPU.
            int inicio = tiempo;
            // Avanzamos el reloj sumando toda la ráfaga: FCFS no interrumpe a nadie.
            tiempo += proceso.getRafaga();
            // Apuntamos el segmento "P1", "P2", ... con su inicio y su fin.
            segmentos.add(new ResultadoCPU.Segmento(proceso.getNombre(), inicio, tiempo));
            // Anotamos el instante de fin para usarlo después en retorno y espera.
            finPorNombre.put(proceso.getNombre(), tiempo);
        }

        // Paso 5: calculamos la tabla y el promedio y empaquetamos todo el resultado.
        return construirResultado("FCFS (First Come, First Served)", procesos, segmentos, finPorNombre);
    }

    /**
     * Round Robin con quantum configurable.
     *
     * Recibe: la lista de procesos y el quantum (el máximo de tiempo que cada proceso
     * puede usar la CPU en una sola turno).
     * Internamente:
     *  - Mantiene una COLA de procesos listos.
     *  - Saca el primero, le da como máximo 'quantum' unidades de tiempo.
     *  - Si no termina, lo vuelve a poner AL FINAL de la cola.
     *  - Antes de volver a ponerlo, mete en la cola a todos los procesos que llegaron
     *    mientras tanto, para que pasen ANTES que el desalojado.
     * Devuelve: un ResultadoCPU con los cálculos de este algoritmo.
     *
     * Al terminar un quantum, primero entran los procesos que llegaron durante ese quantum
     * y después el proceso desalojado, que va al final de la cola.
     */
    public ResultadoCPU roundRobin(List<Proceso> procesos, int quantum) {
        // Validamos los procesos igual que en FCFS.
        validar(procesos);
        // Un quantum de 0 o menos haría un bucle infinito, así que lo rechazamos.
        if (quantum <= 0) {
            throw new IllegalArgumentException("El quantum debe ser mayor que cero: " + quantum);
        }

        // Copiamos y ordenamos por llegada, sin tocar la lista original.
        List<Proceso> ordenados = new ArrayList<>(procesos);
        ordenados.sort(Comparator.comparingInt(Proceso::getLlegada));

        // Esta es la COLA DE LISTOS. Cada elemento lleva su propio tiempo restante.
        Deque<ProcesoEnCola> cola = new ArrayDeque<>();
        // Aquí guardamos los trozos del diagrama de Gantt.
        List<ResultadoCPU.Segmento> segmentos = new ArrayList<>();
        // Aquí anotamos el instante de fin de cada proceso.
        Map<String, Integer> finPorNombre = new HashMap<>();

        // 'siguiente' es un índice: apunta al primer proceso de 'ordenados' que todavía
        // no ha entrado a la cola (porque aún no ha llegado).
        int siguiente = 0;
        // 'tiempo' es el reloj de la simulación.
        int tiempo = 0;

        // Mientras queden procesos por llegar O haya procesos en la cola, seguimos.
        while (siguiente < ordenados.size() || !cola.isEmpty()) {
            // Al inicio de cada turno metemos a la cola todo lo que ya ha llegado.
            siguiente = incorporarLlegadas(ordenados, siguiente, tiempo, cola);

            // Si la cola está vacía, significa que aún no ha llegado nadie.
            if (cola.isEmpty()) {
                // Todavía no ha llegado nadie: avanzamos el reloj hasta la próxima llegada.
                tiempo = ordenados.get(siguiente).getLlegada();
                // Volvemos al principio del while para incorporar esa llegada.
                continue;
            }

            // Aquí se recorre la cola de listos: sacamos al proceso del FRENTE.
            ProcesoEnCola actual = cola.pollFirst();
            // Le damos como máximo el quantum, o menos si ya le falta menos tiempo.
            int duracion = Math.min(quantum, actual.restante);
            // Anotamos cuándo empieza y cuándo termina este turno.
            int inicio = tiempo;
            int fin = tiempo + duracion;

            // Guardamos este turno como un trozo del diagrama de Gantt.
            segmentos.add(new ResultadoCPU.Segmento(actual.proceso.getNombre(), inicio, fin));
            // El reloj avanza hasta el final de este turno.
            tiempo = fin;
            // Le descontamos al proceso el tiempo que acaba de usar.
            actual.restante -= duracion;

            // Los recién llegados entran ANTES del proceso desalojado.
            // Por eso llamamos a incorporarLlegadas AQUÍ, antes de volver a poner 'actual'.
            siguiente = incorporarLlegadas(ordenados, siguiente, tiempo, cola);

            // Si todavía le queda trabajo, NO terminó: va al FINAL de la cola.
            if (actual.restante > 0) {
                cola.addLast(actual);
            } else {
                // Si ya no le queda nada, guardamos su instante de fin.
                finPorNombre.put(actual.proceso.getNombre(), fin);
            }
        }

        // Armamos el ResultadoCPU con el nombre del algoritmo y el quantum usado.
        return construirResultado("ROUND ROBIN (quantum = " + quantum + ")",
                procesos, segmentos, finPorNombre);
    }

    /**
     * Proceso en la cola de listos con el tiempo de CPU que le queda.
     *
     * Es una clase auxiliar privada: solo existe para la simulación de Round Robin.
     * El objeto Proceso original guarda la ráfaga total (por ejemplo, 8);
     * este guarda cuánto le falta en este momento (por ejemplo, 5 después de un quantum).
     */
    private static final class ProcesoEnCola {

        // Referencia al proceso original: de ahí sacamos el nombre y la ráfaga total.
        private final Proceso proceso;

        // Tiempo de CPU que aún le falta para terminar. SÍ cambia durante la simulación.
        private int restante;

        /**
         * Crea un elemento de cola a partir de un proceso.
         * Recibe: el proceso original.
         * Internamente: guarda la referencia y copia la ráfaga total en 'restante'.
         * Devuelve: el elemento listo para entrar a la cola.
         */
        private ProcesoEnCola(Proceso proceso) {
            this.proceso = proceso;
            this.restante = proceso.getRafaga();
        }
    }

    /**
     * Pasa a la cola todos los procesos cuya llegada sea menor o igual al instante actual.
     *
     * Recibe:
     *  - ordenados: la lista de procesos ordenada por llegada.
     *  - siguiente: índice del primer proceso que aún no ha entrado a la cola.
     *  - tiempo: el instante actual del reloj.
     *  - cola: la cola de listos donde vamos a meter esos procesos.
     * Internamente: avanza el índice mientras la llegada sea <= tiempo.
     * Devuelve: el nuevo valor de 'siguiente' (cuántos procesos ya entraron).
     */
    private static int incorporarLlegadas(List<Proceso> ordenados, int siguiente,
                                          int tiempo, Deque<ProcesoEnCola> cola) {
        // Mientras el siguiente proceso ya haya llegado (o llegue justo en este instante)...
        while (siguiente < ordenados.size() && ordenados.get(siguiente).getLlegada() <= tiempo) {
            // Lo empujamos al final de la cola, con su tiempo restante inicial.
            cola.addLast(new ProcesoEnCola(ordenados.get(siguiente)));
            // Y pasamos al siguiente proceso de la lista ordenada.
            siguiente++;
        }
        // Devolvemos el índice actualizado para no volver a meter dos veces el mismo proceso.
        return siguiente;
    }

    /**
     * Calcula retorno, espera y promedio respetando el orden original de los procesos.
     *
     * Recibe:
     *  - algoritmo: texto con el nombre del algoritmo (para el título).
     *  - procesos: la lista original, para imprimir las filas en el mismo orden de entrada.
     *  - segmentos: los trozos del Gantt.
     *  - finPorNombre: mapa con el instante de fin de cada proceso.
     * Internamente: por cada proceso aplica las dos fórmulas de negocio y suma las esperas.
     * Devuelve: un ResultadoCPU completo.
     */
    private static ResultadoCPU construirResultado(String algoritmo, List<Proceso> procesos,
                                                   List<ResultadoCPU.Segmento> segmentos,
                                                   Map<String, Integer> finPorNombre) {
        // Preparamos la lista de filas de la tabla.
        List<ResultadoCPU.Fila> filas = new ArrayList<>();
        // Acumulador para sumar todas las esperas; empieza en 0.
        double sumaEsperas = 0;

        // Recorremos los procesos en el orden original (P1, P2, P3...).
        for (Proceso proceso : procesos) {
            // Buscamos en el mapa el segundo en que terminó este proceso.
            int fin = finPorNombre.get(proceso.getNombre());
            // RETORNO = cuánto tardó en total desde que llegó: fin menos llegada.
            int retorno = fin - proceso.getLlegada();
            // ESPERA = retorno menos la ráfaga (el tiempo que sí estuvo usando la CPU).
            int espera = retorno - proceso.getRafaga();
            // Guardamos los seis datos en una fila de la tabla.
            filas.add(new ResultadoCPU.Fila(proceso.getNombre(), proceso.getLlegada(),
                    proceso.getRafaga(), fin, retorno, espera));
            // Sumamos esta espera al acumulador para luego sacar el promedio.
            sumaEsperas += espera;
        }

        // PROMEDIO = suma de esperas entre la cantidad de procesos.
        return new ResultadoCPU(algoritmo, segmentos, filas, sumaEsperas / filas.size());
    }

    /**
     * Revisa que la lista de procesos sea usable antes de empezar a simular.
     * Recibe: la lista de procesos (puede ser null o estar vacía).
     * Internamente: comprueba tres cosas: que no sea null/vacía, que no haya nulos
     * dentro y que no se repita ningún nombre (si se repitiera, el mapa confundiría
     * los instantes de fin de dos procesos distintos).
     * Devuelve: nada; solo lanza un error si encuentra un problema.
     */
    private static void validar(List<Proceso> procesos) {
        // Si no hay lista, o está vacía, no hay nada que planificar.
        if (procesos == null || procesos.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar al menos un proceso.");
        }
        // Este conjunto guarda los nombres que ya vimos, para detectar repetidos.
        Set<String> nombres = new HashSet<>();
        for (Proceso proceso : procesos) {
            // Un proceso nulo provocaría un error al preguntarle por su nombre.
            if (proceso == null) {
                throw new IllegalArgumentException("La lista contiene un proceso nulo.");
            }
            // add() devuelve false si el nombre ya estaba en el conjunto.
            if (!nombres.add(proceso.getNombre())) {
                throw new IllegalArgumentException("Nombre de proceso repetido: " + proceso.getNombre());
            }
        }
    }
}
