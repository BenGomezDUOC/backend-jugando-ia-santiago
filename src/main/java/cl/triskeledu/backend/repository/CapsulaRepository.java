package cl.triskeledu.backend.repository;

import cl.triskeledu.backend.entity.Capsula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapsulaRepository extends JpaRepository<Capsula, Long> {

    Optional<Capsula> findByCodigoQr(String codigoQr);

    List<Capsula> findByPuntoId(Long puntoId);

    List<Capsula> findByEstado(String estado);
}
