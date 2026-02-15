package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.AlertaInventarioResponse;
import DrinkGo.DrinkGo_backend.security.UsuarioAutenticado;
import DrinkGo.DrinkGo_backend.service.AlertaInventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Alertas de Inventario.
 * Base path: /restful/alertas
 * Las alertas se generan automáticamente. Solo se pueden listar y resolver.
 */
@RestController
@RequestMapping("/restful/alertas")
public class AlertaInventarioController {

    private final AlertaInventarioService alertaService;

    public AlertaInventarioController(AlertaInventarioService alertaService) {
        this.alertaService = alertaService;
    }

    /** GET /restful/alertas - Listar todas las alertas del negocio */
    @GetMapping
    public ResponseEntity<List<AlertaInventarioResponse>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(alertaService.listar(negocioId));
    }

    /** GET /restful/alertas/activas - Listar solo alertas activas (no resueltas) */
    @GetMapping("/activas")
    public ResponseEntity<List<AlertaInventarioResponse>> listarActivas() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(alertaService.listarActivas(negocioId));
    }

    /** GET /restful/alertas/{id} - Obtener alerta por ID */
    @GetMapping("/{id}")
    public ResponseEntity<AlertaInventarioResponse> obtenerPorId(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(alertaService.obtenerPorId(id, negocioId));
    }

    /** PUT /restful/alertas/{id}/resolver - Resolver alerta (borrado lógico: esta_resuelta=true) */
    @PutMapping("/{id}/resolver")
    public ResponseEntity<Void> resolver(@PathVariable Long id) {
        UsuarioAutenticado usuario = obtenerUsuario();
        alertaService.resolver(id, usuario.getNegocioId(), usuario.getUsuarioId());
        return ResponseEntity.noContent().build();
    }

    // ── Métodos auxiliares ──

    private Long obtenerNegocioId() {
        return obtenerUsuario().getNegocioId();
    }

    private UsuarioAutenticado obtenerUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UsuarioAutenticado) auth.getPrincipal();
    }
}
