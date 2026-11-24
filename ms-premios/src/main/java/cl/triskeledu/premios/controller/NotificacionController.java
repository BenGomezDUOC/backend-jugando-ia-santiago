package cl.triskeledu.premios.controller;

import cl.triskeledu.premios.entity.Notificacion;
import cl.triskeledu.premios.service.NotificacionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping({
        "/api/premios/notificaciones",
        "/api/notificaciones"
})
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    // --------------------------------------------------
    // CONSULTAR NOTIFICACIONES PROPIAS
    // --------------------------------------------------

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>>
    obtenerPorUsuario(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("usuarioId") Long usuarioId
    ) {

        Long idAutenticado =
                obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(
                idAutenticado,
                usuarioId
        );

        return ResponseEntity.ok(
                notificacionService.obtenerPorUsuario(
                        idAutenticado
                )
        );
    }

    // --------------------------------------------------
    // CONTAR NOTIFICACIONES NO LEÍDAS
    // --------------------------------------------------

    @GetMapping("/usuario/{usuarioId}/no-leidas/count")
    public ResponseEntity<Long> contarNoLeidas(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("usuarioId") Long usuarioId
    ) {

        Long idAutenticado =
                obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(
                idAutenticado,
                usuarioId
        );

        return ResponseEntity.ok(
                notificacionService.contarNoLeidas(
                        idAutenticado
                )
        );
    }

    // --------------------------------------------------
    // MARCAR UNA NOTIFICACIÓN COMO LEÍDA
    // --------------------------------------------------

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        Long idAutenticado =
                obtenerUsuarioAutenticado(jwt);

        notificacionService.marcarComoLeida(
                idAutenticado,
                id
        );

        return ResponseEntity.ok().build();
    }

    // --------------------------------------------------
    // MARCAR TODAS COMO LEÍDAS
    // --------------------------------------------------

    @PutMapping("/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("usuarioId") Long usuarioId
    ) {

        Long idAutenticado =
                obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(
                idAutenticado,
                usuarioId
        );

        notificacionService.marcarTodasComoLeidas(
                idAutenticado
        );

        return ResponseEntity.ok().build();
    }

    // --------------------------------------------------
    // OBTENER USUARIO DESDE JWT
    // --------------------------------------------------

    private Long obtenerUsuarioAutenticado(Jwt jwt) {

        if (jwt == null || jwt.getSubject() == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        try {

            Long id = Long.valueOf(jwt.getSubject());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException ex) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identidad del usuario no válida"
            );
        }
    }

    // --------------------------------------------------
    // COMPROBAR PROPIETARIO
    // --------------------------------------------------

    private void comprobarPropietario(
            Long idAutenticado,
            Long idSolicitado
    ) {

        if (!idAutenticado.equals(idSolicitado)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes consultar ni modificar notificaciones de otro usuario"
            );
        }
    }
}