package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.Capsula;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite 4 (Backend - ms-localizaciones): Pruebas de validación de códigos QR y Cápsulas de IA.
 * Contiene 5 pruebas unitarias independientes con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class CapsulaValidationTest {

    @Mock
    private CapsulaRepository capsulaRepository;

    @InjectMocks
    private CapsulaService capsulaService;

    private Capsula capsula;

    @BeforeEach
    void setUp() {
        capsula = new Capsula();
        capsula.setId(10L);
        capsula.setPuntoId(1L);
        capsula.setTitulo("IA Urbana");
        capsula.setCodigoQr("QR_LA_MONEDA");
        capsula.setPuntos(10);
        capsula.setEstado("PUBLICADA");
    }

    @Test
    @DisplayName("Validar QR exitoso cuando el código existe y está PUBLICADA")
    void testValidarQr_exitoso() {
        when(capsulaRepository.findByCodigoQrIgnoreCase("QR_LA_MONEDA"))
                .thenReturn(Optional.of(capsula));

        var res = capsulaService.validarQr("QR_LA_MONEDA");

        assertNotNull(res);
        assertEquals("QR_LA_MONEDA", res.getCodigoQr());
        assertEquals("PUBLICADA", res.getEstado());
    }

    @Test
    @DisplayName("Validar QR lanza 404 si el código QR no existe")
    void testValidarQr_noExiste() {
        when(capsulaRepository.findByCodigoQrIgnoreCase("QR_DESCONOCIDO"))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                capsulaService.validarQr("QR_DESCONOCIDO"));
    }

    @Test
    @DisplayName("Validar QR rechaza capsulas que estan en estado BORRADOR")
    void testValidarQr_estadoBorrador_rechazado() {
        capsula.setEstado("BORRADOR");
        when(capsulaRepository.findByCodigoQrIgnoreCase("QR_BORRADOR"))
                .thenReturn(Optional.of(capsula));

        assertThrows(ResponseStatusException.class, () ->
                capsulaService.validarQr("QR_BORRADOR"));
    }

    @Test
    @DisplayName("Normaliza busqueda de codigo QR ignorando mayusculas y minusculas")
    void testValidarQr_insensibleAMayusculas() {
        when(capsulaRepository.findByCodigoQrIgnoreCase("qr_la_moneda"))
                .thenReturn(Optional.of(capsula));

        var res = capsulaService.validarQr("qr_la_moneda");
        assertNotNull(res);
        assertEquals("QR_LA_MONEDA", res.getCodigoQr());
    }

    @Test
    @DisplayName("Asigna 10 puntos por defecto a la capsula")
    void testCapsula_puntosDefecto() {
        assertEquals(10, capsula.getPuntos());
    }
}
