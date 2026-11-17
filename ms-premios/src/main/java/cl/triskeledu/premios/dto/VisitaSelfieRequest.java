package cl.triskeledu.premios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitaSelfieRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El puntoId es obligatorio")
    private Long puntoId;

    private Long capsulaId;

    private String rutaFotoSelfie;

    private String codigoQrEsperado;
}
