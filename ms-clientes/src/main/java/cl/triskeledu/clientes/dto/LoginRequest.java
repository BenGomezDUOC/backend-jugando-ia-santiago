package cl.triskeledu.clientes.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    // Correo electrónico del usuario.
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo electrónico válido")
    @Size(
            max = 150,
            message = "El correo no puede superar los 150 caracteres"
    )
    @JsonAlias("correoElectronico")
    private String correo;

    // Contraseña del usuario.
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}