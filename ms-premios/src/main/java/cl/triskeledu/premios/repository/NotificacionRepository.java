package cl.triskeledu.premios.repository;

import cl.triskeledu.premios.entity.Notificacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacionRepository
        extends JpaRepository<Notificacion, Long> {

    // Consultar las notificaciones de un usuario.
    List<Notificacion> findByUsuarioIdOrderByFechaTimestampDesc(
            Long usuarioId
    );

    // Contar notificaciones pendientes.
    long countByUsuarioIdAndLeidaFalse(
            Long usuarioId
    );

    // Buscar una notificación comprobando su propietario.
    Optional<Notificacion> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    // Marcar como leídas únicamente las notificaciones
    // pendientes del usuario autenticado.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Notificacion n
            SET n.leida = true
            WHERE n.usuarioId = :usuarioId
            AND n.leida = false
            """)
    int marcarTodasComoLeidas(
            @Param("usuarioId") Long usuarioId
    );
}