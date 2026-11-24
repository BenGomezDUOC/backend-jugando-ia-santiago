package cl.triskeledu.premios.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
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
public class EscanearQrRequest {

    // Campo opcional para compatibilidad con Android.
    // No determina la identidad del usuario.
    @Positive
    private Long usuarioId;

    @NotBlank(message = "El código QR es obligatorio")
    @Size(max = 500)
    private String codigoQr;

    // Opcional cuando se utiliza el escáner general.
    @JsonAlias("puntoId")
    @Positive(message = "El punto turístico no es válido")
    private Long puntoIdSeleccionado;
}