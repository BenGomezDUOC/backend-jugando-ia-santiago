package cl.triskeledu.localizaciones.service;

import cl.triskeledu.localizaciones.entity.Capsula;
import cl.triskeledu.localizaciones.repository.CapsulaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapsulaService {

    private final CapsulaRepository capsulaRepository;

    public List<Capsula> obtenerTodas() {
        return capsulaRepository.findAll();
    }

    public List<Capsula> obtenerPublicadas() {
        return capsulaRepository.findByEstado("PUBLICADA");
    }

    public List<Capsula> obtenerPorPunto(Long puntoId) {
        return capsulaRepository.findByPuntoId(puntoId);
    }

    public Capsula validarQr(String codigoQr) {
        Capsula capsula = capsulaRepository.findByCodigoQr(codigoQr.trim())
                .orElseThrow(() -> new IllegalArgumentException("El código QR no corresponde a ninguna cápsula de Triskeledu"));

        if (!"PUBLICADA".equalsIgnoreCase(capsula.getEstado())) {
            throw new IllegalArgumentException("La cápsula educativa no se encuentra publicada");
        }

        return capsula;
    }

    @Transactional
    public Capsula guardar(Capsula capsula) {
        return capsulaRepository.save(capsula);
    }
}
