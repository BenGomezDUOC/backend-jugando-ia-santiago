package cl.triskeledu.premios.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "videos_adicionales_vistos", indexes = {
    @Index(name = "index_videos_adic_usuarioId", columnList = "usuarioId"),
    @Index(name = "index_videos_adic_usuario_video", columnList = "usuarioId, videoAdicionalId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoAdicionalVisto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Integer videoAdicionalId;

    @Column(nullable = false)
    @Builder.Default
    private Long fechaTimestamp = System.currentTimeMillis();
}
