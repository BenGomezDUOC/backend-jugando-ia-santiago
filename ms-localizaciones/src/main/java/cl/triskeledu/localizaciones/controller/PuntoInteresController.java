package cl.triskeledu.localizaciones.controller;

import cl.triskeledu.localizaciones.entity.PuntoInteres;
import cl.triskeledu.localizaciones.service.PuntoInteresService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({
        "/api/localizaciones/puntos-interes",
        "/api/localizaciones/puntos",
        "/api/puntos-interes",
        "/api/puntos"
})
@RequiredArgsConstructor
public class PuntoInteresController {

    private final PuntoInteresService puntoInteresService;

    @GetMapping
    public ResponseEntity<List<PuntoInteres>> obtenerTodos() {

        return ResponseEntity.ok(
                puntoInteresService.obtenerTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuntoInteres> obtenerPorId(
            @PathVariable("id") Long id
    ) {

        return ResponseEntity.ok(
                puntoInteresService.obtenerPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<PuntoInteres> crear(
            @Valid @RequestBody PuntoInteres punto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(puntoInteresService.guardar(punto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuntoInteres> actualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody PuntoInteres punto
    ) {

        return ResponseEntity.ok(
                puntoInteresService.actualizar(id, punto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable("id") Long id
    ) {

        puntoInteresService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}