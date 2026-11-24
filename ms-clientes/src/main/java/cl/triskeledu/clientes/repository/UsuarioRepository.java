package cl.triskeledu.clientes.repository;

import cl.triskeledu.clientes.entity.Usuario;
import cl.triskeledu.clientes.model.Rol;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    // Busca un usuario por su correo electrónico.
    Optional<Usuario> findByCorreo(String correo);

    // Busca un usuario sin distinguir mayúsculas y minúsculas.
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    // Comprueba si un correo ya está registrado.
    boolean existsByCorreo(String correo);

    // Comprueba si el correo pertenece a otro usuario.
    boolean existsByCorreoAndIdNot(String correo, Long id);

    // Busca una cuenta predefinida del sistema.
    Optional<Usuario> findByTipoCuentaSistema(
            String tipoCuentaSistema
    );

    // Obtiene los usuarios de un determinado rol.
    List<Usuario> findByRolOrderByNombreAsc(Rol rol);

    // Bloquea temporalmente el registro del usuario
    // durante una operación de actualización.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    Optional<Usuario> findByIdForUpdate(
            @Param("id") Long id
    );
}