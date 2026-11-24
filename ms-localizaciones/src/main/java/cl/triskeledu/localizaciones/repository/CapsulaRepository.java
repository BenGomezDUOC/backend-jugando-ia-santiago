package cl.triskeledu.localizaciones.repository;

import cl.triskeledu.localizaciones.entity.Capsula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapsulaRepository
        extends JpaRepository<Capsula, Long> {

    Optional<Capsula> findByCodigoQr(String codigoQr);

    List<Capsula> findByPuntoId(Long puntoId);

    List<Capsula> findByEstado(String estado);

    List<Capsula> findByPuntoIdAndEstado(
            Long puntoId,
            String estado
    );

    List<Capsula> findByContenidoId(Long contenidoId);

    boolean existsByCodigoQr(String codigoQr);

    boolean existsByPuntoId(Long puntoId);

    boolean existsByContenidoId(Long contenidoId);

    boolean existsByVideoId(Long videoId);

    boolean existsByUrlVideoAndEstado(
            String urlVideo,
            String estado
    );

    boolean existsByVideoIdAndEstado(
            Long videoId,
            String estado
    );

    List<Capsula> findByVideoId(Long videoId);
}