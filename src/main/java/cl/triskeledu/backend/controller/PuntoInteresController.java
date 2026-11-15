package cl.triskeledu.backend.controller;

import cl.triskeledu.backend.entity.PuntoInteres;
import cl.triskeledu.backend.service.PuntoInteresService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puntos")
@RequiredArgsConstructor
public class PuntoInteresController {

    private final PuntoInteresService puntoInteresService;

    @GetMapping
    public ResponseEntity<List<PuntoInteres>> obtenerTodos() {
        return ResponseEntity.ok(puntoInteresService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuntoInteres> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(puntoInteresService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PuntoInteres> crear(@Valid @RequestBody PuntoInteres punto) {
        PuntoInteres guardado = puntoInteresService.guardar(punto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuntoInteres> actualizar(@PathVariable Long id, @Valid @RequestBody PuntoInteres punto) {
        punto.setId(id);
        return ResponseEntity.ok(puntoInteresService.guardar(punto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        puntoInteresService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
