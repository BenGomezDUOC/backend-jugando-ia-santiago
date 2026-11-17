package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.PuntoInteres;
import cl.triskeledu.localizaciones.repository.PuntoInteresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PuntoInteresService {

    private final PuntoInteresRepository puntoInteresRepository;

    public List<PuntoInteres> obtenerTodos() {
        return puntoInteresRepository.findAllByOrderByNombreAsc();
    }

    public PuntoInteres obtenerPorId(Long id) {
        return puntoInteresRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Punto de interés no encontrado con ID: " + id));
    }

    @Transactional
    public PuntoInteres guardar(PuntoInteres punto) {
        return puntoInteresRepository.save(punto);
    }

    @Transactional
    public void eliminar(Long id) {
        puntoInteresRepository.deleteById(id);
    }
}
