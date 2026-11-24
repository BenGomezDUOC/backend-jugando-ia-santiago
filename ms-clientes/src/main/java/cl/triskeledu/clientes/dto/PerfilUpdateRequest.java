package cl.triskeledu.clientes.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUpdateRequest {

    // Nombre completo del usuario.
    @NotBlank(message = "El nombre es obligatorio")
    @Size(
            min = 3,
            max = 100,
            message = "El nombre debe tener entre 3 y 100 caracteres"
    )
    private String nombre;

    // Alias del usuario.
    @NotBlank(message = "El alias es obligatorio")
    @Size(
            min = 3,
            max = 50,
            message = "El alias debe tener entre 3 y 50 caracteres"
    )
    private String alias;

    // Correo electrónico.
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo electrónico válido")
    @Size(max = 150)
    private String correo;

    // Teléfono.
    @Pattern(
            regexp = "^([0-9]{8,15})?$",
            message = "El teléfono debe contener entre 8 y 15 dígitos"
    )
    private String telefono;

    // Dirección opcional.
    @Size(
            max = 255,
            message = "La dirección no puede superar los 255 caracteres"
    )
    private String direccion;

    // Avatar seleccionado.
    @Min(value = 1, message = "Avatar no válido")
    @Max(value = 3, message = "Avatar no válido")
    private Integer avatar;

    // Contraseñas para el cambio opcional.
    private String contrasenaActual;
    private String contrasenaNueva;

    // Valida la longitud de la contraseña nueva.
    @AssertTrue(
            message = "La contraseña nueva debe tener al menos 8 caracteres"
    )
    public boolean isContrasenaNuevaValida() {

        if (contrasenaNueva == null || contrasenaNueva.isEmpty()) {
            return true;
        }

        return !contrasenaNueva.isBlank()
                && contrasenaNueva.length() >= 8;
    }

    // Exige la contraseña actual si desea cambiarla.
    @AssertTrue(
            message = "Debes ingresar tu contraseña actual"
    )
    public boolean isContrasenaActualValida() {

        if (contrasenaNueva == null || contrasenaNueva.isEmpty()) {
            return true;
        }

        return contrasenaActual != null
                && !contrasenaActual.isBlank();
    }
}