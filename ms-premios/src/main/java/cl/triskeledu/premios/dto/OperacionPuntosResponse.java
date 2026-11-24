package cl.triskeledu.premios.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperacionPuntosResponse {

    private Long idOperacion;
    private String codigoOperacion;
    private Long usuarioId;
    private Integer puntos;
    private String motivo;
    private Integer saldoAnterior;
    private Integer saldoResultante;
    private Integer saldoActual;
    private boolean yaProcesada;
    private String fechaOperacion;
    private String mensaje;
}
