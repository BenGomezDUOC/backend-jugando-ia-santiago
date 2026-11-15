package cl.triskeledu.backend.controller;

import cl.triskeledu.backend.entity.Capsula;
import cl.triskeledu.backend.service.CapsulaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/capsulas")
@RequiredArgsConstructor
public class CapsulaController {

    private final CapsulaService capsulaService;

    @GetMapping
    public ResponseEntity<List<Capsula>> obtenerTodas(@RequestParam(required = false) Long puntoId) {
        if (puntoId != null) {
            return ResponseEntity.ok(capsulaService.obtenerPorPunto(puntoId));
        }
        return ResponseEntity.ok(capsulaService.obtenerTodas());
    }

    @GetMapping("/validar-qr")
    public ResponseEntity<Capsula> validarQr(@RequestParam String codigo) {
        return ResponseEntity.ok(capsulaService.validarQr(codigo));
    }

    @PostMapping
    public ResponseEntity<Capsula> crear(@Valid @RequestBody Capsula capsula) {
        Capsula guardada = capsulaService.guardar(capsula);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
}
