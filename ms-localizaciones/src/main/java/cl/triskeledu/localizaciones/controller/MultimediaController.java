
package cl.triskeledu.localizaciones.controller;

import cl.triskeledu.localizaciones.config.UsuarioAutenticado;
import cl.triskeledu.localizaciones.service.MultimediaService;

import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import cl.triskeledu.localizaciones.repository.VideoRegistradoRepository;
import cl.triskeledu.localizaciones.repository.ContenidoEducativoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/localizaciones/multimedia")
@RequiredArgsConstructor
public class MultimediaController {

    private final MultimediaService multimediaService;
    private final PuntoInteresRepository puntoRepository;
    private final CapsulaRepository capsulaRepository;
    private final VideoRegistradoRepository videoRepository;
    private final ContenidoEducativoRepository contenidoRepository;

    // ADMINISTRADOR: subir fotografías y miniaturas.
    @PostMapping("/imagen")
    public ResponseEntity<Map<String, String>> subirImagen(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("archivo") MultipartFile archivo
    ) {

        exigirAdministrador(jwt);

        String url = multimediaService.guardar(
                archivo,
                "imagen",
                UsuarioAutenticado.obtenerId(jwt)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("url", url));
    }

    // ADMINISTRADOR: subir videos MP4.
    @PostMapping("/video")
    public ResponseEntity<Map<String, String>> subirVideo(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("archivo") MultipartFile archivo
    ) {

        exigirAdministrador(jwt);

        String url = multimediaService.guardar(
                archivo,
                "video",
                UsuarioAutenticado.obtenerId(jwt)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("url", url));
    }

    // Recuperar imágenes y videos autorizados.
    @GetMapping("/{nombre}")
    public ResponseEntity<Resource> obtener(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("nombre") String nombre
    ) {

        Long usuarioId = UsuarioAutenticado.obtenerId(jwt);

        String referencia =
                "/api/localizaciones/multimedia/" + nombre;

        boolean autorizado =
                UsuarioAutenticado.esAdministrador(jwt)

                        // Archivo propio.
                        || nombre.startsWith(usuarioId + "-")

                        // Imagen de un punto turístico.
                        || puntoRepository.existsByImagenUri(referencia)

                        // Video de una cápsula publicada con QR.
                        || capsulaRepository.existsByUrlVideoAndEstado(
                        referencia, "PUBLICADA"
                )

                        // Miniatura de una cápsula publicada con QR.
                        || videoRepository
                        .findByUrlMiniatura(referencia)
                        .stream()
                        .anyMatch(video ->
                                capsulaRepository
                                        .existsByVideoIdAndEstado(
                                                video.getId(),
                                                "PUBLICADA"
                                        )
                        )

                        // Video de contenido publicado SIN QR.
                        || videoRepository
                        .findByUrlVideo(referencia)
                        .stream()
                        .anyMatch(video ->
                                esContenidoSinQrPublicado(
                                        video.getContenidoId()
                                )
                        )

                        // Miniatura de contenido publicado SIN QR.
                        || videoRepository
                        .findByUrlMiniatura(referencia)
                        .stream()
                        .anyMatch(video ->
                                esContenidoSinQrPublicado(
                                        video.getContenidoId()
                                )
                        );

        if (!autorizado) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Archivo no disponible"
            );
        }

        Resource recurso = multimediaService.obtener(nombre);

        MediaType tipo;

        if (nombre.endsWith(".mp4")) {
            tipo = MediaType.parseMediaType("video/mp4");
        } else if (nombre.endsWith(".png")) {
            tipo = MediaType.IMAGE_PNG;
        } else {
            tipo = MediaType.IMAGE_JPEG;
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(recurso);
    }

    private boolean esContenidoSinQrPublicado(Long contenidoId) {

        if (contenidoId == null) {
            return false;
        }

        return contenidoRepository.findById(contenidoId)
                .map(contenido ->
                        contenido.getPuntoId() == null
                                && "PUBLICADA".equals(
                                contenido.getEstado()
                        )
                )
                .orElse(false);
    }

    private void exigirAdministrador(Jwt jwt) {

        if (!UsuarioAutenticado.esAdministrador(jwt)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el administrador puede subir archivos"
            );
        }
    }
}
