
package cl.triskeledu.localizaciones.controller;

import cl.triskeledu.localizaciones.config.UsuarioAutenticado;
import cl.triskeledu.localizaciones.entity.ContenidoEducativo;
import cl.triskeledu.localizaciones.service.ContenidoEducativoService;

import jakarta.validation.Valid;
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
        "/api/localizaciones/contenidos",
        "/api/contenidos"
})
@RequiredArgsConstructor
public class ContenidoEducativoController {

    private final ContenidoEducativoService contenidoService;

    // Consultar contenidos.
    @GetMapping
    public ResponseEntity<List<ContenidoEducativo>> obtenerTodos(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "puntoId", required = false)
            Long puntoId
    ) {

        return ResponseEntity.ok(
                contenidoService.obtenerTodos(
                        puntoId,
                        UsuarioAutenticado.esCliente(jwt)
                )
        );
    }

    // Consultar un contenido por ID.
    @GetMapping("/{id}")
    public ResponseEntity<ContenidoEducativo> obtenerPorId(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        ContenidoEducativo contenido =
                contenidoService.obtenerPorId(id);

        if (UsuarioAutenticado.esCliente(jwt)
                && !"PUBLICADA".equals(contenido.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Contenido no disponible"
            );
        }

        return ResponseEntity.ok(contenido);
    }

    // GENERADOR: crear contenido con o sin QR.
    @PostMapping
    public ResponseEntity<ContenidoEducativo> crear(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ContenidoEducativo contenido
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contenidoService.crear(
                        contenido,
                        UsuarioAutenticado.obtenerId(jwt)
                ));
    }

    // GENERADOR: editar contenido propio.
    @PutMapping("/{id}")
    public ResponseEntity<ContenidoEducativo> actualizar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id,
            @Valid @RequestBody ContenidoEducativo contenido
    ) {

        return ResponseEntity.ok(
                contenidoService.actualizar(
                        id,
                        contenido,
                        UsuarioAutenticado.obtenerId(jwt),
                        UsuarioAutenticado.esAdministrador(jwt)
                )
        );
    }

    // ADMINISTRADOR: publicar contenido SIN QR.
    @PutMapping("/{id}/publicar-sin-qr")
    public ResponseEntity<ContenidoEducativo> publicarSinQr(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        return ResponseEntity.ok(
                contenidoService.publicarSinQr(
                        id,
                        UsuarioAutenticado.esAdministrador(jwt)
                )
        );
    }

    // ADMINISTRADOR: despublicar contenido SIN QR.
    @PutMapping("/{id}/despublicar-sin-qr")
    public ResponseEntity<ContenidoEducativo> despublicarSinQr(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        return ResponseEntity.ok(
                contenidoService.despublicarSinQr(
                        id,
                        UsuarioAutenticado.esAdministrador(jwt)
                )
        );
    }

    // GENERADOR: eliminar contenido sin dependencias.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        contenidoService.eliminar(
                id,
                UsuarioAutenticado.obtenerId(jwt),
                UsuarioAutenticado.esAdministrador(jwt)
        );

        return ResponseEntity.noContent().build();
    }
}
