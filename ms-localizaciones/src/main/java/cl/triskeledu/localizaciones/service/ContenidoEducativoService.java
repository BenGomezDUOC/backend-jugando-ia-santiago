package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.ContenidoEducativo;
import cl.triskeledu.localizaciones.entity.VideoRegistrado;

import cl.triskeledu.localizaciones.repository.ContenidoEducativoRepository;
import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
import cl.triskeledu.localizaciones.repository.VideoRegistradoRepository;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ContenidoEducativoService {

    private final ContenidoEducativoRepository contenidoRepository;
    private final PuntoInteresRepository puntoRepository;
    private final VideoRegistradoRepository videoRepository;
    private final CapsulaRepository capsulaRepository;

    @Transactional(readOnly = true)
    public List<ContenidoEducativo> obtenerTodos(
            Long puntoId,
            boolean soloPublicados
    ) {
        if (soloPublicados) {
            return puntoId == null
                    ? contenidoRepository.findByEstado("PUBLICADA")
                    : contenidoRepository.findByPuntoIdAndEstado(
                    puntoId, "PUBLICADA"
            );
        }

        return puntoId == null
                ? contenidoRepository.findAll()
                : contenidoRepository.findByPuntoId(puntoId);
    }

    @Transactional(readOnly = true)
    public ContenidoEducativo obtenerPorId(Long id) {
        return contenidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Contenido educativo no encontrado"
                        )
                );
    }

    // GENERADOR: crear contenido con o sin punto turístico.
    @Transactional
    public ContenidoEducativo crear(
            ContenidoEducativo contenido,
            Long generadorId
    ) {
        validarPuntoOpcional(contenido.getPuntoId());

        contenido.setId(null);
        contenido.setGeneradorId(generadorId);
        contenido.setEstado("BORRADOR");
        contenido.setCodigoQr(null);
        contenido.setTitulo(contenido.getTitulo().trim());

        return contenidoRepository.saveAndFlush(contenido);
    }

    // GENERADOR: editar título, descripción y categoría, incluso con QR asociado.
    // Un contenido con QR no puede cambiar de punto turístico.
    @Transactional
    public ContenidoEducativo actualizar(
            Long id,
            ContenidoEducativo datos,
            Long usuarioId,
            boolean administrador
    ) {
        ContenidoEducativo contenido = obtenerPorId(id);

        comprobarPropietario(
                contenido, usuarioId, administrador
        );

        validarPuntoOpcional(datos.getPuntoId());

        // Si ya tiene cápsula/QR, impedir cambiar el punto turístico.
        // Sí permitir actualizar título, descripción y categoría.
        if (capsulaRepository.existsByContenidoId(id)
                && !Objects.equals(
                contenido.getPuntoId(),
                datos.getPuntoId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede cambiar el punto turístico de un contenido con QR asociado"
            );
        }

        // Un contenido publicado sin QR no puede convertirse
        // directamente en contenido con punto turístico.
        if ("PUBLICADA".equals(contenido.getEstado())
                && contenido.getPuntoId() == null
                && datos.getPuntoId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Despublica primero el contenido sin QR"
            );
        }

        contenido.setTitulo(datos.getTitulo().trim());
        contenido.setDescripcion(datos.getDescripcion());
        contenido.setCategoria(datos.getCategoria());
        contenido.setPuntoId(datos.getPuntoId());

        // No modificar el estado, el QR ni la asociación del video.
        return contenidoRepository.saveAndFlush(contenido);
    }

    // ADMINISTRADOR: publicar contenido sin QR.
    @Transactional
    public ContenidoEducativo publicarSinQr(
            Long id,
            boolean administrador
    ) {
        exigirAdministrador(administrador);

        ContenidoEducativo contenido = obtenerPorId(id);

        if (contenido.getPuntoId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este contenido está asociado a un punto turístico"
            );
        }

        if (capsulaRepository.existsByContenidoId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este contenido tiene un QR asociado"
            );
        }

        List<VideoRegistrado> videos =
                videoRepository.findByContenidoId(id);

        boolean tieneVideo = videos.stream()
                .anyMatch(video ->
                        video.getUrlVideo() != null
                                && !video.getUrlVideo().isBlank()
                );

        if (!tieneVideo) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Primero el administrador debe asignar un video"
            );
        }

        contenido.setEstado("PUBLICADA");

        return contenidoRepository.saveAndFlush(contenido);
    }

    // ADMINISTRADOR: despublicar contenido sin QR.
    @Transactional
    public ContenidoEducativo despublicarSinQr(
            Long id,
            boolean administrador
    ) {
        exigirAdministrador(administrador);

        ContenidoEducativo contenido = obtenerPorId(id);

        if (contenido.getPuntoId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este contenido no corresponde a la modalidad sin QR"
            );
        }

        contenido.setEstado("BORRADOR");

        return contenidoRepository.saveAndFlush(contenido);
    }

    // GENERADOR: eliminar contenido sin dependencias.
    @Transactional
    public void eliminar(
            Long id,
            Long usuarioId,
            boolean administrador
    ) {
        ContenidoEducativo contenido = obtenerPorId(id);

        comprobarPropietario(
                contenido, usuarioId, administrador
        );

        if (videoRepository.existsByContenidoId(id)
                || capsulaRepository.existsByContenidoId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El contenido tiene videos o cápsulas asociados"
            );
        }

        contenidoRepository.delete(contenido);
    }

    public void comprobarPropietario(
            ContenidoEducativo contenido,
            Long usuarioId,
            boolean administrador
    ) {
        if (!administrador
                && !Objects.equals(
                usuarioId,
                contenido.getGeneradorId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No puedes modificar contenido de otro generador"
            );
        }
    }

    private void validarPuntoOpcional(Long puntoId) {
        if (puntoId == null) {
            return;
        }

        if (puntoId <= 0
                || !puntoRepository.existsById(puntoId)) {
            throw new IllegalArgumentException(
                    "El punto turístico seleccionado no existe"
            );
        }
    }

    private void exigirAdministrador(boolean administrador) {
        if (!administrador) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el administrador puede publicar contenidos"
            );
        }
    }
}
