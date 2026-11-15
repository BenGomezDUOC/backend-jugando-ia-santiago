package cl.triskeledu.backend.repository;

import cl.triskeledu.backend.entity.DescubrimientoQr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescubrimientoQrRepository extends JpaRepository<DescubrimientoQr, Long> {

    List<DescubrimientoQr> findByUsuarioId(Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    Optional<DescubrimientoQr> findByUsuarioIdAndCapsulaId(Long usuarioId, Long capsulaId);

    boolean existsByUsuarioIdAndCapsulaId(Long usuarioId, Long capsulaId);
}
