package cl.triskeledu.premios.service;

import cl.triskeledu.premios.client.ClientesClient;
import cl.triskeledu.premios.client.LocalizacionesClient;
import cl.triskeledu.premios.dto.CapsulaResponse;
import cl.triskeledu.premios.dto.EscanearQrRequest;
import cl.triskeledu.premios.dto.UsuarioResponse;
import cl.triskeledu.premios.dto.VisitaSelfieRequest;
import cl.triskeledu.premios.entity.DescubrimientoQr;
import cl.triskeledu.premios.entity.VideoAdicionalVisto;
import cl.triskeledu.premios.entity.VideoVisto;
import cl.triskeledu.premios.entity.VisitaRealizada;
import cl.triskeledu.premios.repository.DescubrimientoQrRepository;
import cl.triskeledu.premios.repository.VideoAdicionalVistoRepository;
import cl.triskeledu.premios.repository.VideoVistoRepository;
import cl.triskeledu.premios.repository.VisitaRealizadaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificacionService {

    private final ClientesClient clientesClient;
    private final LocalizacionesClient localizacionesClient;
    private final DescubrimientoQrRepository descubrimientoQrRepository;
    private final VisitaRealizadaRepository visitaRealizadaRepository;
    private final VideoVistoRepository videoVistoRepository;
    private final VideoAdicionalVistoRepository videoAdicionalVistoRepository;
    private final NotificacionService notificacionService;

    @Transactional
    public Map<String, Object> escanearQr(EscanearQrRequest request) {
        // 1. Validar usuario en ms-clientes
        UsuarioResponse usuario = clientesClient.obtenerUsuario(request.getUsuarioId());

        // 2. Validar QR en ms-localizaciones (verifica existencia y estado PUBLICADA)
        CapsulaResponse capsula = localizacionesClient.validarQr(request.getCodigoQr().trim());

        // 3. Validar si el usuario seleccionó un punto turístico específico y coincide con el QR escaneado
        if (request.getPuntoIdSeleccionado() != null && capsula.getPuntoId() != null) {
            if (!capsula.getPuntoId().equals(request.getPuntoIdSeleccionado())) {
                throw new IllegalArgumentException("El código QR escaneado pertenece a otro punto turístico, no al seleccionado.");
            }
        }

        // 4. Verificar si ya fue descubierto
        boolean yaDescubierto = descubrimientoQrRepository.existsByUsuarioIdAndCapsulaId(usuario.getId(), capsula.getId());

        int puntosOtorgados = 0;
        int totalPuntos = usuario.getPuntosAcumulados() != null ? usuario.getPuntosAcumulados() : 0;
        String mensaje;

        if (!yaDescubierto) {
            DescubrimientoQr descubrimiento = DescubrimientoQr.builder()
                    .usuarioId(usuario.getId())
                    .capsulaId(capsula.getId())
                    .fechaTimestamp(System.currentTimeMillis())
                    .build();
            descubrimientoQrRepository.save(descubrimiento);

            puntosOtorgados = 10;
            UsuarioResponse usuarioActualizado = clientesClient.sumarPuntos(usuario.getId(), puntosOtorgados);
            totalPuntos = usuarioActualizado.getPuntosAcumulados() != null ? usuarioActualizado.getPuntosAcumulados() : totalPuntos + 10;

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
        response.put("totalPuntosUsuario", totalPuntos);
        response.put("mensaje", mensaje);
        response.put("nuevoDescubrimiento", !yaDescubierto);

        return response;
    }

    @Transactional
    public Map<String, Object> registrarVisitaSelfie(VisitaSelfieRequest request) {
        // 1. Validar usuario en ms-clientes
        UsuarioResponse usuario = clientesClient.obtenerUsuario(request.getUsuarioId());

        if (request.getRutaFotoSelfie() == null || request.getRutaFotoSelfie().isBlank()) {
            throw new IllegalArgumentException("La fotografía selfie es obligatoria para validar la evidencia");
        }

        Long capsulaIdFinal = request.getCapsulaId() != null ? request.getCapsulaId() : 0L;

        // 2. Validar que no haya registrado selfie para esta cápsula turística específica
        boolean yaVisitado;
        if (request.getCapsulaId() != null) {
            yaVisitado = visitaRealizadaRepository.existsByUsuarioIdAndCapsulaId(usuario.getId(), request.getCapsulaId());
            if (yaVisitado) {
                throw new IllegalArgumentException("Ya has registrado tu selfie y validado la bonificación para esta cápsula educativa");
            }
        } else {
            yaVisitado = visitaRealizadaRepository.existsByUsuarioIdAndPuntoId(usuario.getId(), request.getPuntoId());
            if (yaVisitado) {
                throw new IllegalArgumentException("Ya has registrado tu visita en terreno a este punto turístico");
            }
        }

        VisitaRealizada visita = VisitaRealizada.builder()
                .usuarioId(usuario.getId())
                .puntoId(request.getPuntoId())
                .capsulaId(capsulaIdFinal)
                .rutaFotoSelfie(request.getRutaFotoSelfie())
                .puntosGanados(100)
                .fechaTimestamp(System.currentTimeMillis())
                .completada(true)
                .build();
        visitaRealizadaRepository.save(visita);

        UsuarioResponse usuarioActualizado = clientesClient.sumarPuntos(usuario.getId(), 100);
        int totalPuntos = usuarioActualizado.getPuntosAcumulados() != null
                ? usuarioActualizado.getPuntosAcumulados()
                : (usuario.getPuntosAcumulados() != null ? usuario.getPuntosAcumulados() + 100 : 100);

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
        response.put("totalPuntosUsuario", totalPuntos);
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
