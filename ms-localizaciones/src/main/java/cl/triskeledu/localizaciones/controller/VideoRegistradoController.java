package cl.triskeledu.localizaciones.controller;

import cl.triskeledu.localizaciones.entity.VideoRegistrado;
import cl.triskeledu.localizaciones.repository.VideoRegistradoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/localizaciones/videos", "/api/videos"})
@RequiredArgsConstructor
public class VideoRegistradoController {

    private final VideoRegistradoRepository videoRepository;

    @GetMapping
    public ResponseEntity<List<VideoRegistrado>> obtenerTodos(@RequestParam(required = false) Long contenidoId) {
        if (contenidoId != null) {
            return ResponseEntity.ok(videoRepository.findByContenidoId(contenidoId));
        }
        return ResponseEntity.ok(videoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoRegistrado> obtenerPorId(@PathVariable Long id) {
        return videoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VideoRegistrado> crear(@Valid @RequestBody VideoRegistrado video) {
        VideoRegistrado guardado = videoRepository.save(video);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoRegistrado> actualizar(@PathVariable Long id, @Valid @RequestBody VideoRegistrado video) {
        if (!videoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        video.setId(id);
        return ResponseEntity.ok(videoRepository.save(video));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!videoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        videoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
