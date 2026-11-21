package cl.triskeledu.clientes.dto;

import cl.triskeledu.clientes.model.Rol;
import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias({"nombreCompleto", "nombre"})
    private String nombre;

    @JsonAlias({"alias"})
    private String alias;

    @NotBlank(message = "El correo es obligatorio")
    @JsonAlias({"correoElectronico", "correo"})
    private String correo;

    @Builder.Default
    private String telefono = "";

    @Builder.Default
    private String direccion = "";

    @Builder.Default
    private String institucion = "Triskeledu";

    @Builder.Default
    private Integer avatar = 1;

    @Builder.Default
    @JsonAlias({"avatarSeleccionado"})
    private String avatarSeleccionado = "avatar_explorador_1";

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres")
    private String contrasena;

    @Builder.Default
    private Rol rol = Rol.CLIENTE;

    public String getNombreFinal() {
        return nombre != null ? nombre.trim() : "";
    }

    public String getAliasFinal() {
        if (alias != null && !alias.isBlank()) {
            return alias.trim();
        }
        return getNombreFinal();
    }
}
