package cl.triskeledu.premios.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "descubrimientos_qr", indexes = {
    @Index(name = "index_descubrimientos_qr_usuarioId", columnList = "usuarioId"),
    @Index(name = "index_descubrimientos_qr_capsulaId", columnList = "capsulaId"),
    @Index(name = "index_descubrimientos_qr_usuarioId_capsulaId", columnList = "usuarioId, capsulaId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescubrimientoQr {

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
