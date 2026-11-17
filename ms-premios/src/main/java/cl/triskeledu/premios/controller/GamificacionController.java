package cl.triskeledu.premios.controller;

import cl.triskeledu.premios.dto.EscanearQrRequest;
import cl.triskeledu.premios.dto.VisitaSelfieRequest;
import cl.triskeledu.premios.entity.DescubrimientoQr;
import cl.triskeledu.premios.entity.VisitaRealizada;
import cl.triskeledu.premios.service.GamificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/premios/gamificacion", "/api/gamificacion"})
@RequiredArgsConstructor
public class GamificacionController {

    private final GamificacionService gamificacionService;

    @PostMapping("/escanear-qr")
    public ResponseEntity<Map<String, Object>> escanearQr(@Valid @RequestBody EscanearQrRequest request) {
        return ResponseEntity.ok(gamificacionService.escanearQr(request));
    }

    @PostMapping("/visita-selfie")
    public ResponseEntity<Map<String, Object>> registrarVisitaSelfie(@Valid @RequestBody VisitaSelfieRequest request) {
        return ResponseEntity.ok(gamificacionService.registrarVisitaSelfie(request));
    }

    @PostMapping("/video-visto")
    public ResponseEntity<Void> registrarVideoVisto(@RequestParam Long usuarioId, @RequestParam Long capsulaId) {
        gamificacionService.registrarVideoVisto(usuarioId, capsulaId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/video-adicional-visto")
    public ResponseEntity<Void> registrarVideoAdicionalVisto(@RequestParam Long usuarioId, @RequestParam Integer videoAdicionalId) {
        gamificacionService.registrarVideoAdicionalVisto(usuarioId, videoAdicionalId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/descubrimientos/{usuarioId}")
    public ResponseEntity<List<DescubrimientoQr>> obtenerDescubrimientos(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(gamificacionService.obtenerDescubrimientosPorUsuario(usuarioId));
    }

    @GetMapping("/visitas/{usuarioId}")
    public ResponseEntity<List<VisitaRealizada>> obtenerVisitas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(gamificacionService.obtenerVisitasPorUsuario(usuarioId));
    }
}
