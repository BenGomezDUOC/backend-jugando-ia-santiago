package cl.triskeledu.clientes.dto;

import cl.triskeledu.clientes.model.Rol;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RegistroRequest {

    // Nombre completo del cliente.
    @NotBlank(message = "El nombre es obligatorio")
    @Size(
            min = 3,
            max = 100,
            message = "El nombre debe tener entre 3 y 100 caracteres"
    )
    @JsonAlias("nombreCompleto")
    private String nombre;

    // Nombre de usuario.
    @NotBlank(message = "El alias es obligatorio")
    @Size(
            min = 3,
            max = 50,
            message = "El alias debe tener entre 3 y 50 caracteres"
    )
    private String alias;

    // Correo electrónico del cliente.
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo electrónico válido")
    @Size(max = 150)
    @JsonAlias("correoElectronico")
    private String correo;

    // Número de teléfono opcional.
    @Pattern(
            regexp = "^$|^[+]?[0-9\\s-]{8,20}$",
            message = "El teléfono debe contener entre 8 y 15 dígitos"
    )
    private String telefono = "";

    // Dirección opcional.
    @Builder.Default
    @Size(
            max = 255,
            message = "La dirección no puede superar los 255 caracteres"
    )
    private String direccion = "";

    // Avatar seleccionado en Android.
    @Builder.Default
    @NotNull(message = "Debes seleccionar un avatar")
    @Min(value = 1, message = "Avatar no válido")
    @Max(value = 3, message = "Avatar no válido")
    private Integer avatar = 1;

    // Contraseña del cliente.
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(
            min = 3,
            message = "La contraseña debe tener al menos 3 caracteres"
    )
    private String contrasena;

    // El registro público corresponde únicamente a clientes.
    @Builder.Default
    @NotNull(message = "El rol es obligatorio")
    private Rol rol = Rol.CLIENTE;


    // Impide solicitar roles privilegiados desde el registro.
    @AssertTrue(
            message = "Solo se permite el registro de clientes"
    )
    public boolean isRolValido() {
        return rol == Rol.CLIENTE;
    }

    // Devuelve el nombre sin espacios innecesarios.
    public String getNombreFinal() {
        return nombre != null ? nombre.trim() : "";
    }

    // Devuelve el alias sin espacios innecesarios.
    public String getAliasFinal() {
        return alias != null ? alias.trim() : "";
    }
}