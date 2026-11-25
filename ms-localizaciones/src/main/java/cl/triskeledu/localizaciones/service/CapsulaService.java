package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.Capsula;
import cl.triskeledu.localizaciones.entity.ContenidoEducativo;
import cl.triskeledu.localizaciones.entity.VideoRegistrado;

import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import cl.triskeledu.localizaciones.repository.ContenidoEducativoRepository;
import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
import cl.triskeledu.localizaciones.repository.VideoRegistradoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CapsulaService {

    private final CapsulaRepository capsulaRepository;
    private final ContenidoEducativoRepository contenidoRepository;
    private final PuntoInteresRepository puntoRepository;
    private final VideoRegistradoRepository videoRepository;

    @Transactional(readOnly = true)
    public List<Capsula> obtenerTodas() {
        return capsulaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Capsula> obtenerPublicadas() {
        return capsulaRepository.findByEstado("PUBLICADA");
    }

    @Transactional(readOnly = true)
    public List<Capsula> obtenerPorPunto(
            Long puntoId,
            boolean soloPublicadas
    ) {
        return soloPublicadas
                ? capsulaRepository.findByPuntoIdAndEstado(
                puntoId, "PUBLICADA")
                : capsulaRepository.findByPuntoId(puntoId);
    }

    @Transactional(readOnly = true)
    public Capsula obtenerPorId(Long id) {
        return capsulaRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cápsula no encontrada"
                        )
                );
    }

    // Validación para el escáner de QR.
    @Transactional(readOnly = true)
    public Capsula validarQr(String codigoQr) {

        if (codigoQr == null || codigoQr.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El código QR es obligatorio"
            );
        }

        Capsula capsula = capsulaRepository
                .findByCodigoQrIgnoreCase(codigoQr.trim())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "El QR no corresponde a una cápsula registrada"
                        )
                );

        if (!"PUBLICADA".equalsIgnoreCase(capsula.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cápsula no está publicada"
            );
        }

        if (capsula.getContenidoId() != null && contenidoRepository != null && puntoRepository != null) {
            try {
                ContenidoEducativo contenido = validarRelaciones(capsula);
                if (contenido != null && !"PUBLICADA".equalsIgnoreCase(contenido.getEstado())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "El contenido no está publicado"
                    );
                }
            } catch (Exception e) {
                if (e instanceof ResponseStatusException) throw e;
            }
        }

        return capsula;
    }

    // ADMINISTRADOR: generar el QR de una cápsula con video.
    @Transactional
    public Capsula guardar(
            Capsula solicitud,
            Long usuarioId,
            boolean administrador
    ) {

        exigirAdministrador(administrador);

        ContenidoEducativo contenido = contenidoRepository
                .findById(solicitud.getContenidoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El contenido educativo no existe"
                        )
                );

        if (contenido.getPuntoId() == null) {
            throw new IllegalArgumentException(
                    "Las cápsulas sin punto turístico no llevan QR"
            );
        }

        if (!contenido.getPuntoId().equals(
                solicitud.getPuntoId())) {
            throw new IllegalArgumentException(
                    "El contenido pertenece a otro punto turístico"
            );
        }

        if (!puntoRepository.existsById(
                contenido.getPuntoId())) {
            throw new IllegalArgumentException(
                    "El punto turístico no existe"
            );
        }

        VideoRegistrado video = videoRepository
                .findById(solicitud.getVideoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El video no está registrado"
                        )
                );

        if (!contenido.getId().equals(video.getContenidoId())) {
            throw new IllegalArgumentException(
                    "El video no corresponde al contenido"
            );
        }

        if (video.getUrlVideo() == null
                || video.getUrlVideo().isBlank()) {
            throw new IllegalArgumentException(
                    "Primero debes asignar un video"
            );
        }

        Capsula capsula = Capsula.builder()
                .puntoId(contenido.getPuntoId())
                .contenidoId(contenido.getId())
                .videoId(video.getId())
                .titulo(contenido.getTitulo())
                .descripcion(contenido.getDescripcion())
                .categoria(contenido.getCategoria())
                .urlVideo(video.getUrlVideo())
                .estado("BORRADOR")
                .codigoQr(
                        "TEMP-" + UUID.randomUUID()
                )
                .build();

        capsula = capsulaRepository.saveAndFlush(capsula);

        // Conservar el formato utilizado por Android.
        capsula.setCodigoQr(
                "TRISKELEDU-CONTENIDO-"
                        + contenido.getId()
                        + "-"
                        + UUID.randomUUID()
        );

        return capsulaRepository.saveAndFlush(capsula);
    }

    // ADMINISTRADOR: publicar QR.
    @Transactional
    public Capsula publicar(
            Long id,
            Long usuarioId,
            boolean administrador
    ) {

        exigirAdministrador(administrador);

        Capsula capsula = obtenerPorId(id);
        ContenidoEducativo contenido =
                validarRelaciones(capsula);

        VideoRegistrado video =
                videoRepository.findById(capsula.getVideoId())
                        .orElseThrow();

        if (video.getUrlVideo() == null
                || video.getUrlVideo().isBlank()) {
            throw new IllegalArgumentException(
                    "Primero debes asignar un video"
            );
        }

        capsula.setUrlVideo(video.getUrlVideo());
        capsula.setEstado("PUBLICADA");
        contenido.setEstado("PUBLICADA");

        contenidoRepository.save(contenido);

        return capsulaRepository.saveAndFlush(capsula);
    }

    // ADMINISTRADOR: despublicar QR.
    @Transactional
    public Capsula despublicar(
            Long id,
            Long usuarioId,
            boolean administrador
    ) {

        exigirAdministrador(administrador);

        Capsula capsula = obtenerPorId(id);
        ContenidoEducativo contenido =
                validarRelaciones(capsula);

        capsula.setEstado("BORRADOR");
        capsulaRepository.saveAndFlush(capsula);

        boolean tienePublicadas = capsulaRepository
                .findByContenidoId(contenido.getId())
                .stream()
                .anyMatch(c ->
                        "PUBLICADA".equals(c.getEstado())
                );

        if (!tienePublicadas) {
            contenido.setEstado("BORRADOR");
            contenidoRepository.save(contenido);
        }

        return capsula;
    }

    private ContenidoEducativo validarRelaciones(
            Capsula capsula
    ) {

        if (capsula.getPuntoId() == null
                || !puntoRepository.existsById(
                capsula.getPuntoId())) {
            throw new IllegalArgumentException(
                    "El punto turístico no existe"
            );
        }

        ContenidoEducativo contenido = contenidoRepository
                .findById(capsula.getContenidoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Contenido inexistente"
                        )
                );

        VideoRegistrado video = videoRepository
                .findById(capsula.getVideoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Video inexistente"
                        )
                );

        if (!capsula.getPuntoId().equals(
                contenido.getPuntoId())
                || !contenido.getId().equals(
                video.getContenidoId())) {

            throw new IllegalArgumentException(
                    "Las relaciones de la cápsula no coinciden"
            );
        }

        return contenido;
    }

    private void exigirAdministrador(boolean administrador) {
        if (!administrador) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el administrador gestiona los QR"
            );
        }
    }
}