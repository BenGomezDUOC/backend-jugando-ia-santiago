package cl.triskeledu.clientes.controller;

import cl.triskeledu.clientes.dto.LoginRequest;
import cl.triskeledu.clientes.dto.LoginResponse;
import cl.triskeledu.clientes.dto.PerfilUpdateRequest;
import cl.triskeledu.clientes.dto.RegistroRequest;
import cl.triskeledu.clientes.dto.UsuarioResponse;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({
        "/api/clientes/usuarios",
        "/api/usuarios"
})
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Constructor del controlador.
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --------------------------------------------------
    // REGISTRAR CLIENTE
    // --------------------------------------------------

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistroRequest request
    ) {

        UsuarioResponse nuevo =
                usuarioService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevo);
    }

    // --------------------------------------------------
    // INICIAR SESIÓN CON JWT
    // --------------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse respuesta =
                usuarioService.login(request);

        return ResponseEntity.ok(respuesta);
    }

    // --------------------------------------------------
    // CONSULTAR USUARIO POR ID
    // --------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @PathVariable("id") Long id
    ) {

        validarId(id);

        return ResponseEntity.ok(
                usuarioService.obtenerPorId(id)
        );
    }

    // --------------------------------------------------
    // CONSULTAR USUARIOS
    // --------------------------------------------------

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos(
            @RequestParam(name = "rol", required = false)
            Rol rol
    ) {

        // Filtrar usuarios según su rol.
        if (rol != null) {

            return ResponseEntity.ok(
                    usuarioService.obtenerPorRol(rol)
            );
        }

        return ResponseEntity.ok(
                usuarioService.obtenerTodos()
        );
    }

    // --------------------------------------------------
    // ACTUALIZAR PERFIL
    // --------------------------------------------------

    @PutMapping("/{id}/perfil")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @PathVariable("id") Long id,
            @Valid @RequestBody PerfilUpdateRequest request
    ) {

        validarId(id);

        UsuarioResponse actualizado =
                usuarioService.actualizarPerfil(id, request);

        return ResponseEntity.ok(actualizado);
    }

    // --------------------------------------------------
    // VALIDAR IDENTIFICADOR
    // --------------------------------------------------

    private void validarId(Long id) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "El identificador del usuario no es válido"
            );
        }
    }
}