package cl.triskeledu.clientes.service;

import cl.triskeledu.clientes.dto.OperacionPuntosRequest;
import cl.triskeledu.clientes.entity.Usuario;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.repository.OperacionPuntosRepository;
import cl.triskeledu.clientes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite 2 (Backend - ms-clientes): Pruebas de reglas de negocio en la gestión de puntos.
 * Contiene 5 pruebas unitarias independientes con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class OperacionPuntosServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OperacionPuntosRepository operacionPuntosRepository;

    @InjectMocks
    private OperacionPuntosService operacionPuntosService;

    private Usuario usuarioCliente;

    @BeforeEach
    void setUp() {
        usuarioCliente = new Usuario();
        usuarioCliente.setId(1L);
        usuarioCliente.setNombre("Estudiante");
        usuarioCliente.setRol(Rol.CLIENTE);
        usuarioCliente.setPuntosAcumulados(50);
    }

    @Test
    @DisplayName("Rechaza operacion cuando el usuarioId es nulo o menor o igual a cero")
    void testProcesarOperacion_usuarioIdInvalido() {
        OperacionPuntosRequest req = new OperacionPuntosRequest();
        assertThrows(IllegalArgumentException.class, () ->
                operacionPuntosService.procesarOperacion(null, req));
        assertThrows(IllegalArgumentException.class, () ->
                operacionPuntosService.procesarOperacion(0L, req));
    }

    @Test
    @DisplayName("Lanza excepcion si el usuario no existe en base de datos")
    void testProcesarOperacion_usuarioNoExiste() {
        when(usuarioRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        OperacionPuntosRequest req = new OperacionPuntosRequest();
        req.setPuntos(10);
        req.setMotivo("Escaneo QR");

        assertThrows(IllegalArgumentException.class, () ->
                operacionPuntosService.procesarOperacion(99L, req));
    }

    @Test
    @DisplayName("Rechaza asignacion de puntos a un usuario que no tiene rol CLIENTE")
    void testProcesarOperacion_rolNoEsCliente() {
        Usuario admin = new Usuario();
        admin.setId(2L);
        admin.setRol(Rol.ADMINISTRADOR);

        when(usuarioRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(admin));

        OperacionPuntosRequest req = new OperacionPuntosRequest();
        req.setPuntos(10);
        req.setMotivo("Prueba");

        assertThrows(IllegalArgumentException.class, () ->
                operacionPuntosService.procesarOperacion(2L, req));
    }

    @Test
    @DisplayName("Calcula correctamente el nuevo saldo sumando puntos")
    void testProcesarOperacion_sumaPuntosExitosa() {
        when(usuarioRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(usuarioCliente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        OperacionPuntosRequest req = new OperacionPuntosRequest();
        req.setPuntos(10);
        req.setMotivo("Descubrimiento QR");

        var response = operacionPuntosService.procesarOperacion(1L, req);

        assertNotNull(response);
        assertEquals(60, response.getNuevoSaldo());
        verify(operacionPuntosRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Rechaza deduccion si el usuario no tiene saldo suficiente")
    void testProcesarOperacion_saldoInsuficiente() {
        when(usuarioRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(usuarioCliente));

        OperacionPuntosRequest req = new OperacionPuntosRequest();
        req.setPuntos(-100);
        req.setMotivo("Canje premio grande");

        assertThrows(IllegalArgumentException.class, () ->
                operacionPuntosService.procesarOperacion(1L, req));
    }
}
