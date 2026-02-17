package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.service.StockInventarioService;
import DrinkGo.DrinkGo_backend.service.LoteInventarioService;
import DrinkGo.DrinkGo_backend.service.MovimientoInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de Inventario - Bloque 5.
 * Unificado: delega a Feature services (StockInventarioService, LoteInventarioService, MovimientoInventarioService).
 * CRUD completo: GET, POST, PUT, DELETE.
 * Implementa RF-INV-001 a RF-INV-009.
 * negocioId hardcoded = 1L (JWT eliminado para entorno de desarrollo).
 */
@RestController
@RequestMapping("/restful/inventario")
public class InventarioController {

    private static final Long NEGOCIO_ID = 1L;
    private static final Long USUARIO_ID = 1L;

    @Autowired
    private StockInventarioService stockService;

    @Autowired
    private LoteInventarioService loteService;

    @Autowired
    private MovimientoInventarioService movimientoService;

    // ============================================================
    // STOCK (RF-INV-001) — GET, POST, PUT, DELETE
    // ============================================================

    /** POST /restful/inventario/stock — Crear registro de stock */
    @PostMapping("/stock")
    public ResponseEntity<?> crearStock(@RequestBody StockInventarioRequest request) {
        try {
            StockInventarioResponse stock = stockService.crear(request, NEGOCIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Stock creado exitosamente");
            response.put("stock", stock);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario — Listar todo el stock del negocio */
    @GetMapping
    public ResponseEntity<?> listarStock() {
        try {
            List<StockInventarioResponse> stock = stockService.listar(NEGOCIO_ID);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/producto/{productoId} — Stock de un producto en todos los almacenes */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> listarStockProducto(@PathVariable Long productoId) {
        try {
            List<StockInventarioResponse> stock = stockService.listarPorProducto(NEGOCIO_ID, productoId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/producto/{productoId}/almacen/{almacenId} — Stock específico */
    @GetMapping("/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<?> obtenerStockProductoAlmacen(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        try {
            StockInventarioResponse stock = stockService.obtenerPorProductoAlmacen(NEGOCIO_ID, productoId, almacenId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/almacen/{almacenId} — Stock de un almacén */
    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<?> listarStockAlmacen(@PathVariable Long almacenId) {
        try {
            List<StockInventarioResponse> stock = stockService.listarPorAlmacen(NEGOCIO_ID, almacenId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/stock/{id} — Obtener stock por ID */
    @GetMapping("/stock/{id}")
    public ResponseEntity<?> obtenerStock(@PathVariable Long id) {
        try {
            StockInventarioResponse stock = stockService.obtenerPorId(id, NEGOCIO_ID);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** PUT /restful/inventario/stock/{id} — Actualizar stock */
    @PutMapping("/stock/{id}")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestBody StockInventarioRequest request) {
        try {
            StockInventarioResponse stock = stockService.actualizar(id, request, NEGOCIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Stock actualizado exitosamente");
            response.put("stock", stock);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** DELETE /restful/inventario/stock/{id} — Eliminar stock */
    @DeleteMapping("/stock/{id}")
    public ResponseEntity<?> eliminarStock(@PathVariable Long id) {
        try {
            stockService.eliminar(id, NEGOCIO_ID);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Stock eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // LOTES (RF-INV-002, RF-INV-003) — GET, POST, PUT, DELETE
    // ============================================================

    /** POST /restful/inventario/lotes — Registrar entrada de lote */
    @PostMapping("/lotes")
    public ResponseEntity<?> registrarEntradaLote(@RequestBody LoteInventarioRequest request) {
        try {
            LoteInventarioResponse lote = loteService.crear(request, NEGOCIO_ID, USUARIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Lote registrado exitosamente");
            response.put("lote", lote);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/lotes — Listar todos los lotes */
    @GetMapping("/lotes")
    public ResponseEntity<?> listarLotes() {
        try {
            List<LoteInventarioResponse> lotes = loteService.listar(NEGOCIO_ID);
            return ResponseEntity.ok(lotes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/lotes/{id} — Obtener lote por ID */
    @GetMapping("/lotes/{id}")
    public ResponseEntity<?> obtenerLote(@PathVariable Long id) {
        try {
            LoteInventarioResponse lote = loteService.obtenerPorId(id, NEGOCIO_ID);
            return ResponseEntity.ok(lote);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/lotes/producto/{productoId} — Lotes de un producto (FIFO) */
    @GetMapping("/lotes/producto/{productoId}")
    public ResponseEntity<?> listarLotesProducto(@PathVariable Long productoId) {
        try {
            List<LoteInventarioResponse> lotes = loteService.listarPorProducto(NEGOCIO_ID, productoId);
            return ResponseEntity.ok(lotes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/lotes/producto/{productoId}/almacen/{almacenId} — Lotes producto+almacén */
    @GetMapping("/lotes/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<?> listarLotesProductoAlmacen(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        try {
            List<LoteInventarioResponse> lotes = loteService.listarPorProductoAlmacen(NEGOCIO_ID, productoId, almacenId);
            return ResponseEntity.ok(lotes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** PUT /restful/inventario/lotes/{id} — Actualizar lote */
    @PutMapping("/lotes/{id}")
    public ResponseEntity<?> actualizarLote(@PathVariable Long id, @RequestBody LoteInventarioRequest request) {
        try {
            LoteInventarioResponse lote = loteService.actualizar(id, request, NEGOCIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Lote actualizado exitosamente");
            response.put("lote", lote);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** DELETE /restful/inventario/lotes/{id} — Eliminar lote (borrado lógico: estado→agotado) */
    @DeleteMapping("/lotes/{id}")
    public ResponseEntity<?> eliminarLote(@PathVariable Long id) {
        try {
            loteService.eliminar(id, NEGOCIO_ID);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Lote eliminado exitosamente (borrado lógico: estado → agotado)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MOVIMIENTOS (RF-INV-006) — GET, POST (inmutables: sin PUT/DELETE)
    // ============================================================

    /** POST /restful/inventario/movimiento — Registrar movimiento */
    @PostMapping("/movimiento")
    public ResponseEntity<?> registrarMovimiento(@RequestBody MovimientoInventarioRequest request) {
        try {
            MovimientoInventarioResponse movimiento = movimientoService.crear(request, NEGOCIO_ID, USUARIO_ID);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Movimiento registrado exitosamente");
            response.put("movimiento", movimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/movimientos — Listar todos los movimientos */
    @GetMapping("/movimientos")
    public ResponseEntity<?> listarMovimientos() {
        try {
            List<MovimientoInventarioResponse> movimientos = movimientoService.listar(NEGOCIO_ID);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/movimientos/{id} — Obtener movimiento por ID */
    @GetMapping("/movimientos/{id}")
    public ResponseEntity<?> obtenerMovimiento(@PathVariable Long id) {
        try {
            MovimientoInventarioResponse movimiento = movimientoService.obtenerPorId(id, NEGOCIO_ID);
            return ResponseEntity.ok(movimiento);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/movimientos/producto/{productoId} — Movimientos de un producto */
    @GetMapping("/movimientos/producto/{productoId}")
    public ResponseEntity<?> listarMovimientosProducto(@PathVariable Long productoId) {
        try {
            List<MovimientoInventarioResponse> movimientos = movimientoService.listarPorProducto(NEGOCIO_ID, productoId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /** GET /restful/inventario/movimientos/almacen/{almacenId} — Movimientos de un almacén */
    @GetMapping("/movimientos/almacen/{almacenId}")
    public ResponseEntity<?> listarMovimientosAlmacen(@PathVariable Long almacenId) {
        try {
            List<MovimientoInventarioResponse> movimientos = movimientoService.listarPorAlmacen(NEGOCIO_ID, almacenId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MÉTODO AUXILIAR
    // ============================================================

    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
