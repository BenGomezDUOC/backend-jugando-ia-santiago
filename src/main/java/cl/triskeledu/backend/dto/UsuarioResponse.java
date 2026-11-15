package cl.triskeledu.backend.dto;

import cl.triskeledu.backend.model.Rol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String alias;
    private String correo;
    private String telefono;
    private String direccion;
    private Integer avatar;
    private Rol rol;
    private Integer puntosAcumulados;
    private String tipoCuentaSistema;
}
