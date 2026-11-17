package simos.cpu;

// Traemos List porque vamos a guardar colecciones ordenadas de segmentos y de filas.
import java.util.List;

/**
 * Resultado de una simulación de planificación.
 * Contiene el diagrama de Gantt, la tabla de resultados por proceso y el promedio de espera.
 *
 * Esta clase es como una "caja" donde el planificador deja todo lo que encontró:
 *  1. Una lista de segmentos -> sirve para dibujar el diagrama de Gantt.
 *  2. Una lista de filas     -> sirve para imprimir la tabla de resultados.
 *  3. Un promedio de espera  -> sirve para comparar FCFS contra Round Robin.
 *
 * No calcula nada por su cuenta: solo guarda lo que el PlanificadorCPU le entrega.
 */
public final class ResultadoCPU {

    /**
     * Un tramo del diagrama de Gantt: el proceso que ocupó la CPU entre inicio y fin.
     *
     * Ejemplo: un segmento con nombre "P1", inicio 0 y fin 3 se dibuja como "| P1 |"
     * y significa que P1 usó la CPU desde el segundo 0 hasta el segundo 3.
     *
     * 'static' permite usar esta clase pequeña dentro de ResultadoCPU sin crear
     * primero un ResultadoCPU. 'final' evita que se pueda extender.
     */
    public static final class Segmento {

        // Guarda el nombre que se va a escribir dentro del trozo del diagrama.
        // Puede ser "P1", "P2"... o "IDLE" cuando la CPU está libre.
        private final String nombre;

        // Guarda el segundo en el que EMPIEZA este trozo de uso de CPU.
        private final int inicio;

        // Guarda el segundo en el que TERMINA este trozo de uso de CPU.
        private final int fin;

        /**
         * Crea un segmento.
         * Recibe: nombre del proceso, instante de inicio e instante de fin.
         * Internamente guarda esos tres datos. No hace cálculos.
         * Devuelve: el nuevo segmento ya listo para agregarse a la lista.
         */
        public Segmento(String nombre, int inicio, int fin) {
            this.nombre = nombre;
            this.inicio = inicio;
            this.fin = fin;
        }

        /** Devuelve el nombre del proceso de este segmento. */
        public String getNombre() {
            return nombre;
        }

        /** Devuelve el instante en que empezó el segmento. */
        public int getInicio() {
            return inicio;
        }

        /** Devuelve el instante en que terminó el segmento. */
        public int getFin() {
            return fin;
        }
    }

    /**
     * Fila de la tabla de resultados de un proceso.
     *
     * Cada fila responde a las preguntas típicas de un ejercicio de planificación:
     * ¿cuándo terminó?, ¿cuánto tardó desde que llegó? y ¿cuánto esperó?
     * Recuerda las fórmulas que pide el profesor:
     *   Retorno = Fin - Llegada
     *   Espera  = Retorno - Ráfaga
     */
    public static final class Fila {

        // Nombre del proceso de la fila (por ejemplo, "P1").
        private final String nombre;

        // Instante de llegada del proceso; se copia para no depender del objeto original.
        private final int llegada;

        // Ráfaga total del proceso; se copia para tenerla a mano en la tabla.
        private final int rafaga;

        // Instante en el que el proceso terminó de ejecutarse.
        private final int fin;

        // Tiempo de retorno: cuánto tardó en total desde su llegada hasta su fin.
        private final int retorno;

        // Tiempo de espera: cuánto estuvo esperando sin usar la CPU.
        private final int espera;

        /**
         * Crea una fila de resultados.
         * Recibe: nombre, llegada, ráfaga, fin, retorno y espera ya calculados.
         * Internamente solo guarda los seis datos (no recalcula nada).
         * Devuelve: la fila lista para imprimirse con System.out.printf.
         */
        public Fila(String nombre, int llegada, int rafaga, int fin, int retorno, int espera) {
            this.nombre = nombre;
            this.llegada = llegada;
            this.rafaga = rafaga;
            this.fin = fin;
            this.retorno = retorno;
            this.espera = espera;
        }

        /** Devuelve el nombre del proceso de esta fila. */
        public String getNombre() {
            return nombre;
        }

        /** Devuelve la llegada guardada en esta fila. */
        public int getLlegada() {
            return llegada;
        }

        /** Devuelve la ráfaga guardada en esta fila. */
        public int getRafaga() {
            return rafaga;
        }

        /** Devuelve el instante de fin guardado en esta fila. */
        public int getFin() {
            return fin;
        }

        /** Devuelve el retorno ya calculado por el planificador. */
        public int getRetorno() {
            return retorno;
        }

        /** Devuelve la espera ya calculada por el planificador. */
        public int getEspera() {
            return espera;
        }
    }

    // --- ATRIBUTOS DEL RESULTADO COMPLETO ---

    // Texto que identifica al algoritmo: "FCFS (First Come, First Served)" o
    // "ROUND ROBIN (quantum = 3)". Se usa como título al imprimir.
    private final String algoritmo;

    // Todos los trozos del diagrama de Gantt, en el orden en que usaron la CPU.
    private final List<Segmento> segmentos;

    // Todas las filas de la tabla, en el orden original de los procesos.
    private final List<Fila> filas;

    // Suma de las esperas dividida entre la cantidad de procesos.
    // Es un 'double' porque puede dar decimal (por ejemplo, 14.20).
    private final double promedioEspera;

    /**
     * Crea el resultado completo de una simulación.
     *
     * Recibe:
     *  - algoritmo: el nombre del algoritmo usado.
     *  - segmentos: la lista de trozos del Gantt.
     *  - filas: la lista de filas de la tabla.
     *  - promedioEspera: el promedio ya calculado.
     *
     * Qué hace internamente: guarda los datos. 'List.copyOf' crea una copia de solo lectura,
     * así nadie puede modificar la lista original desde fuera después de crear el resultado.
     *
     * Devuelve: el objeto ResultadoCPU listo para imprimirse.
     */
    public ResultadoCPU(String algoritmo, List<Segmento> segmentos, List<Fila> filas, double promedioEspera) {
        this.algoritmo = algoritmo;
        this.segmentos = List.copyOf(segmentos);
        this.filas = List.copyOf(filas);
        this.promedioEspera = promedioEspera;
    }

    /** Devuelve el nombre del algoritmo, usado como título en la consola. */
    public String getAlgoritmo() {
        return algoritmo;
    }

    /** Devuelve la lista de segmentos para dibujar el diagrama de Gantt. */
    public List<Segmento> getSegmentos() {
        return segmentos;
    }

    /** Devuelve la lista de filas para imprimir la tabla de resultados. */
    public List<Fila> getFilas() {
        return filas;
    }

    /** Devuelve el promedio de espera para comparar entre algoritmos. */
    public double getPromedioEspera() {
        return promedioEspera;
    }
}
