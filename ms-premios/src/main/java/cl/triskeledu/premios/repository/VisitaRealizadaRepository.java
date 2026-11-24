
package cl.triskeledu.premios.repository;

import cl.triskeledu.premios.entity.VisitaRealizada;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VisitaRealizadaRepository
        extends JpaRepository<VisitaRealizada, Long> {

    Optional<VisitaRealizada> findByUsuarioIdAndCapsulaId(
            Long usuarioId,
            Long capsulaId
    );

    List<VisitaRealizada> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndRutaFotoSelfie(
            Long usuarioId,
            String rutaFotoSelfie
    );
}
