package cl.triskeledu.localizaciones.entity;

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

    @Column(length = 50)
    @Builder.Default
    private String codigo = "";

    @NotBlank(message = "El nombre del punto es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @Column(length = 300)
    @Builder.Default
    private String direccion = "";

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

    @Column(nullable = false)
    @Builder.Default
    private Integer puntosRecompensa = 100;

    @Column(length = 100)
    @Builder.Default
    private String qrEsperado = "";
}
