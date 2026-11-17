package cl.triskeledu.localizaciones.repository;

import cl.triskeledu.localizaciones.entity.PuntoInteres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuntoInteresRepository extends JpaRepository<PuntoInteres, Long> {
    List<PuntoInteres> findAllByOrderByNombreAsc();
}
