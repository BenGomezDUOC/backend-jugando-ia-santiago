package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.PuntoInteres;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import cl.triskeledu.localizaciones.repository.ContenidoEducativoRepository;
import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite 3 (Backend - ms-localizaciones): Pruebas de reglas de negocio para Puntos Turísticos.
 * Contiene 5 pruebas unitarias independientes con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class PuntoInteresServiceTest {

    @Mock
    private PuntoInteresRepository puntoInteresRepository;

    @Mock
    private CapsulaRepository capsulaRepository;

    @Mock
    private ContenidoEducativoRepository contenidoEducativoRepository;

    @InjectMocks
    private PuntoInteresService puntoInteresService;

    private PuntoInteres punto;

    @BeforeEach
    void setUp() {
        punto = new PuntoInteres();
        punto.setId(1L);
        punto.setNombre("Palacio de La Moneda");
        punto.setLatitud(-33.4430);
        punto.setLongitud(-70.6539);
        punto.setRadioToleranciaMetros(50.0);
    }

    @Test
    @DisplayName("Obtener todos los puntos retorna la lista del repositorio")
    void testObtenerTodos_retornaLista() {
        when(puntoInteresRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(punto));

        var lista = puntoInteresService.obtenerTodos();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Palacio de La Moneda", lista.get(0).getNombre());
    }

    @Test
    @DisplayName("Obtener por id lanza 404 si el punto no existe")
    void testObtenerPorId_noExiste() {
        when(puntoInteresRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                puntoInteresService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Obtener por id retorna el punto si existe")
    void testObtenerPorId_exitoso() {
        when(puntoInteresRepository.findById(1L)).thenReturn(Optional.of(punto));

        var result = puntoInteresService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(-33.4430, result.getLatitud());
    }

    @Test
    @DisplayName("Impide eliminar punto si tiene contenidos educativos asociados")
    void testEliminar_conContenidosAsociados_falla() {
        when(puntoInteresRepository.findById(1L)).thenReturn(Optional.of(punto));
        when(contenidoEducativoRepository.existsByPuntoId(1L)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () ->
                puntoInteresService.eliminar(1L));
        verify(puntoInteresRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Permite eliminar punto cuando no tiene dependencias asociadas")
    void testEliminar_sinDependencias_exitoso() {
        when(puntoInteresRepository.findById(1L)).thenReturn(Optional.of(punto));
        when(contenidoEducativoRepository.existsByPuntoId(1L)).thenReturn(false);
        when(capsulaRepository.existsByPuntoId(1L)).thenReturn(false);

        assertDoesNotThrow(() -> puntoInteresService.eliminar(1L));
        verify(puntoInteresRepository, times(1)).delete(punto);
    }
}
