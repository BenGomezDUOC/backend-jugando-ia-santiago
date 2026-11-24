package cl.triskeledu.clientes.controller;

import cl.triskeledu.clientes.dto.OperacionPuntosRequest;
import cl.triskeledu.clientes.dto.OperacionPuntosResponse;
import cl.triskeledu.clientes.service.OperacionPuntosService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interno/usuarios")
@RequiredArgsConstructor
public class OperacionPuntosController {

    private final OperacionPuntosService operacionPuntosService;

    // --------------------------------------------------
    // PROCESAR OPERACIÓN INTERNA DE PUNTOS
    // --------------------------------------------------

    @PostMapping("/{id}/puntos")
    public ResponseEntity<OperacionPuntosResponse> procesarPuntos(
            @PathVariable("id") Long id,
            @Valid @RequestBody OperacionPuntosRequest request
    ) {

        // Validar el identificador del usuario.
        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "El identificador del usuario no es válido"
            );
        }

        // Procesar la operación de manera transaccional.
        OperacionPuntosResponse respuesta =
                operacionPuntosService.procesarOperacion(
                        id,
                        request
                );

        // Devolver el resultado a ms-premios.
        return ResponseEntity.ok(respuesta);
    }
}
