package cl.triskeledu.backend.service;

import cl.triskeledu.backend.dto.LoginRequest;
import cl.triskeledu.backend.dto.PerfilUpdateRequest;
import cl.triskeledu.backend.dto.RegistroRequest;
import cl.triskeledu.backend.dto.UsuarioResponse;
import cl.triskeledu.backend.entity.Usuario;
import cl.triskeledu.backend.model.Rol;
import cl.triskeledu.backend.repository.UsuarioRepository;
import cl.triskeledu.backend.security.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre().trim())
                .alias(request.getAlias().trim())
                .correo(correoLimpio)
                .telefono(request.getTelefono() != null ? request.getTelefono().trim() : "")
                .direccion(request.getDireccion() != null ? request.getDireccion().trim() : "")
                .avatar(request.getAvatar() != null ? request.getAvatar() : 1)
                .contrasena(PasswordUtils.crearHash(request.getContrasena()))
                .rol(request.getRol() != null ? request.getRol() : Rol.CLIENTE)
                .puntosAcumulados(0)
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
            if (request.getContrasenaNueva().length() < 6) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
            }
            usuario.setContrasena(PasswordUtils.crearHash(request.getContrasenaNueva()));
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return toResponse(actualizado);
    }

    @Transactional
    public void sumarPuntos(Long usuarioId, int puntos) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + puntos);
        usuarioRepository.save(usuario);
    }

    public UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .alias(u.getAlias())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .direccion(u.getDireccion())
                .avatar(u.getAvatar())
                .rol(u.getRol())
                .puntosAcumulados(u.getPuntosAcumulados())
                .tipoCuentaSistema(u.getTipoCuentaSistema())
                .build();
    }
}
