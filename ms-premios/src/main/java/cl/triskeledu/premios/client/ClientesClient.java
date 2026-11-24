package cl.triskeledu.premios.client;

import cl.triskeledu.premios.dto.OperacionPuntosResponse;
import cl.triskeledu.premios.dto.UsuarioResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class ClientesClient {

    private final RestClient restClient;
    private final String claveInterna;

    public ClientesClient(
            @Value("${app.services.clientes-url}") String clientesUrl,
            @Value("${app.internal.premios-key}") String claveInterna
    ) {

        this.claveInterna = claveInterna;

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        this.restClient = RestClient.builder()
                .baseUrl(clientesUrl)
                .requestFactory(factory)
                .build();
    }

    // Consultar al usuario mediante su JWT.
    public UsuarioResponse obtenerUsuario(
            Long usuarioId,
            String token
    ) {

        UsuarioResponse usuario = restClient.get()
                .uri("/api/clientes/usuarios/{id}", usuarioId)
                .headers(headers ->
                        headers.setBearerAuth(token))
                .retrieve()
                .body(UsuarioResponse.class);

        if (usuario == null
                || !usuarioId.equals(usuario.getId())
                || usuario.getPuntosAcumulados() == null) {

            throw new RestClientException(
                    "Respuesta inválida de ms-clientes"
            );
        }

        return usuario;
    }

    // Solicitar una acumulación segura de puntos.
    public OperacionPuntosResponse registrarPuntos(
            Long usuarioId,
            String codigoOperacion,
            int puntos,
            String motivo
    ) {

        Map<String, Object> solicitud = Map.of(
                "codigoOperacion", codigoOperacion,
                "puntos", puntos,
                "motivo", motivo
        );

        OperacionPuntosResponse respuesta = restClient.post()
                .uri(
                        "/api/interno/usuarios/{id}/puntos",
                        usuarioId
                )
                .header("X-Internal-Key", claveInterna)
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitud)
                .retrieve()
                .body(OperacionPuntosResponse.class);

        if (respuesta == null
                || !usuarioId.equals(respuesta.getUsuarioId())
                || !codigoOperacion.equals(
                respuesta.getCodigoOperacion())
                || !Integer.valueOf(puntos).equals(
                respuesta.getPuntos())
                || !motivo.equals(respuesta.getMotivo())
                || respuesta.getSaldoActual() == null) {

            throw new RestClientException(
                    "Respuesta inválida de la operación de puntos"
            );
        }

        return respuesta;
    }
}