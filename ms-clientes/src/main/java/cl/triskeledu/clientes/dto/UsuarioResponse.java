package cl.triskeledu.clientes.dto;

import cl.triskeledu.clientes.model.Rol;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("nombreCompleto")
    public String getNombreCompleto() {
        return nombre;
    }

    private String alias;

    private String correo;

    @JsonProperty("correoElectronico")
    public String getCorreoElectronico() {
        return correo;
    }

    private String telefono;
    private String direccion;

    private Integer avatar;

    @JsonProperty("avatarSeleccionado")
    private String avatarSeleccionado;

    @JsonProperty("institucion")
    private String institucion;

    private Rol rol;

    private Integer puntosAcumulados;

    @JsonProperty("nivelActual")
    public Integer getNivelActual() {
        int pts = puntosAcumulados != null ? puntosAcumulados : 0;
        return (pts / 100) + 1;
    }

    @JsonProperty("fechaRegistro")
    private String fechaRegistro;

    private String tipoCuentaSistema;
}
