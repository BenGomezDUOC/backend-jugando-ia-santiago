package cl.triskeledu.localizaciones.repository;

import cl.triskeledu.localizaciones.entity.ContenidoEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContenidoEducativoRepository
        extends JpaRepository<ContenidoEducativo, Long> {

    Optional<ContenidoEducativo> findByCodigoQr(String codigoQr);

    List<ContenidoEducativo> findByPuntoId(Long puntoId);

    List<ContenidoEducativo> findByEstado(String estado);

    List<ContenidoEducativo> findByPuntoIdAndEstado(
            Long puntoId,
            String estado
    );

    boolean existsByPuntoId(Long puntoId);
}