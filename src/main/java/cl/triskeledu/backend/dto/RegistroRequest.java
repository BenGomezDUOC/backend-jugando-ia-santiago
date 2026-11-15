package cl.triskeledu.backend.dto;

import cl.triskeledu.backend.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El alias es obligatorio")
    @Size(min = 2, max = 50, message = "El alias debe tener entre 2 y 50 caracteres")
    private String alias;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    private String correo;

    private String telefono = "";
    private String direccion = "";
    private Integer avatar = 1;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    private Rol rol = Rol.CLIENTE;
}
