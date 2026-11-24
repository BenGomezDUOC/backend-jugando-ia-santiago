package cl.triskeledu.localizaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "puntos_interes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntoInteres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50)
    @Column(length = 50, unique = true)
    private String codigo;

    @NotBlank(message = "El nombre del punto es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 5000)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String direccion;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Column(nullable = false)
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Column(nullable = false)
    private Double longitud;

    @NotNull(message = "La altitud es obligatoria")
    @Column(nullable = false)
    @Builder.Default
    private Double altitud = 0.0;

    @NotNull
    @Positive
    @Column(nullable = false)
    @Builder.Default
    private Double radioToleranciaMetros = 50.0;

    @Size(max = 500)
    @Column(length = 500)
    private String imagenUri;

    // Campos conservados temporalmente por compatibilidad.
    // No determinan el progreso ni los puntos del usuario.

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer puntosRecompensa = 100;

    @Column(length = 100)
    @Builder.Default
    private String qrEsperado = "";
}