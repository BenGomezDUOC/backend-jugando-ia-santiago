package cl.triskeledu.premios.repository;

import cl.triskeledu.premios.entity.Premio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PremioRepository extends JpaRepository<Premio, Long> {
    List<Premio> findByActivoTrueOrderByPuntosRequeridosAsc();
}
