
package cl.triskeledu.localizaciones.config;

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
    // VALIDACIÓN DEL TOKEN JWT
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
    // ROLES DEL TOKEN
    // --------------------------------------------------

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

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
    // SEGURIDAD DE MS-LOCALIZACIONES
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
                        // ADMINISTRADOR: CREAR PUNTOS TURÍSTICOS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/localizaciones/puntos-interes",
                                "/api/localizaciones/puntos",
                                "/api/puntos-interes",
                                "/api/puntos"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ADMINISTRADOR: EDITAR PUNTOS TURÍSTICOS.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/localizaciones/puntos-interes/{id}",
                                "/api/localizaciones/puntos/{id}",
                                "/api/puntos-interes/{id}",
                                "/api/puntos/{id}"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ADMINISTRADOR: ELIMINAR PUNTOS TURÍSTICOS.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/localizaciones/puntos-interes/{id}",
                                "/api/localizaciones/puntos/{id}",
                                "/api/puntos-interes/{id}",
                                "/api/puntos/{id}"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ------------------------------------------
                        // TODOS: CONSULTAR PUNTOS TURÍSTICOS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/localizaciones/puntos-interes",
                                "/api/localizaciones/puntos-interes/{id}",
                                "/api/localizaciones/puntos",
                                "/api/localizaciones/puntos/{id}",
                                "/api/puntos-interes",
                                "/api/puntos-interes/{id}",
                                "/api/puntos",
                                "/api/puntos/{id}"
                        )
                        .hasAnyRole(
                                "CLIENTE",
                                "ADMINISTRADOR",
                                "GENERADOR"
                        )

                        // ------------------------------------------
                        // GENERADOR: CREAR CONTENIDO EDUCATIVO
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/localizaciones/contenidos",
                                "/api/contenidos"
                        )
                        .hasRole("GENERADOR")

                        // GENERADOR: EDITAR CONTENIDO.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/localizaciones/contenidos/{id}",
                                "/api/contenidos/{id}"
                        )
                        .hasRole("GENERADOR")

                        // GENERADOR: ELIMINAR CONTENIDO.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/localizaciones/contenidos/{id}",
                                "/api/contenidos/{id}"
                        )
                        .hasRole("GENERADOR")

                        // ------------------------------------------
                        // ADMINISTRADOR: VIDEOS EDUCATIVOS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/localizaciones/videos",
                                "/api/videos"
                        )
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/localizaciones/videos/{id}",
                                "/api/videos/{id}"
                        )
                        .hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/localizaciones/videos/{id}",
                                "/api/videos/{id}"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ------------------------------------------
                        // ADMINISTRADOR: GENERAR CÓDIGOS QR
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/localizaciones/capsulas",
                                "/api/capsulas"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ADMINISTRADOR: PUBLICAR Y DESPUBLICAR QR.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/localizaciones/capsulas/{id}/publicar",
                                "/api/capsulas/{id}/publicar",
                                "/api/localizaciones/capsulas/{id}/despublicar",
                                "/api/capsulas/{id}/despublicar"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ------------------------------------------
                        // ADMINISTRADOR: PUBLICAR CONTENIDOS SIN QR
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/localizaciones/contenidos/{id}/publicar-sin-qr",
                                "/api/contenidos/{id}/publicar-sin-qr",
                                "/api/localizaciones/contenidos/{id}/despublicar-sin-qr",
                                "/api/contenidos/{id}/despublicar-sin-qr"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ------------------------------------------
                        // TODOS: CONSULTAR CONTENIDOS Y VIDEOS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/localizaciones/contenidos",
                                "/api/localizaciones/contenidos/{id}",
                                "/api/contenidos",
                                "/api/contenidos/{id}",
                                "/api/localizaciones/videos",
                                "/api/localizaciones/videos/{id}",
                                "/api/videos",
                                "/api/videos/{id}"
                        )
                        .hasAnyRole(
                                "CLIENTE",
                                "ADMINISTRADOR",
                                "GENERADOR"
                        )

                        // ------------------------------------------
                        // ADMINISTRADOR Y GENERADOR:
                        // CONSULTAR CÁPSULAS CON QR
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/localizaciones/capsulas",
                                "/api/capsulas"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "GENERADOR"
                        )

                        // ------------------------------------------
                        // CLIENTE: VALIDAR UN QR ESCANEADO
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/localizaciones/capsulas/validar-qr",
                                "/api/capsulas/validar-qr"
                        )
                        .hasAnyRole(
                                "CLIENTE",
                                "ADMINISTRADOR",
                                "GENERADOR"
                        )

                        // ------------------------------------------
                        // ADMINISTRADOR: SUBIR IMÁGENES Y MP4
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/localizaciones/multimedia/imagen",
                                "/api/localizaciones/multimedia/video"
                        )
                        .hasRole("ADMINISTRADOR")

                        // ------------------------------------------
                        // TODOS: CONSULTAR ARCHIVOS AUTORIZADOS
                        // ------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/localizaciones/multimedia/{nombre}"
                        )
                        .hasAnyRole(
                                "CLIENTE",
                                "ADMINISTRADOR",
                                "GENERADOR"
                        )

                        // ------------------------------------------
                        // BLOQUEAR LAS DEMÁS SOLICITUDES
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
