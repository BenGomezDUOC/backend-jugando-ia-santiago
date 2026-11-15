package cl.triskeledu.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "puntos_interes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuntoInteres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del punto es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Builder.Default
    private Double altitud = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    @Builder.Default
    private Double radioToleranciaMetros = 50.0;

    @Column(length = 500)
    private String imagenUri;
}
