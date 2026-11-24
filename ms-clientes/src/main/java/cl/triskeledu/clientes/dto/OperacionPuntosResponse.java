package cl.triskeledu.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacionPuntosResponse {

    // Identificador del movimiento registrado.
    private Long idOperacion;

    // Código único de la operación.
    private String codigoOperacion;

    // Identificador del usuario.
    private Long usuarioId;

    // Cantidad de puntos sumados o descontados.
    private Integer puntos;

    // Motivo de la operación.
    private String motivo;

    // Saldo antes de realizar la operación.
    private Integer saldoAnterior;

    // Saldo después de realizar la operación.
    private Integer saldoResultante;

    // Saldo actual del usuario.
    private Integer saldoActual;

    // Indica si la operación había sido procesada anteriormente.
    private boolean yaProcesada;

    // Fecha y hora de la operación original.
    private String fechaOperacion;

    // Mensaje informativo del resultado.
    private String mensaje;
}
