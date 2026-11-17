package cl.triskeledu.clientes.config;

import cl.triskeledu.clientes.entity.Usuario;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.repository.UsuarioRepository;
import cl.triskeledu.clientes.security.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        seedUsuarios();
    }

    private void seedUsuarios() {
        // 1. Administrador (clave 123)
        if (usuarioRepository.findByTipoCuentaSistema("ADMIN_PRINCIPAL").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .nombre("Administrador")
                    .alias("Admin")
                    .correo("admin@triskeledu.cl")
                    .contrasena(PasswordUtils.crearHash("123"))
                    .rol(Rol.ADMINISTRADOR)
                    .tipoCuentaSistema("ADMIN_PRINCIPAL")
                    .puntosAcumulados(0)
                    .avatar(1)
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuario Administrador precargado exitosamente en ms-clientes.");
        } else {
            usuarioRepository.findByTipoCuentaSistema("ADMIN_PRINCIPAL").ifPresent(u -> {
                u.setContrasena(PasswordUtils.crearHash("123"));
                usuarioRepository.save(u);
            });
        }

        // 2. Generador de Contenido (clave 123)
        if (usuarioRepository.findByTipoCuentaSistema("GENERADOR_PRINCIPAL").isEmpty()) {
            Usuario gen = Usuario.builder()
                    .nombre("Contenido")
                    .alias("GenCont")
                    .correo("contenido@triskeledu.cl")
                    .contrasena(PasswordUtils.crearHash("123"))
                    .rol(Rol.GENERADOR)
                    .tipoCuentaSistema("GENERADOR_PRINCIPAL")
                    .puntosAcumulados(0)
                    .avatar(2)
                    .build();
            usuarioRepository.save(gen);
            log.info("Usuario Generador de Contenido precargado exitosamente en ms-clientes.");
        } else {
            usuarioRepository.findByTipoCuentaSistema("GENERADOR_PRINCIPAL").ifPresent(u -> {
                u.setContrasena(PasswordUtils.crearHash("123"));
                usuarioRepository.save(u);
            });
        }

        // 3. Cliente Demo (clave 123)
        if (!usuarioRepository.existsByCorreo("estudiante@triskeledu.cl")) {
            Usuario demo = Usuario.builder()
                    .nombre("Estudiante Demo")
                    .alias("Estudiante")
                    .correo("estudiante@triskeledu.cl")
                    .contrasena(PasswordUtils.crearHash("123"))
                    .rol(Rol.CLIENTE)
                    .puntosAcumulados(110)
                    .avatar(3)
                    .build();
            usuarioRepository.save(demo);
            log.info("Usuario Cliente Demo precargado exitosamente en ms-clientes.");
        }
    }
}
