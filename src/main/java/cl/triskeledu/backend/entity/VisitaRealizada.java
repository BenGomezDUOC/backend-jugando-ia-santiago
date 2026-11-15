package cl.triskeledu.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "visitas", indexes = {
    @Index(name = "index_visitas_usuarioId", columnList = "usuarioId"),
    @Index(name = "index_visitas_puntoId", columnList = "puntoId"),
    @Index(name = "index_visitas_usuario_punto", columnList = "usuarioId, puntoId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitaRealizada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long puntoId;

    private Long capsulaId;

    @Column(length = 500)
    private String rutaFotoSelfie;

    @Column(nullable = false)
    @Builder.Default
    private Integer puntosGanados = 100;

    @Column(nullable = false)
    @Builder.Default
    private Long fechaTimestamp = System.currentTimeMillis();

    @Column(nullable = false)
    @Builder.Default
    private Boolean completada = true;
}
