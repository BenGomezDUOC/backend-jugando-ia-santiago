package cl.triskeledu.backend.repository;

import cl.triskeledu.backend.entity.VideoAdicionalVisto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoAdicionalVistoRepository extends JpaRepository<VideoAdicionalVisto, Long> {

    List<VideoAdicionalVisto> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndVideoAdicionalId(Long usuarioId, Integer videoAdicionalId);
}
