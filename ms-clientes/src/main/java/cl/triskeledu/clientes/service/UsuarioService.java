package cl.triskeledu.clientes.service;

import cl.triskeledu.clientes.dto.LoginRequest;
import cl.triskeledu.clientes.dto.LoginResponse;
import cl.triskeledu.clientes.dto.PerfilUpdateRequest;
import cl.triskeledu.clientes.dto.RegistroRequest;
import cl.triskeledu.clientes.dto.UsuarioResponse;
import cl.triskeledu.clientes.entity.Usuario;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.repository.UsuarioRepository;
import cl.triskeledu.clientes.security.PasswordUtils;
import cl.triskeledu.clientes.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    // --------------------------------------------------
    // REGISTRO DE CLIENTES
    // --------------------------------------------------

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {

        // El registro público solo permite crear clientes.
        if (request.getRol() != Rol.CLIENTE) {

            throw new IllegalArgumentException(
                    "Solo se permite el registro de clientes"
            );
        }

        String nombre = request.getNombreFinal();
        String alias = request.getAliasFinal();

        String correoLimpio = normalizarCorreo(
                request.getCorreo()
        );

        // Validar nombre.
        if (nombre.length() < 3) {

            throw new IllegalArgumentException(
                    "El nombre debe tener al menos 3 caracteres"
            );
        }

        // Validar alias.
        if (alias.length() < 3) {

            throw new IllegalArgumentException(
                    "El alias debe tener al menos 3 caracteres"
            );
        }

        // Comprobar que el correo no esté registrado.
        if (usuarioRepository.existsByCorreo(correoLimpio)) {

            throw new IllegalArgumentException(
                    "Este correo electrónico ya está registrado"
            );
        }

        // Validar contraseña.
        validarContrasenaNueva(request.getContrasena());

        // Validar teléfono (opcional).
        String telefono = limpiarTexto(
                request.getTelefono()
        );

        if (!telefono.isEmpty() && !telefono.matches("^[0-9]{8,15}$")) {

            throw new IllegalArgumentException(
                    "El teléfono debe contener entre 8 y 15 dígitos"
            );
        }

        // Validar avatar.
        Integer avatar = request.getAvatar();

        validarAvatar(avatar);

        // Crear una cuenta exclusivamente con el rol CLIENTE.
        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .alias(alias)
                .correo(correoLimpio)
                .telefono(telefono)
                .direccion(
                        limpiarTexto(request.getDireccion())
                )
                .avatar(avatar)
                .contrasena(
                        PasswordUtils.crearHash(
                                request.getContrasena()
                        )
                )
                .rol(Rol.CLIENTE)
                .puntosAcumulados(0)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        return toResponse(guardado);
    }

    // --------------------------------------------------
    // INICIO DE SESIÓN CON JWT
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String correoLimpio = normalizarCorreo(
                request.getCorreo()
        );

        // Buscar al usuario por correo.
        Usuario usuario = usuarioRepository
                .findByCorreoIgnoreCase(correoLimpio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Credenciales incorrectas"
                ));

        // Verificar la contraseña ingresada.
        boolean contrasenaValida =
                PasswordUtils.verificarContrasena(
                        request.getContrasena(),
                        usuario.getContrasena()
                );

        if (!contrasenaValida) {

            throw new IllegalArgumentException(
                    "Credenciales incorrectas"
            );
        }

        // Generar el token únicamente después de autenticar.
        String token = jwtService.generarToken(usuario);

        // Devolver el token y los datos reales del usuario.
        return LoginResponse.builder()
                .token(token)
                .tipoToken("Bearer")
                .expiraEn(jwtService.obtenerDuracionToken())
                .usuario(toResponse(usuario))
                .build();
    }

    // --------------------------------------------------
    // OBTENER USUARIO POR ID
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado con ID: " + id
                ));

        return toResponse(usuario);
    }

    // --------------------------------------------------
    // OBTENER USUARIOS POR ROL
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerPorRol(Rol rol) {

        return usuarioRepository
                .findByRolOrderByNombreAsc(rol)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --------------------------------------------------
    // OBTENER TODOS LOS USUARIOS
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --------------------------------------------------
    // ACTUALIZAR PERFIL
    // --------------------------------------------------

    @Transactional
    public UsuarioResponse actualizarPerfil(
            Long id,
            PerfilUpdateRequest request
    ) {

        // Bloquear el registro durante la actualización.
        Usuario usuario = usuarioRepository
                .findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado"
                ));

        String nombre = limpiarTexto(
                request.getNombre()
        );

        String alias = limpiarTexto(
                request.getAlias()
        );

        String correoLimpio = normalizarCorreo(
                request.getCorreo()
        );

        // Validar nombre.
        if (nombre.length() < 3) {

            throw new IllegalArgumentException(
                    "El nombre debe tener al menos 3 caracteres"
            );
        }

        // Validar alias.
        if (alias.length() < 3) {

            throw new IllegalArgumentException(
                    "El alias debe tener al menos 3 caracteres"
            );
        }

        // Comprobar que el correo no pertenezca a otra cuenta.
        if (usuarioRepository.existsByCorreoAndIdNot(
                correoLimpio,
                id
        )) {

            throw new IllegalArgumentException(
                    "El correo ya está en uso por otra cuenta"
            );
        }

        // Actualizar datos personales.
        usuario.setNombre(nombre);
        usuario.setAlias(alias);
        usuario.setCorreo(correoLimpio);

        // Actualizar teléfono si fue proporcionado.
        if (request.getTelefono() != null) {

            String telefono = limpiarTexto(
                    request.getTelefono()
            );

            // El teléfono es obligatorio para los clientes.
            if (usuario.getRol() == Rol.CLIENTE
                    && telefono.isEmpty()) {

                throw new IllegalArgumentException(
                        "El teléfono es obligatorio"
                );
            }

            // Validar formato del teléfono.
            if (!telefono.isEmpty()
                    && !telefono.matches("^[0-9]{8,15}$")) {

                throw new IllegalArgumentException(
                        "El teléfono debe contener entre 8 y 15 dígitos"
                );
            }

            usuario.setTelefono(telefono);
        }

        // Actualizar dirección.
        if (request.getDireccion() != null) {

            usuario.setDireccion(
                    limpiarTexto(request.getDireccion())
            );
        }

        // Actualizar avatar.
        if (request.getAvatar() != null) {

            validarAvatar(request.getAvatar());

            usuario.setAvatar(request.getAvatar());
        }

        // Cambiar contraseña únicamente si se solicita.
        String contrasenaNueva =
                request.getContrasenaNueva();

        if (contrasenaNueva != null
                && !contrasenaNueva.isEmpty()) {

            String contrasenaActual =
                    request.getContrasenaActual();

            // Comprobar que ingresó su contraseña actual.
            if (contrasenaActual == null
                    || contrasenaActual.isBlank()) {

                throw new IllegalArgumentException(
                        "Debes ingresar tu contraseña actual"
                );
            }

            // Verificar la contraseña actual.
            boolean contrasenaCorrecta =
                    PasswordUtils.verificarContrasena(
                            contrasenaActual,
                            usuario.getContrasena()
                    );

            if (!contrasenaCorrecta) {

                throw new IllegalArgumentException(
                        "La contraseña actual es incorrecta"
                );
            }

            // Validar la nueva contraseña.
            validarContrasenaNueva(contrasenaNueva);

            // Guardar la contraseña protegida mediante hash.
            usuario.setContrasena(
                    PasswordUtils.crearHash(contrasenaNueva)
            );
        }

        // Conserva el ID, rol, puntos y tipo de cuenta.
        Usuario actualizado = usuarioRepository.save(usuario);

        return toResponse(actualizado);
    }

    // --------------------------------------------------
    // CONVERTIR ENTIDAD EN DTO DE RESPUESTA
    // --------------------------------------------------

    public UsuarioResponse toResponse(Usuario usuario) {

        String fecha = usuario.getFechaRegistro() != null
                ? usuario.getFechaRegistro().format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
                : null;

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .alias(usuario.getAlias())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .avatar(usuario.getAvatar())
                .rol(usuario.getRol())
                .puntosAcumulados(
                        usuario.getPuntosAcumulados()
                )
                .fechaRegistro(fecha)
                .tipoCuentaSistema(
                        usuario.getTipoCuentaSistema()
                )
                .build();
    }

    // --------------------------------------------------
    // MÉTODOS PRIVADOS DE VALIDACIÓN
    // --------------------------------------------------

    // Elimina espacios innecesarios.
    private String limpiarTexto(String texto) {

        return texto != null ? texto.trim() : "";
    }

    // Normaliza el correo electrónico.
    private String normalizarCorreo(String correo) {

        if (correo == null || correo.isBlank()) {

            throw new IllegalArgumentException(
                    "El correo es obligatorio"
            );
        }

        return correo.trim().toLowerCase(Locale.ROOT);
    }

    // Comprueba que el avatar exista.
    private void validarAvatar(Integer avatar) {

        if (avatar == null || avatar < 1 || avatar > 3) {

            throw new IllegalArgumentException(
                    "El avatar seleccionado no es válido"
            );
        }
    }

    // Comprueba la seguridad mínima de la contraseña.
    private void validarContrasenaNueva(String contrasena) {

        if (contrasena == null
                || contrasena.isBlank()
                || contrasena.length() < 3) {

            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 3 caracteres"
            );
        }
    }
}