package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.ConfiguracionNegocio;
import DrinkGo.DrinkGo_backend.service.ConfiguracionNegocioService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Configuración de Negocio
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/configuracion-negocio")
public class ConfiguracionNegocioController {

    private final ConfiguracionNegocioService configuracionService;
    private final UsuarioService usuarioService;

    public ConfiguracionNegocioController(ConfiguracionNegocioService configuracionService,
            UsuarioService usuarioService) {
        this.configuracionService = configuracionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<ConfiguracionNegocio>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(configuracionService.findByNegocio(negocioId));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ConfiguracionNegocio>> listarPorCategoria(@PathVariable String categoria) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(configuracionService.findByNegocioYCategoria(negocioId, categoria));
    }

    @GetMapping("/clave/{clave}")
    public ResponseEntity<ConfiguracionNegocio> obtenerPorClave(@PathVariable String clave) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(
                configuracionService.findByClave(negocioId, clave)
                        .orElseThrow(() -> new RuntimeException("Configuración no encontrada")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfiguracionNegocio> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(configuracionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ConfiguracionNegocio> crear(@RequestBody ConfiguracionNegocio configuracion) {
        Long negocioId = obtenerNegocioId();
        configuracion.setNegocioId(negocioId);
        return ResponseEntity.ok(configuracionService.crear(configuracion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionNegocio> actualizar(@PathVariable Long id,
            @RequestBody ConfiguracionNegocio configuracion) {
        verificarAutenticacion();
        return ResponseEntity.ok(configuracionService.actualizar(id, configuracion));
    }

    @PutMapping("/guardar")
    public ResponseEntity<ConfiguracionNegocio> guardarOActualizar(@RequestParam String clave,
            @RequestParam String valor) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(configuracionService.guardarOActualizar(negocioId, clave, valor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        configuracionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    private void verificarAutenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
    }
}
