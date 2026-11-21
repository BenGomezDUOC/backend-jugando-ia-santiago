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
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String alias;
    private String correo;
    private Integer puntosAcumulados;
    private String rol;
}
