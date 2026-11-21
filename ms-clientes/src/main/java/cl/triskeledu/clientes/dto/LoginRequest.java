package cl.triskeledu.clientes.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El correo es obligatorio")
    @JsonAlias({"correoElectronico", "correo"})
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
}
