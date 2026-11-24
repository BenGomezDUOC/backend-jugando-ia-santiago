package cl.triskeledu.clientes.entity;

import cl.triskeledu.clientes.model.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", indexes = {
        @Index(
                name = "index_usuarios_correo",
                columnList = "correo",
                unique = true
        ),
        @Index(
                name = "index_usuarios_tipoCuentaSistema",
                columnList = "tipoCuentaSistema",
                unique = true
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    // Identificador único del usuario.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre completo.
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    // Alias del usuario.
    @NotBlank(message = "El alias es obligatorio")
    @Column(nullable = false, length = 100)
    private String alias;

    // Correo electrónico único.
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    // Número de teléfono.
    @Pattern(
            regexp = "^([0-9]{8,15})?$",
            message = "El teléfono debe contener entre 8 y 15 dígitos"
    )
    @Column(length = 20)
    @Builder.Default
    private String telefono = "";

    // Dirección opcional.
    @Column(length = 255)
    @Builder.Default
    private String direccion = "";

    // Avatar seleccionado.
    @NotNull(message = "El avatar es obligatorio")
    @Min(value = 1, message = "Avatar no válido")
    @Max(value = 3, message = "Avatar no válido")
    @Column(nullable = false)
    @Builder.Default
    private Integer avatar = 1;

    // Contraseña almacenada mediante hash.
    // No debe exponerse en las respuestas JSON.
    @JsonIgnore
    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false, length = 255)
    private String contrasena;

    // Rol asignado al usuario.
    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Rol rol = Rol.CLIENTE;

    // Puntaje acumulado por el jugador.
    @NotNull(message = "Los puntos son obligatorios")
    @PositiveOrZero(message = "Los puntos no pueden ser negativos")
    @Column(nullable = false)
    @Builder.Default
    private Integer puntosAcumulados = 0;

    // Identificador de las cuentas predefinidas.
    @Column(length = 50, unique = true)
    private String tipoCuentaSistema;

    // Fecha de creación de la cuenta.
    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

}