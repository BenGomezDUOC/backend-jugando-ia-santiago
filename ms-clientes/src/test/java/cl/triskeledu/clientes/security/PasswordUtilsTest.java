package cl.triskeledu.clientes.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite 1 (Backend - ms-clientes): Pruebas de Criptografía y Seguridad con PBKDF2.
 * Contiene 5 pruebas unitarias independientes.
 */
class PasswordUtilsTest {

    @Test
    @DisplayName("crearHash genera un hash no nulo y con formato sal:hash")
    void testCrearHash_formatoValido() {
        String hash = PasswordUtils.crearHash("passwordSegura123");
        assertNotNull(hash);
        assertTrue(hash.contains(":"));
        assertEquals(2, hash.split(":").length);
    }

    @Test
    @DisplayName("verificarContrasena valida exitosamente con la contraseña correcta")
    void testVerificarContrasena_correcta() {
        String clave = "Triskeledu2026!";
        String hash = PasswordUtils.crearHash(clave);
        assertTrue(PasswordUtils.verificarContrasena(clave, hash));
    }

    @Test
    @DisplayName("verificarContrasena rechaza una contraseña incorrecta")
    void testVerificarContrasena_incorrecta() {
        String hash = PasswordUtils.crearHash("claveCorrecta");
        assertFalse(PasswordUtils.verificarContrasena("claveErronea", hash));
    }

    @Test
    @DisplayName("crearHash genera sales diferentes para la misma clave")
    void testCrearHash_salesDiferentes() {
        String hash1 = PasswordUtils.crearHash("mismaClave");
        String hash2 = PasswordUtils.crearHash("mismaClave");
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("verificarContrasena maneja valores nulos o vacios de forma segura")
    void testVerificarContrasena_valoresNulosOVacios() {
        assertFalse(PasswordUtils.verificarContrasena(null, "hash"));
        assertFalse(PasswordUtils.verificarContrasena("clave", null));
        assertFalse(PasswordUtils.verificarContrasena("", ""));
        assertFalse(PasswordUtils.verificarContrasena("clave", "hashSinDosPuntos"));
    }
}
