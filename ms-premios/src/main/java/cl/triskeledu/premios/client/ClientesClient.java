package cl.triskeledu.premios.client;

import cl.triskeledu.premios.dto.UsuarioResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class ClientesClient {

    private final RestClient restClient;

    public ClientesClient(@Value("${app.services.clientes-url:http://localhost:8081}") String clientesUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(clientesUrl)
                .build();
    }

    public UsuarioResponse obtenerUsuario(Long usuarioId) {
        try {
            return restClient.get()
                    .uri("/api/clientes/usuarios/{id}", usuarioId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        log.warn("Error consultando ms-clientes para usuario ID {}: {}", usuarioId, res.getStatusCode());
                        throw new IllegalArgumentException("Usuario no encontrado o no disponible en ms-clientes");
                    })
                    .body(UsuarioResponse.class);
        } catch (Exception e) {
            log.error("Fallo comunicación con ms-clientes al obtener usuario {}: {}", usuarioId, e.getMessage());
            throw new IllegalArgumentException("Usuario no encontrado en ms-clientes: " + e.getMessage());
        }
    }

    public UsuarioResponse sumarPuntos(Long usuarioId, int puntos) {
        try {
            return restClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/clientes/usuarios/{id}/sumar-puntos")
                            .queryParam("puntos", puntos)
                            .build(usuarioId))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        log.warn("Error sumando puntos en ms-clientes para usuario {}: {}", usuarioId, res.getStatusCode());
                        throw new IllegalArgumentException("No se pudieron sumar puntos en ms-clientes");
                    })
                    .body(UsuarioResponse.class);
        } catch (Exception e) {
            log.error("Fallo comunicación con ms-clientes al sumar puntos a usuario {}: {}", usuarioId, e.getMessage());
            throw new IllegalArgumentException("Error al actualizar puntos del usuario: " + e.getMessage());
        }
    }
}
