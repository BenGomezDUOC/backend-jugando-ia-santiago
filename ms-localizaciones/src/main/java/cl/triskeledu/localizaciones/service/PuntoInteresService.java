package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.PuntoInteres;
import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
import cl.triskeledu.localizaciones.repository.ContenidoEducativoRepository;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PuntoInteresService {

    private final PuntoInteresRepository puntoInteresRepository;
    private final ContenidoEducativoRepository contenidoRepository;
    private final CapsulaRepository capsulaRepository;
    private final MultimediaService multimediaService;

    @Transactional(readOnly = true)
    public List<PuntoInteres> obtenerTodos() {

        return puntoInteresRepository
                .findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public PuntoInteres obtenerPorId(Long id) {

        return puntoInteresRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Punto turístico no encontrado"
                        )
                );
    }

    @Transactional
    public PuntoInteres guardar(PuntoInteres punto) {

        validar(punto);

        // El identificador y código los genera el servidor.
        punto.setId(null);

        punto.setCodigo(
                "POI-" + UUID.randomUUID()
        );

        punto.setCompletado(false);
        punto.setPuntosRecompensa(100);
        punto.setQrEsperado("");

        return puntoInteresRepository.saveAndFlush(punto);
    }

    @Transactional
    public PuntoInteres actualizar(
            Long id,
            PuntoInteres datos
    ) {

        validar(datos);

        PuntoInteres punto = obtenerPorId(id);

        punto.setNombre(datos.getNombre().trim());
        punto.setDescripcion(datos.getDescripcion());
        punto.setDireccion(datos.getDireccion().trim());
        punto.setLatitud(datos.getLatitud());
        punto.setLongitud(datos.getLongitud());
        punto.setAltitud(datos.getAltitud());
        punto.setImagenUri(datos.getImagenUri());

        punto.setRadioToleranciaMetros(
                datos.getRadioToleranciaMetros()
        );

        return puntoInteresRepository.saveAndFlush(punto);
    }

    @Transactional
    public void eliminar(Long id) {

        PuntoInteres punto = obtenerPorId(id);

        if (contenidoRepository.existsByPuntoId(id)
                || capsulaRepository.existsByPuntoId(id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El punto turístico tiene contenidos o cápsulas asociados"
            );
        }

        puntoInteresRepository.delete(punto);
    }

    private void validar(PuntoInteres punto) {

        if (punto.getNombre() == null
                || punto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio"
            );
        }

        if (punto.getDireccion() == null
                || punto.getDireccion().isBlank()) {

            throw new IllegalArgumentException(
                    "La dirección es obligatoria"
            );
        }

        if (punto.getLatitud() == null
                || !Double.isFinite(punto.getLatitud())
                || punto.getLatitud() < -90
                || punto.getLatitud() > 90) {

            throw new IllegalArgumentException(
                    "Latitud no válida"
            );
        }

        if (punto.getLongitud() == null
                || !Double.isFinite(punto.getLongitud())
                || punto.getLongitud() < -180
                || punto.getLongitud() > 180) {

            throw new IllegalArgumentException(
                    "Longitud no válida"
            );
        }

        if (punto.getAltitud() == null
                || !Double.isFinite(punto.getAltitud())) {

            throw new IllegalArgumentException(
                    "Altitud no válida"
            );
        }

        if (punto.getRadioToleranciaMetros() == null
                || !Double.isFinite(
                punto.getRadioToleranciaMetros())
                || punto.getRadioToleranciaMetros() <= 0) {

            throw new IllegalArgumentException(
                    "Radio de tolerancia no válido"
            );
        }

        punto.setNombre(punto.getNombre().trim());
        punto.setDireccion(punto.getDireccion().trim());

        if (punto.getImagenUri() != null
                && !punto.getImagenUri().isBlank()) {

            multimediaService.validarReferencia(
                    punto.getImagenUri().trim(),
                    "imagen",
                    null,
                    true
            );

            punto.setImagenUri(
                    punto.getImagenUri().trim()
            );
        }
    }
}