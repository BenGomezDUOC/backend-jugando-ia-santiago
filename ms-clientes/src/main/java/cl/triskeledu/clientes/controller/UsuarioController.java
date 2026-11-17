package cl.triskeledu.clientes.controller;

import cl.triskeledu.clientes.dto.LoginRequest;
import cl.triskeledu.clientes.dto.PerfilUpdateRequest;
import cl.triskeledu.clientes.dto.RegistroRequest;
import cl.triskeledu.clientes.dto.UsuarioResponse;
import cl.triskeledu.clientes.model.Rol;
import cl.triskeledu.clientes.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/clientes/usuarios", "/api/usuarios"})
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

    @PutMapping("/{id}/sumar-puntos")
    public ResponseEntity<UsuarioResponse> sumarPuntos(
            @PathVariable Long id,
            @RequestParam int puntos
    ) {
        UsuarioResponse actualizado = usuarioService.sumarPuntos(id, puntos);
        return ResponseEntity.ok(actualizado);
    }
}
