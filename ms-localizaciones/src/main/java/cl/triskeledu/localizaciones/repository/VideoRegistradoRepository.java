package cl.triskeledu.localizaciones.repository;

import cl.triskeledu.localizaciones.entity.VideoRegistrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRegistradoRepository extends JpaRepository<VideoRegistrado, Long> {
    List<VideoRegistrado> findByContenidoId(Long contenidoId);
}
