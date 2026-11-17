package cl.triskeledu.premios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapsulaResponse {
    private Long id;
    private Long puntoId;
    private String codigoQr;
    private String titulo;
    private String descripcion;
    private String urlVideo;
    private String categoria;
    private String estado;
}
