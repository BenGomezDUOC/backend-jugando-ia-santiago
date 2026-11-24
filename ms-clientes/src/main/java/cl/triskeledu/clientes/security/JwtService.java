package cl.triskeledu.clientes.security;

import cl.triskeledu.clientes.entity.Usuario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-seconds:3600}")
    private long expirationSeconds;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    // --------------------------------------------------
    // GENERAR TOKEN
    // --------------------------------------------------

    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()

                // Sistema que emite el token.
                .issuer("triskeledu")

                // Fecha de emisión.
                .issuedAt(ahora)

                // Fecha de vencimiento.
                .expiresAt(
                        ahora.plusSeconds(expirationSeconds)
                )

                // Identificador del usuario.
                .subject(usuario.getId().toString())

                // Rol real del usuario autenticado.
                .claim(
                        "roles",
                        List.of(usuario.getRol().name())
                )

                .build();

        // Algoritmo de firma.
        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        // Generar el token firmado.
        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    // --------------------------------------------------
    // OBTENER DURACIÓN DEL TOKEN
    // --------------------------------------------------

    public long obtenerDuracionToken() {

        return expirationSeconds;
    }
}