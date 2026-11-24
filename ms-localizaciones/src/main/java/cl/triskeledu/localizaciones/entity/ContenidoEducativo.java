package cl.triskeledu.localizaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "contenidos_educativos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenidoEducativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @Size(max = 5000)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    @Builder.Default
    private String categoria = "General";

    @NotBlank
    @Pattern(regexp = "BORRADOR|PUBLICADA")
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "BORRADOR";

    // NULL = cápsula educativa general sin QR.
    // Con valor = contenido asociado a un punto turístico.
    @Positive
    @Column(nullable = true)
    private Long puntoId;

    // El generador no asigna este código.
    // El administrador lo genera posteriormente.
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String codigoQr;

    private Long generadorId;
}