package simos.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utilidad de hashing para no almacenar claves en texto plano.
 */
public final class HashSHA256 {

    private HashSHA256() {
    }

    public static String calcular(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El texto a hashear no puede ser nulo");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 es obligatorio en toda implementación de la plataforma Java.
            throw new IllegalStateException("SHA-256 no disponible en la JVM", e);
        }
    }
}
