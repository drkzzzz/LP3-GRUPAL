package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.StockInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.StockInventarioResponse;
import DrinkGo.DrinkGo_backend.security.UsuarioAutenticado;
import DrinkGo.DrinkGo_backend.service.StockInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Stock de Inventario.
 * Base path: /restful/stock
 * Todos los endpoints requieren JWT válido.
 * negocio_id se obtiene del token, nunca del JSON.
 */
@RestController
@RequestMapping("/restful/stock")
public class StockInventarioController {

    private final StockInventarioService stockService;

    public StockInventarioController(StockInventarioService stockService) {
        this.stockService = stockService;
    }

    /** GET /restful/stock - Listar todo el stock del negocio */
    @GetMapping
    public ResponseEntity<List<StockInventarioResponse>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(stockService.listar(negocioId));
    }

    /** GET /restful/stock/{id} - Obtener stock por ID */
    @GetMapping("/{id}")
    public ResponseEntity<StockInventarioResponse> obtenerPorId(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(stockService.obtenerPorId(id, negocioId));
    }

    /** POST /restful/stock - Crear registro de stock */
    @PostMapping
    public ResponseEntity<StockInventarioResponse> crear(@Valid @RequestBody StockInventarioRequest request) {
        Long negocioId = obtenerNegocioId();
        StockInventarioResponse response = stockService.crear(request, negocioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** PUT /restful/stock/{id} - Actualizar stock */
    @PutMapping("/{id}")
    public ResponseEntity<StockInventarioResponse> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody StockInventarioRequest request) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(stockService.actualizar(id, request, negocioId));
    }

    // ── Método auxiliar ──

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsuarioAutenticado usuario = (UsuarioAutenticado) auth.getPrincipal();
        return usuario.getNegocioId();
    }
}
