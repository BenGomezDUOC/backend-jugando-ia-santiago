package cl.triskeledu.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PerfilUpdateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El alias es obligatorio")
    private String alias;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    private String correo;

    private String telefono;
    private String direccion;
    private Integer avatar;

    private String contrasenaActual;
    private String contrasenaNueva;
}
