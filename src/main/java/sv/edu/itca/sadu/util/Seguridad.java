package sv.edu.itca.sadu.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Hash de contrasenas: SHA-256 + Base64, igual que la Guia 2
 * y que los usuarios de prueba de sql/02_datos_prueba.sql.
 */
public final class Seguridad {

    private Seguridad() {
    }

    public static String hash(String clave) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(clave.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No fue posible procesar la contrasena.", e);
        }
    }
}
