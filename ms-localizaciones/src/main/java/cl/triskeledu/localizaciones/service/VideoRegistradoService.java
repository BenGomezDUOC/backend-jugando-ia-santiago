package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.Capsula;
import cl.triskeledu.localizaciones.entity.VideoRegistrado;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import cl.triskeledu.localizaciones.repository.VideoRegistradoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoRegistradoService {

    private final VideoRegistradoRepository videoRepository;
    private final CapsulaRepository capsulaRepository;
    private final ContenidoEducativoService contenidoService;
    private final MultimediaService multimediaService;

    @Transactional(readOnly = true)
    public List<VideoRegistrado> obtenerTodos(
            Long contenidoId,
            boolean soloPublicados
    ) {

        List<VideoRegistrado> videos = contenidoId == null
                ? videoRepository.findAll()
                : videoRepository.findByContenidoId(contenidoId);

        if (!soloPublicados) {
            return videos;
        }

        return videos.stream()
                .filter(video -> "PUBLICADA".equals(
                        contenidoService.obtenerPorId(
                                video.getContenidoId()
                        ).getEstado()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public VideoRegistrado obtenerPorId(Long id) {

        return videoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Video no encontrado"
                        )
                );
    }

    // ADMINISTRADOR: asignar video al contenido.
    @Transactional
    public VideoRegistrado crear(
            VideoRegistrado video,
            Long usuarioId,
            boolean administrador
    ) {

        exigirAdministrador(administrador);

        contenidoService.obtenerPorId(
                video.getContenidoId()
        );

        validarUrl(video.getUrlVideo(), usuarioId);
        validarMiniatura(video.getUrlMiniatura(), usuarioId);

        video.setId(null);
        video.setUrlVideo(video.getUrlVideo().trim());

        return videoRepository.saveAndFlush(video);
    }

    // ADMINISTRADOR: editar o reemplazar un video.
    @Transactional
    public VideoRegistrado actualizar(
            Long id,
            VideoRegistrado datos,
            Long usuarioId,
            boolean administrador
    ) {

        exigirAdministrador(administrador);

        VideoRegistrado video = obtenerPorId(id);

        if (!video.getContenidoId().equals(
                datos.getContenidoId())) {

            throw new IllegalArgumentException(
                    "No se puede cambiar el contenido asociado"
            );
        }

        validarUrl(datos.getUrlVideo(), usuarioId);
        validarMiniatura(datos.getUrlMiniatura(), usuarioId);

        video.setTitulo(datos.getTitulo().trim());
        video.setDescripcion(datos.getDescripcion());
        video.setUrlVideo(datos.getUrlVideo().trim());
        video.setAutor(datos.getAutor());
        video.setDuracion(datos.getDuracion());
        video.setUrlMiniatura(datos.getUrlMiniatura());

        VideoRegistrado actualizado =
                videoRepository.saveAndFlush(video);

        // Sincronizar la referencia del video con sus cápsulas.
        List<Capsula> capsulas =
                capsulaRepository.findByVideoId(id);

        for (Capsula capsula : capsulas) {
            capsula.setUrlVideo(actualizado.getUrlVideo());
        }

        capsulaRepository.saveAll(capsulas);

        return actualizado;
    }

    @Transactional
    public void eliminar(
            Long id,
            Long usuarioId,
            boolean administrador
    ) {

        exigirAdministrador(administrador);

        VideoRegistrado video = obtenerPorId(id);

        if (capsulaRepository.existsByVideoId(id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El video está vinculado a una cápsula"
            );
        }

        videoRepository.delete(video);
    }

    private void exigirAdministrador(boolean administrador) {

        if (!administrador) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el administrador puede gestionar videos"
            );
        }
    }

    private void validarUrl(String url, Long usuarioId) {

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "El video es obligatorio"
            );
        }

        String referencia = url.trim();

        if (multimediaService.esReferenciaLocal(referencia)) {

            multimediaService.validarReferencia(
                    referencia,
                    "video",
                    usuarioId,
                    true
            );
            return;
        }

        URI uri = URI.create(referencia);

        if (!"https".equalsIgnoreCase(uri.getScheme())
                || uri.getHost() == null
                || uri.getUserInfo() != null) {

            throw new IllegalArgumentException(
                    "La referencia del video no es válida"
            );
        }
    }

    private void validarMiniatura(
            String referencia,
            Long usuarioId
    ) {

        if (referencia == null || referencia.isBlank()) {
            return;
        }

        multimediaService.validarReferencia(
                referencia.trim(),
                "imagen",
                usuarioId,
                true
        );
    }
}