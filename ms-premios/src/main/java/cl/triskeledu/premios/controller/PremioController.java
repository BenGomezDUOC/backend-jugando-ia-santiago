package cl.triskeledu.premios.controller;

import cl.triskeledu.premios.client.ClientesClient;
import cl.triskeledu.premios.entity.Premio;
import cl.triskeledu.premios.repository.PremioRepository;
import cl.triskeledu.premios.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/premios", "/api/premios/catalogo"})
@RequiredArgsConstructor
public class PremioController {

    private final PremioRepository premioRepository;
    private final ClientesClient clientesClient;
    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<Premio>> obtenerPremios() {
        return ResponseEntity.ok(premioRepository.findByActivoTrueOrderByPuntosRequeridosAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Premio> obtenerPorId(@PathVariable Long id) {
        return premioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Premio> crear(@Valid @RequestBody Premio premio) {
        Premio guardado = premioRepository.save(premio);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PostMapping("/canjear")
    public ResponseEntity<Map<String, Object>> canjearPremio(
            @RequestParam Long usuarioId,
            @RequestParam Long premioId
    ) {
        Premio premio = premioRepository.findById(premioId)
                .orElseThrow(() -> new IllegalArgumentException("Premio no encontrado"));

        if (premio.getStockDisponible() <= 0) {
            throw new IllegalArgumentException("El premio seleccionado no tiene stock disponible");
        }

        // Descontar puntos en ms-clientes
        clientesClient.sumarPuntos(usuarioId, -premio.getPuntosRequeridos());

        // Descontar stock
        premio.setStockDisponible(premio.getStockDisponible() - 1);
        premioRepository.save(premio);

        // Notificar al usuario
        notificacionService.crearNotificacion(
                usuarioId,
                "¡Premio Canjeado! 🎁",
                "Has canjeado exitosamente: " + premio.getNombre(),
                "PREMIO",
                "perfil"
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("mensaje", "Premio canjeado con éxito");
        resp.put("premio", premio);
        return ResponseEntity.ok(resp);
    }
}
