package cl.triskeledu.localizaciones.controller;

import cl.triskeledu.localizaciones.config.UsuarioAutenticado;
import cl.triskeledu.localizaciones.entity.ContenidoEducativo;
import cl.triskeledu.localizaciones.entity.VideoRegistrado;
import cl.triskeledu.localizaciones.service.ContenidoEducativoService;
import cl.triskeledu.localizaciones.service.VideoRegistradoService;

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
        "/api/localizaciones/videos",
        "/api/videos"
})
@RequiredArgsConstructor
public class VideoRegistradoController {

    private final VideoRegistradoService videoService;
    private final ContenidoEducativoService contenidoService;

    @GetMapping
    public ResponseEntity<List<VideoRegistrado>> obtenerTodos(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(name = "contenidoId", required = false)
            Long contenidoId
    ) {

        return ResponseEntity.ok(
                videoService.obtenerTodos(
                        contenidoId,
                        UsuarioAutenticado.esCliente(jwt)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoRegistrado> obtenerPorId(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        VideoRegistrado video = videoService.obtenerPorId(id);

        ContenidoEducativo contenido =
                contenidoService.obtenerPorId(
                        video.getContenidoId()
                );

        if (UsuarioAutenticado.esCliente(jwt)
                && !"PUBLICADA".equals(
                contenido.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Video no disponible"
            );
        }

        return ResponseEntity.ok(video);
    }

    @PostMapping
    public ResponseEntity<VideoRegistrado> crear(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody VideoRegistrado video
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(videoService.crear(
                        video,
                        UsuarioAutenticado.obtenerId(jwt),
                        UsuarioAutenticado.esAdministrador(jwt)
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoRegistrado> actualizar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id,
            @Valid @RequestBody VideoRegistrado video
    ) {

        return ResponseEntity.ok(
                videoService.actualizar(
                        id,
                        video,
                        UsuarioAutenticado.obtenerId(jwt),
                        UsuarioAutenticado.esAdministrador(jwt)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        videoService.eliminar(
                id,
                UsuarioAutenticado.obtenerId(jwt),
                UsuarioAutenticado.esAdministrador(jwt)
        );

        return ResponseEntity.noContent().build();
    }
}