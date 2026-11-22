package logica;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de contraseñas con PBKDF2-HMAC-SHA256 + salt aleatorio
 * (se guarda en credencial.password_hash / salt / algoritmo_hash).
 *
 * @author crstvbl
 */
public final class Contrasenas {

    public static final String ALGORITMO = "PBKDF2-SHA256-120000";
    private static final int ITERACIONES = 120_000;
    private static final int LONGITUD_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Contrasenas() {
    }

    public static String nuevoSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hash(String contrasena, String saltBase64) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(),
                    Base64.getDecoder().decode(saltBase64), ITERACIONES, LONGITUD_BITS);
            byte[] h = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.getEncoder().encodeToString(h);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 no disponible en la JVM", e);
        }
    }

    public static boolean verificar(String contrasena, String saltBase64, String hashEsperado) {
        if (contrasena == null || saltBase64 == null || hashEsperado == null) {
            return false;
        }
        byte[] a = Base64.getDecoder().decode(hash(contrasena, saltBase64));
        byte[] b = Base64.getDecoder().decode(hashEsperado);
        return MessageDigest.isEqual(a, b);
    }
}
