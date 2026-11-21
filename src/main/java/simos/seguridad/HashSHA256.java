package simos.seguridad; // Indica que esta clase pertenece al paquete de seguridad.

// --- IMPORTACIONES ---
import java.nio.charset.StandardCharsets; // Herramienta para decirle a Java que use codificación UTF-8 (para que tildes y símbolos no se dañen).
import java.security.MessageDigest;       // Herramienta de Java que contiene los algoritmos de encriptación (como SHA-256).
import java.security.NoSuchAlgorithmException; // Error que se lanza si el algoritmo de encriptación no existe.
import java.util.HexFormat;               // Herramienta para convertir los bytes de la encriptación en un texto legible (números y letras).

/**
 * Clase utilitaria para generar hashes SHA-256.
 * ¿Qué es un HASH? Es como una licuadora de un solo sentido: 
 * si metes "admin123", sale un texto larguísimo (ej. "240be518fa..."). 
 * Pero es imposible hacer el proceso inverso (no puedes meter el texto largo y sacar "admin123").
 */
public final class HashSHA256 {

    /**
     * CONSTRUCTOR PRIVADO.
     * ¿Por qué está vacío y es privado? Porque esta clase es de "utilidad" (solo tiene métodos estáticos).
     * No queremos que nadie haga un 'new HashSHA256()' en el programa, porque no tiene sentido. 
     * Solo queremos usar su método 'calcular'.
     */
    private HashSHA256() {
    }

    /**
     * MÉTODO PRINCIPAL: calcular.
     * Recibe un texto (la contraseña) y devuelve su hash SHA-256.
     * 
     * @param texto La contraseña en texto plano (ej. "admin123").
     * @return El hash en formato texto (ej. "240be518fabd2724...").
     */
    public static String calcular(String texto) {
        // 1. VALIDACIÓN: Si el texto es nulo, lanza un error.
        if (texto == null) {
            throw new IllegalArgumentException("El texto a hashear no puede ser nulo");
        }
        
        // 2. INTENTO DE ENCRIPTACIÓN:
        try {
            // 'MessageDigest' es la herramienta de Java para hashear. 
            // Le decimos que use el algoritmo "SHA-256".
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Aquí ocurre la magia: 
            // - 'texto.getBytes(StandardCharsets.UTF_8)' convierte el texto a bytes.
            // - '.digest(...)' aplica el algoritmo matemático SHA-256 y devuelve un arreglo de bytes.
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            
            // 3. FORMATO: 
            // Los bytes son difíciles de leer. 'HexFormat' los convierte en un texto hexadecimal 
            // (números del 0-9 y letras de la A-F) para que lo podamos guardar en un archivo de texto.
            return HexFormat.of().formatHex(hash);
            
        } catch (NoSuchAlgorithmException e) {
            // 4. MANEJO DE ERRORES:
            // Este error solo pasaría si la versión de Java instalada es súper vieja y no tiene SHA-256.
            // Como es un estándar obligatorio, lanzamos un error interno de Java.
            throw new IllegalStateException("SHA-256 no disponible en la JVM", e);
        }
    }
}