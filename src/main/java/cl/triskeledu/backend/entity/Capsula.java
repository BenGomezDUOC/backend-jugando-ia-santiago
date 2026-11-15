package cl.triskeledu.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "capsulas", indexes = {
    @Index(name = "index_capsulas_codigoQr", columnList = "codigoQr", unique = true)
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

    private Long puntoId;

    @NotBlank(message = "El código QR es obligatorio")
    @Column(nullable = false, unique = true, length = 100)
    private String codigoQr;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @Column(length = 500)
    @Builder.Default
    private String urlVideo = "";

    @Column(nullable = false, length = 80)
    @Builder.Default
    private String categoria = "General";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "PUBLICADA";
}
