package cl.triskeledu.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @NotBlank(message = "La URL del video es obligatoria")
    @Column(nullable = false, length = 500)
    private String urlVideo;

    private Long contenidoId;

    @Column(length = 100)
    @Builder.Default
    private String autor = "";

    @Builder.Default
    private Long duracion = 0L;

    @Column(length = 500)
    @Builder.Default
    private String urlMiniatura = "";
}
