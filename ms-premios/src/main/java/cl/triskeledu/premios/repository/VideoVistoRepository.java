package cl.triskeledu.premios.repository;

import cl.triskeledu.premios.entity.VideoVisto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoVistoRepository extends JpaRepository<VideoVisto, Long> {
    List<VideoVisto> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndCapsulaId(Long usuarioId, Long capsulaId);
}
