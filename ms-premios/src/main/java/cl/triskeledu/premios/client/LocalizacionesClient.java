package cl.triskeledu.premios.client;

import cl.triskeledu.premios.dto.CapsulaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LocalizacionesClient {

    private final RestClient restClient;

    public LocalizacionesClient(@Value("${app.services.localizaciones-url:http://localhost:8083}") String localizacionesUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(localizacionesUrl)
                .build();
    }

    public CapsulaResponse validarQr(String codigoQr) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/localizaciones/capsulas/validar-qr")
                            .queryParam("codigo", codigoQr.trim())
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String msg = "";
                        try {
                            msg = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        } catch (Exception ignored) {}
                        log.warn("Error validando QR '{}' en ms-localizaciones: {} - {}", codigoQr, res.getStatusCode(), msg);
                        if (msg.contains("no se encuentra publicada")) {
                            throw new IllegalArgumentException("La cápsula educativa no se encuentra publicada");
                        }
                        throw new IllegalArgumentException("El código QR no corresponde a ninguna cápsula de Triskeledu");
                    })
                    .body(CapsulaResponse.class);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Fallo comunicación con ms-localizaciones al validar QR '{}': {}", codigoQr, e.getMessage());
            throw new IllegalArgumentException("Error comunicando con servicio de localizaciones para validar QR: " + e.getMessage());
        }
    }
}
