package cl.triskeledu.clientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacionPuntosRequest {

    // Código único para evitar operaciones duplicadas.
    @NotBlank(message = "El código de operación es obligatorio")
    @Size(
            max = 150,
            message = "El código no puede superar los 150 caracteres"
    )
    @Pattern(
            regexp = "^[A-Z0-9_-]+$",
            message = "El código de operación tiene un formato inválido"
    )
    private String codigoOperacion;

    // Cantidad de puntos que se otorgarán.
    @NotNull(message = "La cantidad de puntos es obligatoria")
    private Integer puntos;

    // Motivo de la acumulación de puntos.
    @NotBlank(message = "El motivo de la operación es obligatorio")
    @Pattern(
            regexp = "^(QR|SELFIE)$",
            message = "El motivo de la operación no es válido"
    )
    private String motivo;

    // Validar los puntos según el motivo de la operación.
    @AssertTrue(message = "La cantidad de puntos no corresponde al motivo")
    public boolean isOperacionValida() {

        if (puntos == null || motivo == null) {
            return false;
        }

        return switch (motivo) {

            case "QR" -> puntos == 10;

            case "SELFIE" -> puntos == 100;

            default -> false;
        };
    }
}