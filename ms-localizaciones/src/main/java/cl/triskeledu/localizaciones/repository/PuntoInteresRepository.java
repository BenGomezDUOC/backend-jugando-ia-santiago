package cl.triskeledu.localizaciones.repository;

import cl.triskeledu.localizaciones.entity.PuntoInteres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuntoInteresRepository
        extends JpaRepository<PuntoInteres, Long> {

    List<PuntoInteres> findAllByOrderByNombreAsc();

    Optional<PuntoInteres> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByImagenUri(String imagenUri);
}