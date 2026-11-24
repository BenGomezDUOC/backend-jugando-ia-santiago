
package cl.triskeledu.premios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

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
    // VALIDAR TOKEN
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
    // OBTENER ROLES DEL JWT
    // --------------------------------------------------

    @Bean
    public JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter roles =
                new JwtGrantedAuthoritiesConverter();

        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(roles);

        return converter;
    }

    // --------------------------------------------------
    // SEGURIDAD DE MS-PREMIOS
    // --------------------------------------------------

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter converter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        // ------------------------------------------
                        // CLIENTE: ESCANEAR QR
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/premios/gamificacion/escanear-qr",
                                "/api/gamificacion/escanear-qr"
                        )
                        .hasRole("CLIENTE")

                        // ------------------------------------------
                        // CLIENTE: REGISTRAR SELFIE REAL
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/premios/gamificacion/visita-selfie",
                                "/api/gamificacion/visita-selfie"
                        )
                        .hasRole("CLIENTE")

                        // ------------------------------------------
                        // CLIENTE: CONSULTAR SU FOTO
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/premios/gamificacion/selfies/{nombre}",
                                "/api/gamificacion/selfies/{nombre}"
                        )
                        .hasRole("CLIENTE")

                        // ------------------------------------------
                        // CLIENTE: REGISTRAR VIDEO VISTO
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/premios/gamificacion/video-visto",
                                "/api/gamificacion/video-visto"
                        )
                        .hasRole("CLIENTE")


                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/premios/gamificacion/video-adicional-visto",
                                "/api/gamificacion/video-adicional-visto"
                        )
                        .hasRole("CLIENTE")


                        // ------------------------------------------
                        // CLIENTE: CONSULTAR DESCUBRIMIENTOS
                        // Y VISITAS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/premios/gamificacion/descubrimientos/{usuarioId}",
                                "/api/gamificacion/descubrimientos/{usuarioId}",
                                "/api/premios/gamificacion/visitas/{usuarioId}",
                                "/api/gamificacion/visitas/{usuarioId}"
                        )
                        .hasRole("CLIENTE")

                        // ------------------------------------------
                        // CONSULTAR NOTIFICACIONES PROPIAS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/premios/notificaciones/usuario/{usuarioId}",
                                "/api/notificaciones/usuario/{usuarioId}",
                                "/api/premios/notificaciones/usuario/{usuarioId}/no-leidas/count",
                                "/api/notificaciones/usuario/{usuarioId}/no-leidas/count"
                        )
                        .hasAnyRole(
                                "CLIENTE",
                                "ADMINISTRADOR",
                                "GENERADOR"
                        )

                        // ------------------------------------------
                        // MARCAR NOTIFICACIONES COMO LEÍDAS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/premios/notificaciones/{id}/leer",
                                "/api/notificaciones/{id}/leer",
                                "/api/premios/notificaciones/usuario/{usuarioId}/leer-todas",
                                "/api/notificaciones/usuario/{usuarioId}/leer-todas"
                        )
                        .hasAnyRole(
                                "CLIENTE",
                                "ADMINISTRADOR",
                                "GENERADOR"
                        )

                        // ------------------------------------------
                        // BLOQUEAR EL RESTO
                        // ------------------------------------------

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
}
