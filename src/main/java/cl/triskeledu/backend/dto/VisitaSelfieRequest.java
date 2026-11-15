package cl.triskeledu.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VisitaSelfieRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El puntoId es obligatorio")
    private Long puntoId;

    private Long capsulaId;

    private String rutaFotoSelfie;

    private String codigoQrEsperado;
}
