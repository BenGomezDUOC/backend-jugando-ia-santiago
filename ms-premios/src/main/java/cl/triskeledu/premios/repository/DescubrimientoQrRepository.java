package cl.triskeledu.premios.repository;

import cl.triskeledu.premios.entity.DescubrimientoQr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescubrimientoQrRepository extends JpaRepository<DescubrimientoQr, Long> {
    List<DescubrimientoQr> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndCapsulaId(Long usuarioId, Long capsulaId);
}
