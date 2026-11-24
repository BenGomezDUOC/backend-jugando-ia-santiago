package cl.triskeledu.localizaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "capsulas", indexes = {
        @Index(
                name = "index_capsulas_codigoQr",
                columnList = "codigoQr",
                unique = true
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capsula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El punto turístico es obligatorio")
    @Positive
    @Column(nullable = false)
    private Long puntoId;

    @NotNull(message = "El contenido educativo es obligatorio")
    @Positive
    @Column(nullable = false)
    private Long contenidoId;

    @NotNull(message = "El video educativo es obligatorio")
    @Positive
    @Column(nullable = false)
    private Long videoId;

    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String codigoQr;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @Size(max = 5000)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @Size(max = 500)
    @Column(length = 500)
    @Builder.Default
    private String urlVideo = "";

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
}