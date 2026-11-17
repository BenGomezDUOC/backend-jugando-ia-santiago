package cl.triskeledu.clientes.dto;

import cl.triskeledu.clientes.model.Rol;
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

    @Builder.Default
    private String telefono = "";

    @Builder.Default
    private String direccion = "";

    @Builder.Default
    private Integer avatar = 1;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @Builder.Default
    private Rol rol = Rol.CLIENTE;
}
