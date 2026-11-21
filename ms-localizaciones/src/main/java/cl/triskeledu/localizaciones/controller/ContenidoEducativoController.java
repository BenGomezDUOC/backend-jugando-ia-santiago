package cl.triskeledu.localizaciones.controller;

import cl.triskeledu.localizaciones.entity.ContenidoEducativo;
import cl.triskeledu.localizaciones.repository.ContenidoEducativoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/localizaciones/contenidos", "/api/contenidos"})
@RequiredArgsConstructor
public class ContenidoEducativoController {

    private final ContenidoEducativoRepository contenidoRepository;

    @GetMapping
    public ResponseEntity<List<ContenidoEducativo>> obtenerTodos(@RequestParam(required = false) Long puntoId) {
        if (puntoId != null) {
            return ResponseEntity.ok(contenidoRepository.findByPuntoId(puntoId));
        }
        return ResponseEntity.ok(contenidoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenidoEducativo> obtenerPorId(@PathVariable Long id) {
        return contenidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContenidoEducativo> crear(@Valid @RequestBody ContenidoEducativo contenido) {
        ContenidoEducativo guardado = contenidoRepository.save(contenido);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContenidoEducativo> actualizar(@PathVariable Long id, @Valid @RequestBody ContenidoEducativo contenido) {
        if (!contenidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        contenido.setId(id);
        return ResponseEntity.ok(contenidoRepository.save(contenido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!contenidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        contenidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
