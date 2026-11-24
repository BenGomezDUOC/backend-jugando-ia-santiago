package cl.triskeledu.premios.client;

import cl.triskeledu.premios.dto.CapsulaResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class LocalizacionesClient {

    private final RestClient restClient;

    public LocalizacionesClient(
            @Value("${app.services.localizaciones-url}")
            String localizacionesUrl
    ) {

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        this.restClient = RestClient.builder()
                .baseUrl(localizacionesUrl)
                .requestFactory(factory)
                .build();
    }

    public CapsulaResponse validarQr(
            String codigoQr,
            String token
    ) {

        if (codigoQr == null || codigoQr.isBlank()) {

            throw new IllegalArgumentException(
                    "El código QR es obligatorio"
            );
        }

        CapsulaResponse capsula;

        try {

            capsula = restClient.get()
                    .uri(
                            "/api/localizaciones/capsulas/validar-qr?codigo={codigo}",
                            codigoQr.trim()
                    )
                    .headers(headers ->
                            headers.setBearerAuth(token))
                    .retrieve()
                    .body(CapsulaResponse.class);

        } catch (RestClientResponseException ex) {

            int estado = ex.getStatusCode().value();

            if (estado == 400 || estado == 404) {

                throw new IllegalArgumentException(
                        "El código QR no corresponde a una cápsula disponible"
                );
            }

            throw ex;
        }

        if (capsula == null
                || capsula.getId() == null
                || capsula.getId() <= 0) {

            throw new RestClientException(
                    "Respuesta inválida de ms-localizaciones"
            );
        }

        if (!"PUBLICADA".equals(capsula.getEstado())) {

            throw new IllegalArgumentException(
                    "La cápsula educativa no se encuentra publicada"
            );
        }

        return capsula;
    }
}