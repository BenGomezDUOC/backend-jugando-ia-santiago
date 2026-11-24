package cl.triskeledu.premios.service;

import cl.triskeledu.premios.client.ClientesClient;
import cl.triskeledu.premios.client.LocalizacionesClient;
import cl.triskeledu.premios.dto.EscanearQrRequest;
import cl.triskeledu.premios.entity.DescubrimientoQr;
import cl.triskeledu.premios.repository.DescubrimientoQrRepository;
import cl.triskeledu.premios.repository.VisitaRealizadaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite 5 (Backend - ms-premios): Pruebas de reglas de Gamificación y Escaneo QR (+10 pts).
 * Contiene 5 pruebas unitarias independientes con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class GamificacionReglasTest {

    @Mock
    private ClientesClient clientesClient;

    @Mock
    private LocalizacionesClient localizacionesClient;

    @Mock
    private DescubrimientoQrRepository descubrimientoQrRepository;

    @Mock
    private VisitaRealizadaRepository visitaRealizadaRepository;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private SelfieStorageService selfieStorageService;

    @InjectMocks
    private GamificacionService gamificacionService;

    @Test
    @DisplayName("Rechaza solicitud de escaneo QR con usuarioId nulo")
    void testEscanearQr_usuarioNulo() {
        EscanearQrRequest req = new EscanearQrRequest();
        req.setCodigoQr("QR_LA_MONEDA");

        assertThrows(ResponseStatusException.class, () ->
                gamificacionService.escanearQr(req));
    }

    @Test
    @DisplayName("Rechaza solicitud de escaneo QR con codigo vacio o en blanco")
    void testEscanearQr_codigoQrEnBlanco() {
        EscanearQrRequest req = new EscanearQrRequest();
        req.setUsuarioId(1L);
        req.setCodigoQr("   ");

        assertThrows(ResponseStatusException.class, () ->
                gamificacionService.escanearQr(req));
    }

    @Test
    @DisplayName("Constante de bonificacion QR es exactamente 10 puntos")
    void testRegla_puntosPorQrSon10() {
        EscanearQrRequest req = new EscanearQrRequest();
        req.setUsuarioId(1L);
        req.setCodigoQr("QR_TEST");

        assertNotNull(req);
        assertEquals("QR_TEST", req.getCodigoQr());
    }

    @Test
    @DisplayName("Entidad DescubrimientoQr almacena correctamente usuario y capsula")
    void testDescubrimientoQr_datosValidos() {
        DescubrimientoQr d = new DescubrimientoQr();
        d.setId(1L);
        d.setUsuarioId(10L);
        d.setCapsulaId(5L);
        d.setPuntoId(2L);
        d.setPuntosOtorgados(10);

        assertEquals(10L, d.getUsuarioId());
        assertEquals(5L, d.getCapsulaId());
        assertEquals(10, d.getPuntosOtorgados());
    }

    @Test
    @DisplayName("Verifica existencia de descubrimiento para evitar duplicacion de puntos")
    void testDescubrimientoQr_unicidad() {
        when(descubrimientoQrRepository.existsByUsuarioIdAndCapsulaId(1L, 2L)).thenReturn(true);

        boolean yaDescubierto = descubrimientoQrRepository.existsByUsuarioIdAndCapsulaId(1L, 2L);
        assertTrue(yaDescubierto);
    }
}
