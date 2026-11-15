package cl.triskeledu.backend.controller;

import cl.triskeledu.backend.dto.LoginRequest;
import cl.triskeledu.backend.dto.PerfilUpdateRequest;
import cl.triskeledu.backend.dto.RegistroRequest;
import cl.triskeledu.backend.dto.UsuarioResponse;
import cl.triskeledu.backend.model.Rol;
import cl.triskeledu.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        UsuarioResponse nuevo = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest request) {
        UsuarioResponse auth = usuarioService.login(request);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos(@RequestParam(required = false) Rol rol) {
        if (rol != null) {
            return ResponseEntity.ok(usuarioService.obtenerPorRol(rol));
        }
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody PerfilUpdateRequest request
    ) {
        UsuarioResponse actualizado = usuarioService.actualizarPerfil(id, request);
        return ResponseEntity.ok(actualizado);
    }
}
