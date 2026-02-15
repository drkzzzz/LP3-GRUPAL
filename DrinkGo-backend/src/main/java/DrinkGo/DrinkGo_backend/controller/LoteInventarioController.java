package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.LoteInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.LoteInventarioResponse;
import DrinkGo.DrinkGo_backend.security.UsuarioAutenticado;
import DrinkGo.DrinkGo_backend.service.LoteInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Lotes de Inventario.
 * Base path: /restful/lotes
 * Implementa FIFO estricto en las operaciones de consumo.
 */
@RestController
@RequestMapping("/restful/lotes")
public class LoteInventarioController {

    private final LoteInventarioService loteService;

    public LoteInventarioController(LoteInventarioService loteService) {
        this.loteService = loteService;
    }

    /** GET /restful/lotes - Listar todos los lotes del negocio */
    @GetMapping
    public ResponseEntity<List<LoteInventarioResponse>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(loteService.listar(negocioId));
    }

    /** GET /restful/lotes/{id} - Obtener lote por ID */
    @GetMapping("/{id}")
    public ResponseEntity<LoteInventarioResponse> obtenerPorId(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(loteService.obtenerPorId(id, negocioId));
    }

    /** POST /restful/lotes - Registrar entrada de lote (RF-INV-002) */
    @PostMapping
    public ResponseEntity<LoteInventarioResponse> crear(@Valid @RequestBody LoteInventarioRequest request) {
        UsuarioAutenticado usuario = obtenerUsuario();
        LoteInventarioResponse response = loteService.crear(request, usuario.getNegocioId(), usuario.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** PUT /restful/lotes/{id} - Actualizar datos del lote */
    @PutMapping("/{id}")
    public ResponseEntity<LoteInventarioResponse> actualizar(@PathVariable Long id,
                                                              @Valid @RequestBody LoteInventarioRequest request) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(loteService.actualizar(id, request, negocioId));
    }

    /** DELETE /restful/lotes/{id} - Borrado lógico (estado → 'agotado') */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        loteService.eliminar(id, negocioId);
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
