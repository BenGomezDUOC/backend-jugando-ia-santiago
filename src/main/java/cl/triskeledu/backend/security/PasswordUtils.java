package cl.triskeledu.backend.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Utilidad de cifrado compatible con el algoritmo PBKDF2 usado en la app Android.
 * Formato almacenado: salBase64:hashBase64
 */
public class PasswordUtils {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_CLAVE = 256;
    private static final int LONGITUD_SAL = 16;

    public static String crearHash(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            return "";
        }
        try {
            SecureRandom random = new SecureRandom();
            byte[] sal = new byte[LONGITUD_SAL];
            random.nextBytes(sal);

            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LONGITUD_CLAVE);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            String salBase64 = Base64.getEncoder().encodeToString(sal);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return salBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    public static boolean verificarContrasena(String contrasena, String hashGuardado) {
        if (contrasena == null || hashGuardado == null || !hashGuardado.contains(":")) {
            return false;
        }
        try {
            String[] partes = hashGuardado.split(":");
            if (partes.length != 2) return false;

            byte[] sal = Base64.getDecoder().decode(partes[0]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);

            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LONGITUD_CLAVE);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
            byte[] hashCalculado = factory.generateSecret(spec).getEncoded();

            return MessageDigestEquals(hashCalculado, hashEsperado);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean MessageDigestEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
