package cl.triskeledu.premios.repository;

import cl.triskeledu.premios.entity.VisitaRealizada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitaRealizadaRepository extends JpaRepository<VisitaRealizada, Long> {
    List<VisitaRealizada> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndPuntoId(Long usuarioId, Long puntoId);
}
