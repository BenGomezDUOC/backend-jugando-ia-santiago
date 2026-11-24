package cl.triskeledu.localizaciones.controller;

import cl.triskeledu.localizaciones.config.UsuarioAutenticado;
import cl.triskeledu.localizaciones.entity.Capsula;
import cl.triskeledu.localizaciones.service.CapsulaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({
        "/api/localizaciones/capsulas",
        "/api/capsulas"
})
@RequiredArgsConstructor
public class CapsulaController {

    private final CapsulaService capsulaService;

    // --------------------------------------------------
    // CONSULTAR CÁPSULAS
    // --------------------------------------------------

    @GetMapping
    public ResponseEntity<List<Capsula>> obtenerTodas(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(
                    name = "puntoId",
                    required = false
            ) Long puntoId
    ) {

        boolean esCliente =
                UsuarioAutenticado.esCliente(jwt);

        List<Capsula> capsulas;

        if (puntoId != null) {

            capsulas = capsulaService.obtenerPorPunto(
                    puntoId,
                    esCliente
            );

        } else {

            capsulas = esCliente
                    ? capsulaService.obtenerPublicadas()
                    : capsulaService.obtenerTodas();
        }

        return ResponseEntity.ok(capsulas);
    }

    // --------------------------------------------------
    // VALIDAR QR ESCANEADO
    // --------------------------------------------------

    @GetMapping("/validar-qr")
    public ResponseEntity<Capsula> validarQr(
            @RequestParam("codigo") String codigo
    ) {

        return ResponseEntity.ok(
                capsulaService.validarQr(codigo)
        );
    }

    // --------------------------------------------------
    // ADMINISTRADOR: GENERAR CÓDIGO QR
    // --------------------------------------------------

    @PostMapping
    public ResponseEntity<Capsula> crear(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Capsula capsula
    ) {

        Capsula guardada = capsulaService.guardar(
                capsula,
                UsuarioAutenticado.obtenerId(jwt),
                UsuarioAutenticado.esAdministrador(jwt)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardada);
    }

    // --------------------------------------------------
    // ADMINISTRADOR: PUBLICAR CÁPSULA
    // --------------------------------------------------

    @PutMapping("/{id}/publicar")
    public ResponseEntity<Capsula> publicar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        Capsula publicada = capsulaService.publicar(
                id,
                UsuarioAutenticado.obtenerId(jwt),
                UsuarioAutenticado.esAdministrador(jwt)
        );

        return ResponseEntity.ok(publicada);
    }

    // --------------------------------------------------
    // ADMINISTRADOR: DESPUBLICAR CÁPSULA
    // --------------------------------------------------

    @PutMapping("/{id}/despublicar")
    public ResponseEntity<Capsula> despublicar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long id
    ) {

        Capsula despublicada = capsulaService.despublicar(
                id,
                UsuarioAutenticado.obtenerId(jwt),
                UsuarioAutenticado.esAdministrador(jwt)
        );

        return ResponseEntity.ok(despublicada);
    }
}