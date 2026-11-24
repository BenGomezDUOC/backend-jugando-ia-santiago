
package cl.triskeledu.clientes.config;

import cl.triskeledu.clientes.security.InternalAuthenticationFilter;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.internal.premios-key}")
    private String premiosInternalKey;

    // --------------------------------------------------
    // CLAVE JWT
    // --------------------------------------------------

    @Bean
    public SecretKey jwtSecretKey() {

        byte[] clave = Base64.getDecoder().decode(jwtSecret);

        if (clave.length < 32) {
            throw new IllegalStateException(
                    "La clave JWT debe tener al menos 256 bits"
            );
        }

        return new SecretKeySpec(clave, "HmacSHA256");
    }

    // --------------------------------------------------
    // GENERAR TOKENS JWT
    // --------------------------------------------------

    @Bean
    public JwtEncoder jwtEncoder(SecretKey clave) {

        return new NimbusJwtEncoder(
                new ImmutableSecret<>(clave)
        );
    }

    // --------------------------------------------------
    // VALIDAR TOKENS JWT
    // --------------------------------------------------

    @Bean
    public JwtDecoder jwtDecoder(SecretKey clave) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withSecretKey(clave)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(
                        "triskeledu"
                )
        );

        return decoder;
    }

    // --------------------------------------------------
    // ROLES DE LOS USUARIOS
    // --------------------------------------------------

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter rolesConverter =
                new JwtGrantedAuthoritiesConverter();

        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                rolesConverter
        );

        return converter;
    }

    // --------------------------------------------------
    // SEGURIDAD INTERNA: MS-PREMIOS
    // --------------------------------------------------

    @Bean
    @Order(1)
    public SecurityFilterChain internalSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .securityMatcher("/api/interno/**")

                .csrf(AbstractHttpConfigurer::disable)

                .cors(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(
                        new InternalAuthenticationFilter(
                                premiosInternalKey
                        ),
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(auth -> auth

                        // Solo ms-premios registra operaciones internas.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/interno/usuarios/{id}/puntos"
                        )
                        .hasRole("SERVICIO_PREMIOS")

                        .anyRequest().denyAll()
                );

        return http.build();
    }

    // --------------------------------------------------
    // SEGURIDAD DE LOS USUARIOS
    // --------------------------------------------------

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter converter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // Registro e inicio de sesión.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/clientes/usuarios/registro",
                                "/api/clientes/usuarios/login",
                                "/api/usuarios/registro",
                                "/api/usuarios/login"
                        )
                        .permitAll()

                        // Administrador: consultar usuarios.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/clientes/usuarios",
                                "/api/usuarios"
                        )
                        .hasRole("ADMINISTRADOR")

                        // Consultar el perfil propio.
                        // El administrador también puede consultarlo.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/clientes/usuarios/{id}",
                                "/api/usuarios/{id}"
                        )
                        .access(esPropietario(true))

                        // Cada usuario edita su propio perfil.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/clientes/usuarios/{id}/perfil",
                                "/api/usuarios/{id}/perfil"
                        )
                        .access(esPropietario(false))

                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        converter
                                )
                        )
                );

        return http.build();
    }

    // --------------------------------------------------
    // COMPROBAR PROPIETARIO DEL PERFIL
    // --------------------------------------------------

    private AuthorizationManager<RequestAuthorizationContext>
    esPropietario(boolean permitirAdministrador) {

        return (authentication, context) -> {

            var usuario = authentication.get();

            if (usuario == null || !usuario.isAuthenticated()) {
                return new AuthorizationDecision(false);
            }

            if (permitirAdministrador) {

                boolean esAdmin = usuario.getAuthorities()
                        .stream()
                        .anyMatch(rol ->
                                rol.getAuthority().equals(
                                        "ROLE_ADMINISTRADOR"
                                )
                        );

                if (esAdmin) {
                    return new AuthorizationDecision(true);
                }
            }

            String idSolicitado =
                    context.getVariables().get("id");

            boolean propietario =
                    idSolicitado != null
                            && idSolicitado.equals(usuario.getName());

            return new AuthorizationDecision(propietario);
        };
    }
}
