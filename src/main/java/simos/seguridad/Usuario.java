package simos.seguridad; // Indica que esta clase vive dentro de la carpeta/paquete de seguridad.

/**
 * Clase que representa a un Usuario del simulador.
 * Es importante notar que la contraseña NUNCA se guarda en texto plano, 
 * solo se guarda su "hash" (un código encriptado SHA-256).
 */
public final class Usuario {

    // --- ATRIBUTOS ---
    // 'final' significa que una vez se le asigna un valor en el constructor, ya no se puede cambiar.
    private final String nombre;    // Ejemplo: "admin" o "usuario"
    private final String hashClave; // El texto encriptado de la contraseña (ej. "240be518fa...").
    private final Rol rol;          // El rol del usuario (ADMINISTRADOR o USUARIO).

    /**
     * CONSTRUCTOR.
     * Es el método que se ejecuta cuando creamos un "new Usuario(...)". 
     * Su trabajo es validar que los datos que nos pasan sean correctos antes de crear el usuario.
     */
    public Usuario(String nombre, String hashClave, Rol rol) {
        
        // VALIDACIONES DE SEGURIDAD:
        // Si el nombre es nulo o está vacío (solo espacios), lanza un error y detiene la creación.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        // Si el hash está vacío, también lanza un error. Un usuario no puede existir sin clave.
        if (hashClave == null || hashClave.isBlank()) {
            throw new IllegalArgumentException("El hash de la clave es obligatorio");
        }
        // Si no tiene rol asignado, lanza error.
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        // ASIGNACIÓN DE DATOS:
        // Si pasó las validaciones, guardamos los datos en los atributos de la clase.
        // Usamos .trim() en el nombre y el hash para borrar espacios en blanco accidentales.
        this.nombre = nombre.trim();
        this.hashClave = hashClave.trim();
        this.rol = rol;
    }

    /**
     * GETTER del Nombre. 
     * Sirve para consultar el nombre del usuario desde otras clases sin poder modificarlo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * GETTER del Rol.
     * Sirve para consultar qué rol tiene (y por ende, qué permisos tiene).
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * MÉTODO VITAL PARA EL LOGIN: verificarClave.
     * Compara la clave que el usuario escribe en el teclado con la que está guardada en el sistema.
     * 
     * @param clave La contraseña en texto plano que el usuario acaba de escribir (ej. "admin123").
     * @return true si la contraseña es correcta, false si es incorrecta.
     */
    public boolean verificarClave(String clave) {
        // 1. Si la clave que escribió es nula, de una vez retorna 'false' (no puede ser válida).
        // 2. Si no es nula, llama a HashSHA256.calcular(clave) para ENCRIPTAR lo que el usuario escribió.
        // 3. Compara (con .equalsIgnoreCase) ese nuevo hash generado con el 'hashClave' guardado en la base de datos.
        // 4. Si son iguales, retorna true (la clave es correcta).
        return clave != null && hashClave.equalsIgnoreCase(HashSHA256.calcular(clave));
    }

    /**
     * MÉTODO toString().
     * El '@Override' indica que estamos sobrescribiendo un método que ya existe en Java.
     * Sirve para que, si intentamos imprimir el usuario (System.out.println(usuario)), 
     * en lugar de mostrar la dirección de memoria (como "simos.seguridad.Usuario@5f150435"), 
     * muestre un texto bonito: "admin (Administrador)".
     */
    @Override
    public String toString() {
        return nombre + " (" + rol.getDescripcion() + ")";
    }
}