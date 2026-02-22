package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.service.LoteInventarioService;
import DrinkGo.DrinkGo_backend.service.MovimientoInventarioService;
import DrinkGo.DrinkGo_backend.service.StockInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    // ── Dev mode: sin JWT, negocio y usuario hardcodeados ──
    private static final Long NEGOCIO_ID = 1L;
    private static final Long USUARIO_ID = 1L;

    private final StockInventarioService stockService;
    private final LoteInventarioService loteService;
    private final MovimientoInventarioService movService;

    public InventarioController(StockInventarioService stockService,
                                 LoteInventarioService loteService,
                                 MovimientoInventarioService movService) {
        this.stockService = stockService;
        this.loteService = loteService;
        this.movService = movService;
    }

    /* ================================================================
     *  STOCK INVENTARIO
     * ================================================================ */

    @GetMapping("/stock")
    public ResponseEntity<List<StockInventarioResponse>> listarStock() {
        return ResponseEntity.ok(stockService.listarTodos(NEGOCIO_ID));
    }

    @GetMapping("/stock/{id}")
    public ResponseEntity<StockInventarioResponse> obtenerStock(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.obtenerPorId(id, NEGOCIO_ID));
    }

    @GetMapping("/stock/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<StockInventarioResponse> obtenerStockPorProductoAlmacen(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        return ResponseEntity.ok(stockService.obtenerPorProductoYAlmacen(productoId, almacenId, NEGOCIO_ID));
    }

    @GetMapping("/stock/almacen/{almacenId}")
    public ResponseEntity<List<StockInventarioResponse>> listarStockPorAlmacen(@PathVariable Long almacenId) {
        return ResponseEntity.ok(stockService.listarPorAlmacen(almacenId, NEGOCIO_ID));
    }

    @GetMapping("/stock/bajo")
    public ResponseEntity<List<StockInventarioResponse>> listarStockBajo(
            @RequestParam(defaultValue = "10") int umbral) {
        return ResponseEntity.ok(stockService.listarStockBajo(NEGOCIO_ID, umbral));
    }

    @PostMapping("/stock")
    public ResponseEntity<StockInventarioResponse> crearStock(@Valid @RequestBody StockInventarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.crear(req, NEGOCIO_ID));
    }

    @PutMapping("/stock/{id}")
    public ResponseEntity<StockInventarioResponse> actualizarStock(
            @PathVariable Long id, @Valid @RequestBody StockInventarioRequest req) {
        return ResponseEntity.ok(stockService.actualizar(id, req, NEGOCIO_ID));
    }

    @DeleteMapping("/stock/{id}")
    public ResponseEntity<Void> eliminarStock(@PathVariable Long id) {
        stockService.eliminar(id, NEGOCIO_ID);
        return ResponseEntity.noContent().build();
    }

    /* ================================================================
     *  LOTES INVENTARIO
     * ================================================================ */

    @GetMapping("/lotes")
    public ResponseEntity<List<LoteInventarioResponse>> listarLotes() {
        return ResponseEntity.ok(loteService.listarTodos(NEGOCIO_ID));
    }

    @GetMapping("/lotes/{id}")
    public ResponseEntity<LoteInventarioResponse> obtenerLote(@PathVariable Long id) {
        return ResponseEntity.ok(loteService.obtenerPorId(id, NEGOCIO_ID));
    }

    @GetMapping("/lotes/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<List<LoteInventarioResponse>> listarLotesPorProductoAlmacen(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        return ResponseEntity.ok(loteService.listarPorProductoYAlmacen(productoId, almacenId, NEGOCIO_ID));
    }

    @GetMapping("/lotes/disponibles/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<List<LoteInventarioResponse>> listarLotesDisponiblesFIFO(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        return ResponseEntity.ok(loteService.listarDisponiblesFIFO(productoId, almacenId, NEGOCIO_ID));
    }

    @GetMapping("/lotes/proximos-a-vencer")
    public ResponseEntity<List<LoteInventarioResponse>> listarProximosAVencer(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(loteService.listarProximosAVencer(NEGOCIO_ID, dias));
    }

    @PostMapping("/lotes")
    public ResponseEntity<LoteInventarioResponse> crearLote(@Valid @RequestBody LoteInventarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loteService.crear(req, NEGOCIO_ID));
    }

    @PutMapping("/lotes/{id}")
    public ResponseEntity<LoteInventarioResponse> actualizarLote(
            @PathVariable Long id, @Valid @RequestBody LoteInventarioRequest req) {
        return ResponseEntity.ok(loteService.actualizar(id, req, NEGOCIO_ID));
    }

    @DeleteMapping("/lotes/{id}")
    public ResponseEntity<Void> eliminarLote(@PathVariable Long id) {
        loteService.eliminar(id, NEGOCIO_ID);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lotes/marcar-vencidos")
    public ResponseEntity<Map<String, Object>> marcarVencidos() {
        int cantidad = loteService.marcarLotesVencidos(NEGOCIO_ID);
        return ResponseEntity.ok(Map.of("lotesActualizados", cantidad));
    }

    /* ================================================================
     *  MOVIMIENTOS INVENTARIO
     * ================================================================ */

    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarMovimientos() {
        return ResponseEntity.ok(movService.listarTodos(NEGOCIO_ID));
    }

    @GetMapping("/movimientos/{id}")
    public ResponseEntity<MovimientoInventarioResponse> obtenerMovimiento(@PathVariable Long id) {
        return ResponseEntity.ok(movService.obtenerPorId(id, NEGOCIO_ID));
    }

    @GetMapping("/movimientos/producto/{productoId}/almacen/{almacenId}")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarMovimientosPorProductoAlmacen(
            @PathVariable Long productoId, @PathVariable Long almacenId) {
        return ResponseEntity.ok(movService.listarPorProductoYAlmacen(productoId, almacenId, NEGOCIO_ID));
    }

    @GetMapping("/movimientos/tipo/{tipo}")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarMovimientosPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(movService.listarPorTipo(tipo, NEGOCIO_ID));
    }

    @GetMapping("/movimientos/rango")
    public ResponseEntity<List<MovimientoInventarioResponse>> listarMovimientosPorRango(
            @RequestParam LocalDateTime desde, @RequestParam LocalDateTime hasta) {
        return ResponseEntity.ok(movService.listarPorRangoFechas(NEGOCIO_ID, desde, hasta));
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoInventarioResponse> registrarMovimiento(
            @Valid @RequestBody MovimientoInventarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movService.registrar(req, NEGOCIO_ID, USUARIO_ID));
    }
}
