package cl.triskeledu.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "notificaciones", indexes = {
    @Index(name = "index_notificaciones_usuarioId", columnList = "usuarioId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String tipo = "GENERAL";

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String rutaDestino = "notificaciones";

    @Column(nullable = false)
    @Builder.Default
    private Long fechaTimestamp = System.currentTimeMillis();

    @Column(nullable = false)
    @Builder.Default
    private Boolean leida = false;
}
