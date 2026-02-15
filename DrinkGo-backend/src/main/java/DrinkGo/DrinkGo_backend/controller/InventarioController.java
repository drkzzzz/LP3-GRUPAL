package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.LoteEntradaRequest;
import DrinkGo.DrinkGo_backend.dto.LoteUpdateRequest;
import DrinkGo.DrinkGo_backend.dto.MovimientoRequest;
import DrinkGo.DrinkGo_backend.dto.MovimientoUpdateRequest;
import DrinkGo.DrinkGo_backend.dto.StockCreateRequest;
import DrinkGo.DrinkGo_backend.dto.StockUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.service.InventarioService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de Inventario - Bloque 5.
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, POST, PUT, DELETE (borrado lógico).
 * Implementa RF-INV-001 a RF-INV-009.
 */
@RestController
@RequestMapping("/restful/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // STOCK (RF-INV-001) — GET, POST, PUT, DELETE
    // ============================================================

    /**
     * POST /restful/inventario/stock
     * Crear un registro de stock manualmente.
     */
    @PostMapping("/stock")
    public ResponseEntity<?> crearStock(@RequestBody StockCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            StockInventario stock = inventarioService.crearStock(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Stock creado exitosamente");
            response.put("stock", stock);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario
     * Listar todo el stock del negocio autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarStock() {
        try {
            Long negocioId = obtenerNegocioId();
            List<StockInventario> stock = inventarioService.listarStock(negocioId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/producto/{productoId}
     * Listar stock de un producto en todos los almacenes.
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> listarStockProducto(@PathVariable Long productoId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<StockInventario> stock = inventarioService.listarStockProducto(negocioId, productoId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/producto/{productoId}/almacen/{almacenId}
     * Obtener stock de un producto en un almacén específico.
     */
    @GetMapping("/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<?> obtenerStockProductoAlmacen(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        try {
            Long negocioId = obtenerNegocioId();
            StockInventario stock = inventarioService.obtenerStockProductoAlmacen(negocioId, productoId, almacenId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/almacen/{almacenId}
     * Listar stock de un almacén.
     */
    @GetMapping("/almacen/{almacenId}")
    public ResponseEntity<?> listarStockAlmacen(@PathVariable Long almacenId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<StockInventario> stock = inventarioService.listarStockAlmacen(negocioId, almacenId);
            return ResponseEntity.ok(stock);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/inventario/stock/{id}
     * Actualizar stock manualmente.
     */
    @PutMapping("/stock/{id}")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestBody StockUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            StockInventario stock = inventarioService.actualizarStock(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Stock actualizado exitosamente");
            response.put("stock", stock);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/inventario/stock/{id}
     * Eliminar stock (borrado lógico).
     */
    @DeleteMapping("/stock/{id}")
    public ResponseEntity<?> eliminarStock(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            inventarioService.eliminarStock(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Stock eliminado exitosamente (borrado lógico)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // LOTES (RF-INV-002, RF-INV-003) — GET, POST, PUT, DELETE
    // ============================================================

    /**
     * POST /restful/inventario/lotes
     * Registrar entrada de un nuevo lote de inventario.
     */
    @PostMapping("/lotes")
    public ResponseEntity<?> registrarEntradaLote(@RequestBody LoteEntradaRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            LoteInventario lote = inventarioService.registrarEntradaLote(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Lote registrado exitosamente");
            response.put("lote", lote);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/lotes
     * Listar todos los lotes del negocio.
     */
    @GetMapping("/lotes")
    public ResponseEntity<?> listarLotes() {
        try {
            Long negocioId = obtenerNegocioId();
            List<LoteInventario> lotes = inventarioService.listarLotes(negocioId);
            return ResponseEntity.ok(lotes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/lotes/producto/{productoId}
     * Listar lotes de un producto (orden FIFO).
     */
    @GetMapping("/lotes/producto/{productoId}")
    public ResponseEntity<?> listarLotesProducto(@PathVariable Long productoId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<LoteInventario> lotes = inventarioService.listarLotesProducto(negocioId, productoId);
            return ResponseEntity.ok(lotes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/lotes/producto/{productoId}/almacen/{almacenId}
     * Listar lotes de un producto en un almacén específico.
     */
    @GetMapping("/lotes/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<?> listarLotesProductoAlmacen(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<LoteInventario> lotes = inventarioService.listarLotesProductoAlmacen(
                    negocioId, productoId, almacenId);
            return ResponseEntity.ok(lotes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/inventario/lotes/{id}
     * Actualizar un lote de inventario.
     */
    @PutMapping("/lotes/{id}")
    public ResponseEntity<?> actualizarLote(@PathVariable Long id, @RequestBody LoteUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            LoteInventario lote = inventarioService.actualizarLote(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Lote actualizado exitosamente");
            response.put("lote", lote);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/inventario/lotes/{id}
     * Eliminar lote (borrado lógico).
     */
    @DeleteMapping("/lotes/{id}")
    public ResponseEntity<?> eliminarLote(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            inventarioService.eliminarLote(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Lote eliminado exitosamente (borrado lógico)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MOVIMIENTOS (RF-INV-006) — GET, POST, PUT, DELETE
    // ============================================================

    /**
     * POST /restful/inventario/movimiento
     * Registrar un movimiento de inventario (ajuste, merma, rotura, etc.).
     */
    @PostMapping("/movimiento")
    public ResponseEntity<?> registrarMovimiento(@RequestBody MovimientoRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            MovimientoInventario movimiento = inventarioService.registrarMovimiento(
                    negocioId, request, null);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Movimiento registrado exitosamente");
            response.put("movimiento", movimiento);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/movimientos
     * Listar todos los movimientos del negocio.
     */
    @GetMapping("/movimientos")
    public ResponseEntity<?> listarMovimientos() {
        try {
            Long negocioId = obtenerNegocioId();
            List<MovimientoInventario> movimientos = inventarioService.listarMovimientos(negocioId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/movimientos/producto/{productoId}
     * Listar movimientos de un producto (trazabilidad RF-INV-008).
     */
    @GetMapping("/movimientos/producto/{productoId}")
    public ResponseEntity<?> listarMovimientosProducto(@PathVariable Long productoId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<MovimientoInventario> movimientos = inventarioService.listarMovimientosProducto(
                    negocioId, productoId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/inventario/movimientos/almacen/{almacenId}
     * Listar movimientos de un almacén.
     */
    @GetMapping("/movimientos/almacen/{almacenId}")
    public ResponseEntity<?> listarMovimientosAlmacen(@PathVariable Long almacenId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<MovimientoInventario> movimientos = inventarioService.listarMovimientosAlmacen(
                    negocioId, almacenId);
            return ResponseEntity.ok(movimientos);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/inventario/movimientos/{id}
     * Actualizar un movimiento de inventario.
     */
    @PutMapping("/movimientos/{id}")
    public ResponseEntity<?> actualizarMovimiento(@PathVariable Long id,
                                                   @RequestBody MovimientoUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            MovimientoInventario movimiento = inventarioService.actualizarMovimiento(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Movimiento actualizado exitosamente");
            response.put("movimiento", movimiento);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/inventario/movimientos/{id}
     * Eliminar movimiento (borrado lógico).
     */
    @DeleteMapping("/movimientos/{id}")
    public ResponseEntity<?> eliminarMovimiento(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            inventarioService.eliminarMovimiento(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Movimiento eliminado exitosamente (borrado lógico)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    /**
     * Obtener el negocio_id del tenant autenticado.
     * El UUID se extrae desde SecurityContextHolder (nunca del request).
     */
    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No se pudo obtener la autenticación del contexto de seguridad");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
