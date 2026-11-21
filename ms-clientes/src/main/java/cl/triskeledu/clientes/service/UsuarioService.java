package cl.triskeledu.clientes.service;

import cl.triskeledu.clientes.dto.LoginRequest;
import cl.triskeledu.clientes.dto.PerfilUpdateRequest;
import cl.triskeledu.clientes.dto.RegistroRequest;
import cl.triskeledu.clientes.dto.UsuarioResponse;
import cl.triskeledu.clientes.entity.Usuario;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.repository.UsuarioRepository;
import cl.triskeledu.clientes.security.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        String correoLimpio = request.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoLimpio)) {
            throw new IllegalArgumentException("Este correo electrónico ya está registrado");
        }

        String aliasFinal = request.getAliasFinal();
        String institucionFinal = request.getInstitucion() != null && !request.getInstitucion().isBlank()
                ? request.getInstitucion().trim() : "Triskeledu";
        String avatarSeleccionadoFinal = request.getAvatarSeleccionado() != null && !request.getAvatarSeleccionado().isBlank()
                ? request.getAvatarSeleccionado().trim() : "avatar_explorador_1";

        Rol rolFinal = request.getRol() != null ? request.getRol() : Rol.CLIENTE;

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombreFinal())
                .alias(aliasFinal)
                .correo(correoLimpio)
                .telefono(request.getTelefono() != null ? request.getTelefono().trim() : "")
                .direccion(request.getDireccion() != null ? request.getDireccion().trim() : "")
                .institucion(institucionFinal)
                .avatar(request.getAvatar() != null ? request.getAvatar() : 1)
                .avatarSeleccionado(avatarSeleccionadoFinal)
                .contrasena(PasswordUtils.crearHash(request.getContrasena()))
                .rol(rolFinal)
                .puntosAcumulados(0)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return toResponse(guardado);
    }

    public UsuarioResponse login(LoginRequest request) {
        String correoLimpio = request.getCorreo().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByCorreo(correoLimpio)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));

        boolean valida = PasswordUtils.verificarContrasena(request.getContrasena(), usuario.getContrasena());
        if (!valida) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        return toResponse(usuario);
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        return toResponse(usuario);
    }

    public List<UsuarioResponse> obtenerPorRol(Rol rol) {
        return usuarioRepository.findByRolOrderByNombreAsc(rol).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(Long id, PerfilUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String correoLimpio = request.getCorreo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreoAndIdNot(correoLimpio, id)) {
            throw new IllegalArgumentException("El correo ya está en uso por otra cuenta");
        }

        usuario.setNombre(request.getNombre().trim());
        usuario.setAlias(request.getAlias().trim());
        usuario.setCorreo(correoLimpio);
        if (request.getTelefono() != null) usuario.setTelefono(request.getTelefono().trim());
        if (request.getDireccion() != null) usuario.setDireccion(request.getDireccion().trim());
        if (request.getAvatar() != null) usuario.setAvatar(request.getAvatar());

        if (request.getContrasenaNueva() != null && !request.getContrasenaNueva().isBlank()) {
            if (request.getContrasenaActual() == null || request.getContrasenaActual().isBlank()) {
                throw new IllegalArgumentException("Debe ingresar su contraseña actual para cambiarla");
            }
            if (!PasswordUtils.verificarContrasena(request.getContrasenaActual(), usuario.getContrasena())) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }
            if (request.getContrasenaNueva().length() < 3) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 3 caracteres");
            }
            usuario.setContrasena(PasswordUtils.crearHash(request.getContrasenaNueva()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return toResponse(actualizado);
    }

    @Transactional
    public UsuarioResponse sumarPuntos(Long usuarioId, int puntos) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        int anterior = usuario.getPuntosAcumulados() != null ? usuario.getPuntosAcumulados() : 0;
        usuario.setPuntosAcumulados(anterior + puntos);
        Usuario actualizado = usuarioRepository.save(usuario);
        return toResponse(actualizado);
    }

    public UsuarioResponse toResponse(Usuario u) {
        String fecha = u.getFechaRegistro() != null
                ? u.getFechaRegistro().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .alias(u.getAlias())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .direccion(u.getDireccion())
                .institucion(u.getInstitucion() != null ? u.getInstitucion() : "Triskeledu")
                .avatar(u.getAvatar())
                .avatarSeleccionado(u.getAvatarSeleccionado() != null ? u.getAvatarSeleccionado() : "avatar_explorador_1")
                .rol(u.getRol())
                .puntosAcumulados(u.getPuntosAcumulados())
                .fechaRegistro(fecha)
                .tipoCuentaSistema(u.getTipoCuentaSistema())
                .build();
    }
}
