package cl.triskeledu.localizaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "videos_registrados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRegistrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del video es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @Size(max = 5000)
    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @NotBlank(message = "La URL del video es obligatoria")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String urlVideo;

    @NotNull(message = "El contenido educativo es obligatorio")
    @Positive
    @Column(nullable = false)
    private Long contenidoId;

    @Size(max = 100)
    @Column(length = 100)
    @Builder.Default
    private String autor = "";

    @NotNull
    @PositiveOrZero
    @Builder.Default
    private Long duracion = 0L;

    @Size(max = 500)
    @Column(length = 500)
    @Builder.Default
    private String urlMiniatura = "";
}