package cl.triskeledu.clientes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class InternalAuthenticationFilter
        extends OncePerRequestFilter {

    private final byte[] claveInterna;

    // Recibe la clave configurada para ms-premios.
    public InternalAuthenticationFilter(String claveInterna) {

        this.claveInterna = Base64.getDecoder()
                .decode(claveInterna);

        if (this.claveInterna.length < 32) {
            throw new IllegalStateException(
                    "La clave interna debe tener al menos 256 bits"
            );
        }
    }

    // Aplica el filtro únicamente a las rutas internas.
    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        return !request.getServletPath()
                .startsWith("/api/interno/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Obtener la credencial enviada por el microservicio.
        String claveRecibida =
                request.getHeader("X-Internal-Key");

        // Rechazar solicitudes sin credencial válida.
        if (!esClaveValida(claveRecibida)) {

            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType("application/json");
            response.setCharacterEncoding(
                    StandardCharsets.UTF_8.name()
            );

            response.getWriter().write(
                    "{\"mensaje\":\"Credencial interna invalida\"}"
            );

            return;
        }

        // Crear una identidad exclusiva para ms-premios.
        UsernamePasswordAuthenticationToken autenticacion =
                new UsernamePasswordAuthenticationToken(
                        "ms-premios",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_SERVICIO_PREMIOS"
                                )
                        )
                );

        // Registrar la autenticación interna.
        SecurityContext contexto =
                SecurityContextHolder.createEmptyContext();

        contexto.setAuthentication(autenticacion);

        SecurityContextHolder.setContext(contexto);

        filterChain.doFilter(request, response);
    }

    // Comparar las claves de forma segura.
    private boolean esClaveValida(String claveRecibida) {

        if (claveRecibida == null
                || claveRecibida.isBlank()) {

            return false;
        }

        try {

            byte[] claveDecodificada =
                    Base64.getDecoder().decode(claveRecibida);

            return MessageDigest.isEqual(
                    claveInterna,
                    claveDecodificada
            );

        } catch (IllegalArgumentException e) {

            return false;
        }
    }
}