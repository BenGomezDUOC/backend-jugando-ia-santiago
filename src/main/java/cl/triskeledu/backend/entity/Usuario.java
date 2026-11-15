package cl.triskeledu.backend.entity;

import cl.triskeledu.backend.model.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "index_usuarios_correo", columnList = "correo", unique = true),
    @Index(name = "index_usuarios_tipoCuentaSistema", columnList = "tipoCuentaSistema", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El alias es obligatorio")
    @Column(nullable = false, length = 50)
    private String alias;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo electrónico válido")
    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(length = 20)
    @Builder.Default
    private String telefono = "";

    @Column(length = 200)
    @Builder.Default
    private String direccion = "";

    @Column(nullable = false)
    @Builder.Default
    private Integer avatar = 1;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false, length = 255)
    private String contrasena;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Rol rol = Rol.CLIENTE;

    @Column(nullable = false)
    @Builder.Default
    private Integer puntosAcumulados = 0;

    @Column(length = 50, unique = true)
    private String tipoCuentaSistema;
}
