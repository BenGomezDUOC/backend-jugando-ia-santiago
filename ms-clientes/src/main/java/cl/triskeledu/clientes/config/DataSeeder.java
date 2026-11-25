package cl.triskeledu.clientes.config;

import cl.triskeledu.clientes.entity.Usuario;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.repository.UsuarioRepository;
import cl.triskeledu.clientes.security.PasswordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    // Logger para registrar información en la consola.
    private static final Logger log =
            LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioRepository usuarioRepository;

    // Constructor.
    public DataSeeder(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Contraseñas iniciales de las cuentas del sistema.
    @Value("${app.seed.admin-password:}")
    private String adminPassword;

    @Value("${app.seed.generador-password:}")
    private String generadorPassword;

    @Override
    public void run(String... args) {

        seedUsuarios();
    }

    // --------------------------------------------------
    // CUENTAS PREDEFINIDAS DEL SISTEMA
    // --------------------------------------------------

    private void seedUsuarios() {

        // Cuenta de administrador.
        crearCuentaSistema(
                "Administrador",
                "Admin",
                "admin@triskeledu.cl",
                adminPassword,
                Rol.ADMINISTRADOR,
                "ADMIN_PRINCIPAL",
                1
        );

        // Cuenta del generador de contenido.
        crearCuentaSistema(
                "Contenido",
                "GenCont",
                "contenido@triskeledu.cl",
                generadorPassword,
                Rol.GENERADOR,
                "GENERADOR_PRINCIPAL",
                2
        );
    }

    // --------------------------------------------------
    // CREACIÓN DE CUENTAS PREDEFINIDAS
    // --------------------------------------------------

    private void crearCuentaSistema(
            String nombre,
            String alias,
            String correo,
            String contrasena,
            Rol rol,
            String tipoCuentaSistema,
            Integer avatar
    ) {

        // Si la cuenta existe, conserva todos sus datos.
        if (usuarioRepository
                .findByTipoCuentaSistema(tipoCuentaSistema)
                .isPresent()) {

            log.info(
                    "La cuenta {} ya existe. Se conservan sus datos.",
                    tipoCuentaSistema
            );

            return;
        }

        // Evita duplicar cuentas por correo electrónico.
        if (usuarioRepository.existsByCorreo(correo)) {

            throw new IllegalStateException(
                    "El correo reservado para una cuenta del sistema "
                            + "ya está registrado: " + correo
            );
        }

        // Validar la contraseña inicial.
        if (contrasena == null
                || contrasena.isBlank()
                || contrasena.length() < 3) {

            throw new IllegalStateException(
                    "Debes configurar una contraseña inicial "
                            + "de al menos 3 caracteres para "
                            + tipoCuentaSistema
            );
        }

        // Crear la cuenta únicamente si no existe.
        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .alias(alias)
                .correo(correo)
                .telefono("")
                .direccion("")
                .contrasena(
                        PasswordUtils.crearHash(contrasena)
                )
                .rol(rol)
                .tipoCuentaSistema(tipoCuentaSistema)
                .avatar(avatar)
                .puntosAcumulados(0)
                .fechaRegistro(LocalDateTime.now())
                .build();

        usuarioRepository.save(usuario);

        log.info(
                "Cuenta {} creada correctamente.",
                tipoCuentaSistema
        );
    }
}