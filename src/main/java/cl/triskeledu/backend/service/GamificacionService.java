package cl.triskeledu.backend.service;

import cl.triskeledu.backend.dto.EscanearQrRequest;
import cl.triskeledu.backend.dto.VisitaSelfieRequest;
import cl.triskeledu.backend.entity.*;
import cl.triskeledu.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GamificacionService {

    private final UsuarioRepository usuarioRepository;
    private final CapsulaRepository capsulaRepository;
    private final DescubrimientoQrRepository descubrimientoQrRepository;
    private final VisitaRealizadaRepository visitaRealizadaRepository;
    private final VideoVistoRepository videoVistoRepository;
    private final VideoAdicionalVistoRepository videoAdicionalVistoRepository;
    private final NotificacionService notificacionService;

    @Transactional
    public Map<String, Object> escanearQr(EscanearQrRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Capsula capsula = capsulaRepository.findByCodigoQr(request.getCodigoQr().trim())
                .orElseThrow(() -> new IllegalArgumentException("El código QR no pertenece a Triskeledu"));

        boolean yaDescubierto = descubrimientoQrRepository.existsByUsuarioIdAndCapsulaId(usuario.getId(), capsula.getId());

        int puntosOtorgados = 0;
        String mensaje;

        if (!yaDescubierto) {
            DescubrimientoQr descubrimiento = DescubrimientoQr.builder()
                    .usuarioId(usuario.getId())
                    .capsulaId(capsula.getId())
                    .fechaTimestamp(System.currentTimeMillis())
                    .build();
            descubrimientoQrRepository.save(descubrimiento);

            puntosOtorgados = 10;
            usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + puntosOtorgados);
            usuarioRepository.save(usuario);

            mensaje = "¡QR descubierto exitosamente! Has ganado 10 puntos.";

            notificacionService.crearNotificacion(
                    usuario.getId(),
                    "¡Nuevo QR Descubierto! 🎯",
                    "Descubriste la cápsula: " + capsula.getTitulo() + " (+10 pts)",
                    "QR",
                    "coleccion"
            );
        } else {
            mensaje = "Cápsula ya descubierta anteriormente. No suma puntos adicionales.";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("capsula", capsula);
        response.put("puntosGanados", puntosOtorgados);
        response.put("totalPuntosUsuario", usuario.getPuntosAcumulados());
        response.put("mensaje", mensaje);
        response.put("nuevoDescubrimiento", !yaDescubierto);

        return response;
    }

    @Transactional
    public Map<String, Object> registrarVisitaSelfie(VisitaSelfieRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        boolean yaVisitado = visitaRealizadaRepository.existsByUsuarioIdAndPuntoId(usuario.getId(), request.getPuntoId());
        if (yaVisitado) {
            throw new IllegalArgumentException("Ya has registrado tu visita en terreno a este punto turístico");
        }

        VisitaRealizada visita = VisitaRealizada.builder()
                .usuarioId(usuario.getId())
                .puntoId(request.getPuntoId())
                .capsulaId(request.getCapsulaId())
                .rutaFotoSelfie(request.getRutaFotoSelfie())
                .puntosGanados(100)
                .fechaTimestamp(System.currentTimeMillis())
                .completada(true)
                .build();
        visitaRealizadaRepository.save(visita);

        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + 100);
        usuarioRepository.save(usuario);

        notificacionService.crearNotificacion(
                usuario.getId(),
                "¡Selfie Validada en Terreno! 📸",
                "Completaste la misión turística. ¡Ganaste 100 puntos!",
                "EVIDENCIA",
                "historial_actividades"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("visita", visita);
        response.put("puntosGanados", 100);
        response.put("totalPuntosUsuario", usuario.getPuntosAcumulados());
        response.put("mensaje", "¡Visita y selfie validada con éxito! Ganaste 100 puntos.");

        return response;
    }

    @Transactional
    public void registrarVideoVisto(Long usuarioId, Long capsulaId) {
        if (!videoVistoRepository.existsByUsuarioIdAndCapsulaId(usuarioId, capsulaId)) {
            VideoVisto videoVisto = VideoVisto.builder()
                    .usuarioId(usuarioId)
                    .capsulaId(capsulaId)
                    .fechaTimestamp(System.currentTimeMillis())
                    .build();
            videoVistoRepository.save(videoVisto);

            notificacionService.crearNotificacion(
                    usuarioId,
                    "¡Cápsula de IA completada! 🎬",
                    "Has completado la visualización de la cápsula educativa.",
                    "VIDEO",
                    "coleccion"
            );
        }
    }

    @Transactional
    public void registrarVideoAdicionalVisto(Long usuarioId, Integer videoAdicionalId) {
        if (!videoAdicionalVistoRepository.existsByUsuarioIdAndVideoAdicionalId(usuarioId, videoAdicionalId)) {
            VideoAdicionalVisto videoAdicionalVisto = VideoAdicionalVisto.builder()
                    .usuarioId(usuarioId)
                    .videoAdicionalId(videoAdicionalId)
                    .fechaTimestamp(System.currentTimeMillis())
                    .build();
            videoAdicionalVistoRepository.save(videoAdicionalVisto);
        }
    }

    public List<DescubrimientoQr> obtenerDescubrimientosPorUsuario(Long usuarioId) {
        return descubrimientoQrRepository.findByUsuarioId(usuarioId);
    }

    public List<VisitaRealizada> obtenerVisitasPorUsuario(Long usuarioId) {
        return visitaRealizadaRepository.findByUsuarioId(usuarioId);
    }
}
