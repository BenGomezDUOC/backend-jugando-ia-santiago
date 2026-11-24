
package cl.triskeledu.premios.controller;

import cl.triskeledu.premios.dto.EscanearQrRequest;
import cl.triskeledu.premios.dto.VisitaSelfieRequest;

import cl.triskeledu.premios.entity.DescubrimientoQr;
import cl.triskeledu.premios.entity.VisitaRealizada;

import cl.triskeledu.premios.service.GamificacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({
        "/api/premios/gamificacion",
        "/api/gamificacion"
})
@RequiredArgsConstructor
public class GamificacionController {

    private final GamificacionService gamificacionService;

    // --------------------------------------------------
    // CLIENTE: ESCANEAR QR
    // --------------------------------------------------

    @PostMapping("/escanear-qr")
    public ResponseEntity<Map<String, Object>> escanearQr(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EscanearQrRequest request
    ) {

        Long usuarioId = obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(
                usuarioId,
                request.getUsuarioId()
        );

        return ResponseEntity.ok(
                gamificacionService.escanearQr(
                        usuarioId,
                        jwt.getTokenValue(),
                        request
                )
        );
    }

    // --------------------------------------------------
    // CLIENTE: REGISTRAR SELFIE CON ARCHIVO REAL
    // --------------------------------------------------

    @PostMapping(
            value = "/visita-selfie",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> registrarVisitaSelfie(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(
                    name = "usuarioId",
                    required = false
            ) Long usuarioIdSolicitado,
            @RequestParam("puntoId") Long puntoId,
            @RequestParam("capsulaId") Long capsulaId,
            @RequestParam("codigoQrEsperado") String codigoQrEsperado,
            @RequestParam("archivo") MultipartFile archivo
    ) {

        Long usuarioId = obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(
                usuarioId,
                usuarioIdSolicitado
        );

        VisitaSelfieRequest request =
                VisitaSelfieRequest.builder()
                        .usuarioId(usuarioId)
                        .puntoId(puntoId)
                        .capsulaId(capsulaId)
                        .codigoQrEsperado(codigoQrEsperado)
                        .build();

        return ResponseEntity.ok(
                gamificacionService.registrarVisitaSelfie(
                        usuarioId,
                        jwt.getTokenValue(),
                        request,
                        archivo
                )
        );
    }

    // --------------------------------------------------
    // CLIENTE: CONSULTAR SU FOTOGRAFÍA
    // --------------------------------------------------

    @GetMapping("/selfies/{nombre}")
    public ResponseEntity<Resource> obtenerSelfie(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("nombre") String nombre
    ) {

        Long usuarioId = obtenerUsuarioAutenticado(jwt);

        Resource recurso = gamificacionService.obtenerSelfie(
                usuarioId,
                nombre
        );

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(recurso);
    }

    // --------------------------------------------------
    // CLIENTE: REGISTRAR VIDEO DE CÁPSULA VISTO
    // --------------------------------------------------

    @PostMapping("/video-visto")
    public ResponseEntity<Void> registrarVideoVisto(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(
                    name = "usuarioId",
                    required = false
            ) Long usuarioId,
            @RequestParam("capsulaId") Long capsulaId
    ) {

        Long idAutenticado = obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(idAutenticado, usuarioId);

        gamificacionService.registrarVideoVisto(
                idAutenticado,
                capsulaId
        );

        return ResponseEntity.ok().build();
    }

    // --------------------------------------------------
    // CLIENTE: REGISTRAR VIDEO ADICIONAL VISTO
    // --------------------------------------------------

    @PostMapping("/video-adicional-visto")
    public ResponseEntity<Void> registrarVideoAdicionalVisto(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(
                    name = "usuarioId",
                    required = false
            ) Long usuarioId,
            @RequestParam("videoAdicionalId")
            Integer videoAdicionalId
    ) {

        Long idAutenticado = obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(idAutenticado, usuarioId);

        gamificacionService.registrarVideoAdicionalVisto(
                idAutenticado,
                videoAdicionalId
        );

        return ResponseEntity.ok().build();
    }

    // --------------------------------------------------
    // CLIENTE: CONSULTAR DESCUBRIMIENTOS
    // --------------------------------------------------

    @GetMapping("/descubrimientos/{usuarioId}")
    public ResponseEntity<List<DescubrimientoQr>>
    obtenerDescubrimientos(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("usuarioId") Long usuarioId
    ) {

        Long idAutenticado = obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(idAutenticado, usuarioId);

        return ResponseEntity.ok(
                gamificacionService
                        .obtenerDescubrimientosPorUsuario(
                                idAutenticado
                        )
        );
    }

    // --------------------------------------------------
    // CLIENTE: CONSULTAR VISITAS
    // --------------------------------------------------

    @GetMapping("/visitas/{usuarioId}")
    public ResponseEntity<List<VisitaRealizada>>
    obtenerVisitas(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("usuarioId") Long usuarioId
    ) {

        Long idAutenticado = obtenerUsuarioAutenticado(jwt);

        comprobarPropietario(idAutenticado, usuarioId);

        return ResponseEntity.ok(
                gamificacionService
                        .obtenerVisitasPorUsuario(
                                idAutenticado
                        )
        );
    }

    // --------------------------------------------------
    // SEGURIDAD DEL USUARIO
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

    private void comprobarPropietario(
            Long idAutenticado,
            Long idSolicitado
    ) {

        if (idSolicitado != null
                && !idAutenticado.equals(idSolicitado)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes realizar operaciones de otro usuario"
            );
        }
    }
}
