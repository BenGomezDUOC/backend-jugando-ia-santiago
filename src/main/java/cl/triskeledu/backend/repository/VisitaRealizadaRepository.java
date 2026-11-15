package cl.triskeledu.backend.repository;

import cl.triskeledu.backend.entity.VisitaRealizada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitaRealizadaRepository extends JpaRepository<VisitaRealizada, Long> {

    List<VisitaRealizada> findByUsuarioId(Long usuarioId);

    Optional<VisitaRealizada> findByUsuarioIdAndPuntoId(Long usuarioId, Long puntoId);

    boolean existsByUsuarioIdAndPuntoId(Long usuarioId, Long puntoId);
}
