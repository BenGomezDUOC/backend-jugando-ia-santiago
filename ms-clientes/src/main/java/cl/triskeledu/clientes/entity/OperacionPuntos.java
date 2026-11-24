package cl.triskeledu.clientes.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "operaciones_puntos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_operacion_puntos_codigo",
                        columnNames = "codigo_operacion"
                )
        },
        indexes = {
                @Index(
                        name = "idx_operacion_puntos_usuario",
                        columnList = "usuario_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacionPuntos {

    // Identificador interno del registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificador único enviado por ms-premios.
    @Column(
            name = "codigo_operacion",
            nullable = false,
            length = 150,
            updatable = false
    )
    private String codigoOperacion;

    // Usuario al que pertenece el movimiento.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            updatable = false
    )
    private Usuario usuario;

    // Cantidad de puntos sumados o descontados.
    @Column(nullable = false, updatable = false)
    private Integer puntos;

    // Motivo por el cual se realizó la operación.
    @Column(
            nullable = false,
            length = 100,
            updatable = false
    )
    private String motivo;

    // Saldo del usuario antes de la operación.
    @Column(
            name = "saldo_anterior",
            nullable = false,
            updatable = false
    )
    private Integer saldoAnterior;

    // Saldo del usuario después de la operación.
    @Column(
            name = "saldo_resultante",
            nullable = false,
            updatable = false
    )
    private Integer saldoResultante;

    // Fecha y hora en que se procesó el movimiento.
    @Builder.Default
    @Column(
            name = "fecha_operacion",
            nullable = false,
            updatable = false
    )
    private LocalDateTime fechaOperacion =
            LocalDateTime.now();
}
