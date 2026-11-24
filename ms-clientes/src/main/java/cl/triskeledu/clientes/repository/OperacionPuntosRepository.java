package cl.triskeledu.clientes.repository;

import cl.triskeledu.clientes.entity.OperacionPuntos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperacionPuntosRepository
        extends JpaRepository<OperacionPuntos, Long> {

    // Buscar una operación mediante su código único.
    Optional<OperacionPuntos> findByCodigoOperacion(
            String codigoOperacion
    );
}
