package cl.triskeledu.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "videos_vistos", indexes = {
    @Index(name = "index_videos_vistos_usuarioId", columnList = "usuarioId"),
    @Index(name = "index_videos_vistos_capsulaId", columnList = "capsulaId"),
    @Index(name = "index_videos_vistos_usuarioId_capsulaId", columnList = "usuarioId, capsulaId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoVisto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long capsulaId;

    @Column(nullable = false)
    @Builder.Default
    private Long fechaTimestamp = System.currentTimeMillis();
}
