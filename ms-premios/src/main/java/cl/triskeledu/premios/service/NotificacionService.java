package cl.triskeledu.premios.service;

import cl.triskeledu.premios.entity.Notificacion;
import cl.triskeledu.premios.repository.NotificacionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository
            notificacionRepository;

    // --------------------------------------------------
    // CONSULTAR NOTIFICACIONES
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<Notificacion> obtenerPorUsuario(
            Long usuarioId
    ) {

        validarId(usuarioId);

        return notificacionRepository
                .findByUsuarioIdOrderByFechaTimestampDesc(
                        usuarioId
                );
    }

    // --------------------------------------------------
    // CONTAR NOTIFICACIONES NO LEÍDAS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public long contarNoLeidas(Long usuarioId) {

        validarId(usuarioId);

        return notificacionRepository
                .countByUsuarioIdAndLeidaFalse(
                        usuarioId
                );
    }

    // --------------------------------------------------
    // CREAR NOTIFICACIÓN
    // --------------------------------------------------

    @Transactional
    public Notificacion crearNotificacion(
            Long usuarioId,
            String titulo,
            String mensaje,
            String tipo,
            String rutaDestino
    ) {

        validarId(usuarioId);

        if (titulo == null
                || titulo.isBlank()
                || titulo.length() > 150) {

            throw new IllegalArgumentException(
                    "El título de la notificación no es válido"
            );
        }

        if (mensaje == null
                || mensaje.isBlank()
                || mensaje.length() > 500) {

            throw new IllegalArgumentException(
                    "El mensaje de la notificación no es válido"
            );
        }

        if (tipo == null
                || tipo.isBlank()
                || tipo.length() > 50) {

            throw new IllegalArgumentException(
                    "El tipo de notificación no es válido"
            );
        }

        if (rutaDestino == null
                || rutaDestino.isBlank()
                || rutaDestino.length() > 100) {

            throw new IllegalArgumentException(
                    "La ruta de destino no es válida"
            );
        }

        Notificacion notificacion =
                Notificacion.builder()
                        .usuarioId(usuarioId)
                        .titulo(titulo.trim())
                        .mensaje(mensaje.trim())
                        .tipo(tipo.trim())
                        .rutaDestino(rutaDestino.trim())
                        .fechaTimestamp(
                                System.currentTimeMillis()
                        )
                        .leida(false)
                        .build();

        return notificacionRepository.save(notificacion);
    }

    // --------------------------------------------------
    // MARCAR UNA NOTIFICACIÓN COMO LEÍDA
    // --------------------------------------------------

    @Transactional
    public void marcarComoLeida(
            Long usuarioId,
            Long notificacionId
    ) {

        validarId(usuarioId);
        validarId(notificacionId);

        // Solo buscar notificaciones que pertenezcan
        // al usuario autenticado.
        Notificacion notificacion =
                notificacionRepository
                        .findByIdAndUsuarioId(
                                notificacionId,
                                usuarioId
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Notificación no encontrada"
                                )
                        );

        notificacion.setLeida(true);

        notificacionRepository.save(notificacion);
    }

    // --------------------------------------------------
    // MARCAR TODAS COMO LEÍDAS
    // --------------------------------------------------

    @Transactional
    public void marcarTodasComoLeidas(
            Long usuarioId
    ) {

        validarId(usuarioId);

        notificacionRepository.marcarTodasComoLeidas(
                usuarioId
        );
    }

    // --------------------------------------------------
    // VALIDAR IDENTIFICADORES
    // --------------------------------------------------

    private void validarId(Long id) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "El identificador no es válido"
            );
        }
    }
}