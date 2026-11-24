
package cl.triskeledu.premios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitaSelfieRequest {

    // Compatibilidad con el identificador de Android.
    @Positive
    private Long usuarioId;

    @NotNull(message = "El punto turístico es obligatorio")
    @Positive
    private Long puntoId;

    @NotNull(message = "La cápsula es obligatoria")
    @Positive
    private Long capsulaId;

    @NotBlank(message = "El código QR es obligatorio")
    @Size(max = 500)
    private String codigoQrEsperado;

    // La fotografía se enviará como archivo multipart.
    // La ruta se genera en el backend.
}
