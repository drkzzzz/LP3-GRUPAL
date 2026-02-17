package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.UsuarioPlataformaRequest;
import DrinkGo.DrinkGo_backend.dto.UsuarioPlataformaResponse;
import DrinkGo.DrinkGo_backend.service.UsuarioPlataformaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Usuarios de Plataforma (SuperAdmin y Soporte)
 */
@RestController
@RequestMapping("/api/usuarios-plataforma")
@CrossOrigin(origins = "*")
public class UsuarioPlataformaController {

    @Autowired
    private UsuarioPlataformaService usuarioPlataformaService;

    @GetMapping
    public ResponseEntity<List<UsuarioPlataformaResponse>> obtenerTodosLosUsuarios() {
        return ResponseEntity.ok(usuarioPlataformaService.obtenerTodosLosUsuarios());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioPlataformaResponse>> obtenerUsuariosActivos() {
        return ResponseEntity.ok(usuarioPlataformaService.obtenerUsuariosActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPlataformaResponse> obtenerUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioPlataformaService.obtenerUsuarioPorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioPlataformaResponse> obtenerUsuarioPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioPlataformaService.obtenerUsuarioPorEmail(email));
    }

    @PostMapping
    public ResponseEntity<UsuarioPlataformaResponse> crearUsuarioPlataforma(@RequestBody UsuarioPlataformaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioPlataformaService.crearUsuarioPlataforma(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioPlataformaResponse> actualizarUsuarioPlataforma(
            @PathVariable Long id,
            @RequestBody UsuarioPlataformaRequest request) {
        return ResponseEntity.ok(usuarioPlataformaService.actualizarUsuarioPlataforma(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuarioPlataforma(@PathVariable Long id) {
        usuarioPlataformaService.eliminarUsuarioPlataforma(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/registrar-acceso")
    public ResponseEntity<Void> registrarAcceso(
            @PathVariable Long id,
            @RequestParam String ipAddress) {
        usuarioPlataformaService.registrarAcceso(id, ipAddress);
        return ResponseEntity.ok().build();
    }
}
