package cl.triskeledu.localizaciones.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public final class UsuarioAutenticado {

    private UsuarioAutenticado() {
    }

    public static Long obtenerId(Jwt jwt) {

        if (jwt == null || jwt.getSubject() == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        try {

            Long id = Long.valueOf(jwt.getSubject());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException ex) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Identidad no válida"
            );
        }
    }

    public static boolean tieneRol(Jwt jwt, String rol) {

        if (jwt == null) {
            return false;
        }

        List<String> roles =
                jwt.getClaimAsStringList("roles");

        return roles != null && roles.contains(rol);
    }

    public static boolean esAdministrador(Jwt jwt) {

        return tieneRol(jwt, "ADMINISTRADOR");
    }

    public static boolean esCliente(Jwt jwt) {

        return tieneRol(jwt, "CLIENTE");
    }
}
