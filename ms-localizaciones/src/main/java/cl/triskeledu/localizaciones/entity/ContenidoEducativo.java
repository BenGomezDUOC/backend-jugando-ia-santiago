package cl.triskeledu.localizaciones.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "contenidos_educativos", indexes = {
    @Index(name = "index_contenidos_educativos_codigoQr", columnList = "codigoQr", unique = true)
})
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
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @Column(nullable = false, length = 80)
    @Builder.Default
    private String categoria = "General";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "BORRADOR";

    private Long puntoId;

    @Column(length = 100, unique = true)
    private String codigoQr;
}
