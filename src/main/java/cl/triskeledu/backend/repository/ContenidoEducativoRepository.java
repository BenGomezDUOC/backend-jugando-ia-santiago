package cl.triskeledu.backend.repository;

import cl.triskeledu.backend.entity.ContenidoEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContenidoEducativoRepository extends JpaRepository<ContenidoEducativo, Long> {

    List<ContenidoEducativo> findByEstado(String estado);

    Optional<ContenidoEducativo> findByCodigoQr(String codigoQr);
}
