package simos.cpu;

// Los comentarios que empiezan con // son solo para las personas: Java los ignora al compilar.
// No cambian el funcionamiento del programa, sirven para explicar cada decisión.

/**
 * Representa un proceso listo para ser planificado.
 * Guarda únicamente sus datos fijos: nombre, instante de llegada y ráfaga (tiempo de CPU).
 *
 * Podemos imaginar esta clase como una "ficha" o una "tarjeta" de cada proceso:
 *   - Nombre: cómo se llama (por ejemplo, P1).
 *   - Llegada: en qué segundo aparece en la cola de listos (por ejemplo, 0).
 *   - Ráfaga: cuántos segundos de CPU necesita para terminar (por ejemplo, 8).
 *
 * Estos tres datos NO se modifican durante la simulación. El tiempo que le "queda"
 * a un proceso dentro de Round Robin se guarda en otra clase (ProcesoEnCola),
 * para mantener esta ficha limpia y sencilla.
 */
public final class Proceso {

    // --- ATRIBUTOS (variables que pertenecen al objeto) ---
    // 'private' significa que solo se pueden usar desde dentro de esta clase.
    // 'final' significa que, una vez asignados en el constructor, ya no cambian.

    // Guarda el texto que identifica al proceso: "P1", "P2", etc.
    private final String nombre;

    // Guarda el instante (un número entero) en el que el proceso llega a la cola.
    // Ejemplo: llegada = 3 significa que el proceso aparece en el segundo 3.
    private final int llegada;

    // Guarda la cantidad total de trabajo de CPU que necesita el proceso.
    // Ejemplo: rafaga = 8 significa que necesita 8 unidades de tiempo de CPU.
    private final int rafaga;

    /**
     * CONSTRUCTOR: crea un proceso nuevo y comprueba que sus datos sean válidos.
     *
     * Recibe:
     *  - nombre: el texto identificador del proceso (por ejemplo, "P1").
     *  - llegada: el instante de llegada; no puede ser negativo.
     *  - rafaga: el trabajo de CPU; debe ser mayor que cero.
     *
     * Qué hace internamente:
     *  1. Revisa que el nombre no esté vacío.
     *  2. Revisa que la llegada no sea negativa (no existen tiempos negativos aquí).
     *  3. Revisa que la ráfaga sea positiva (un proceso con ráfaga 0 no haría nada).
     *  4. Guarda los tres valores en los atributos con 'this.'.
     *
     * Devuelve: nada explícitamente; los constructores no tienen tipo de retorno.
     * Al terminar, ya tenemos un objeto Proceso listo para usar.
     */
    public Proceso(String nombre, int llegada, int rafaga) {
        // Si no se escribió nombre, o solo se escribieron espacios, avisamos con un error.
        // 'isBlank()' detecta cadenas vacías o que solo tienen espacios.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proceso no puede estar vacío.");
        }
        // Un proceso no puede llegar "antes del inicio" de la simulación (tiempo 0).
        if (llegada < 0) {
            throw new IllegalArgumentException("La llegada no puede ser negativa: " + llegada);
        }
        // Una ráfaga de 0 o menos no tiene sentido: siempre hay algo de trabajo por hacer.
        if (rafaga <= 0) {
            throw new IllegalArgumentException("La ráfaga debe ser mayor que cero: " + rafaga);
        }
        // 'this.nombre' es el ATRIBUTO de la clase; 'nombre' es el PARÁMETRO recibido.
        // '.trim()' quita espacios sobrantes al principio y al final del texto.
        this.nombre = nombre.trim();
        this.llegada = llegada;
        this.rafaga = rafaga;
    }

    /**
     * Devuelve el nombre del proceso.
     * Recibe: nada. Internamente solo entrega el atributo 'nombre' guardado.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el instante de llegada del proceso.
     * Recibe: nada. Devuelve el número guardado en 'llegada'.
     */
    public int getLlegada() {
        return llegada;
    }

    /**
     * Devuelve la ráfaga total del proceso (el trabajo que necesita).
     * Recibe: nada. Devuelve el número guardado en 'rafaga'.
     */
    public int getRafaga() {
        return rafaga;
    }

    /**
     * Convierte el proceso en texto para poder mostrarlo en consola.
     * Recibe: nada. Devuelve algo como "P1(0,8)".
     * Se usa, por ejemplo, cuando imprimimos la lista de procesos cargados.
     */
    @Override
    public String toString() {
        return nombre + "(" + llegada + "," + rafaga + ")";
    }
}
