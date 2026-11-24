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

    // Identificador único del usuario.
    private Long id;

    // Datos personales.
    private String nombre;
    private String alias;
    private String correo;
    private String telefono;
    private String direccion;

    // Avatar seleccionado por el usuario.
    private Integer avatar;

    // Rol asignado al usuario.
    private Rol rol;

    // Puntaje acumulado.
    private Integer puntosAcumulados;

    // Fecha de creación de la cuenta.
    private String fechaRegistro;

    // Identificación de cuentas internas del sistema.
    private String tipoCuentaSistema;


    @JsonProperty("nombreCompleto")
    public String getNombreCompleto() {
        return nombre;
    }

    @JsonProperty("correoElectronico")
    public String getCorreoElectronico() {
        return correo;
    }

    // Nivel calculado a partir de los puntos acumulados.
    @JsonProperty("nivelActual")
    public Integer getNivelActual() {

        int puntos = puntosAcumulados != null
                ? Math.max(0, puntosAcumulados)
                : 0;

        return (puntos / 100) + 1;
    }
}