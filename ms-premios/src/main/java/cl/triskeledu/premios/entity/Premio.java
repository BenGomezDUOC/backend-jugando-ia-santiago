package cl.triskeledu.premios.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "premios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Premio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del premio es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    private String descripcion = "";

    @NotNull(message = "Los puntos requeridos son obligatorios")
    @Column(nullable = false)
    @Builder.Default
    private Integer puntosRequeridos = 100;

    @NotNull(message = "El stock disponible es obligatorio")
    @Column(nullable = false)
    @Builder.Default
    private Integer stockDisponible = 10;

    @Column(length = 500)
    private String imagenUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
