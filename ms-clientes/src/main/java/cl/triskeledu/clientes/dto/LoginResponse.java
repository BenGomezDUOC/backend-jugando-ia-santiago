package cl.triskeledu.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    // Token JWT del usuario autenticado.
    private String token;

    // Tipo de autenticación.
    @Builder.Default
    private String tipoToken = "Bearer";

    // Duración del token en segundos.
    private Long expiraEn;

    // Datos del usuario autenticado.
    private UsuarioResponse usuario;
}