package cl.triskeledu.clientes.dto;

import cl.triskeledu.clientes.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
