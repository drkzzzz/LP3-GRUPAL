package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.LoteInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.LoteInventarioResponse;
import DrinkGo.DrinkGo_backend.service.LoteInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Long negocioId = obtenerNegocioId();
        Long usuarioId = 1L; // Sin seguridad: valor por defecto para pruebas
        LoteInventarioResponse response = loteService.crear(request, negocioId, usuarioId);
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

    // ── Método auxiliar (sin seguridad: valor por defecto para pruebas) ──

    private Long obtenerNegocioId() {
        return 1L;
    }
}
