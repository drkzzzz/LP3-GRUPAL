package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.LoteFifoDTO;
import DrinkGo.DrinkGo_backend.dto.ProductoProximoVencerDTO;
import DrinkGo.DrinkGo_backend.service.DatabaseFunctionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para las funciones de V2__database_functions.sql
 * 
 * Endpoints:
 * - POST /api/inventario/fifo/seleccionar - Selección FIFO de lotes
 * - POST /api/inventario/fifo/descontar - Descuento FIFO
 * - GET /api/inventario/proximos-vencer - Productos por vencer
 * - POST /api/inventario/combos/descontar - Descuento de combos
 * - GET /api/ventas/validar-alcohol/{sedeId} - Validar horario alcohol
 * - POST /api/inventario/marcar-vencidos - Job de vencimientos
 */
@RestController
@RequestMapping("/api")
public class DatabaseFunctionsController {
    
    private final DatabaseFunctionsService functionsService;
    
    public DatabaseFunctionsController(DatabaseFunctionsService functionsService) {
        this.functionsService = functionsService;
    }
    
    // =========================================================================
    // FIFO - Selección de lotes
    // =========================================================================
    
    /**
     * POST /api/inventario/fifo/seleccionar
     * Obtiene los lotes a descontar siguiendo FIFO
     * 
     * Body: { "productoId": 1, "sedeId": 1, "cantidad": 10.5 }
     */
    @PostMapping("/inventario/fifo/seleccionar")
    public ResponseEntity<List<LoteFifoDTO>> seleccionarLotesFifo(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Long sedeId = Long.valueOf(request.get("sedeId").toString());
        BigDecimal cantidad = new BigDecimal(request.get("cantidad").toString());
        
        List<LoteFifoDTO> lotes = functionsService.seleccionarLotesFifo(productoId, sedeId, cantidad);
        return ResponseEntity.ok(lotes);
    }
    
    // =========================================================================
    // FIFO - Descuento de inventario
    // =========================================================================
    
    /**
     * POST /api/inventario/fifo/descontar
     * Descuenta inventario usando FIFO automático
     * 
     * Body: { "productoId": 1, "sedeId": 1, "cantidad": 5.0, "referenciaTipo": "venta", "referenciaId": 123 }
     */
    @PostMapping("/inventario/fifo/descontar")
    public ResponseEntity<Map<String, String>> descontarInventarioFifo(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Long sedeId = Long.valueOf(request.get("sedeId").toString());
        BigDecimal cantidad = new BigDecimal(request.get("cantidad").toString());
        String referenciaTipo = (String) request.get("referenciaTipo");
        Long referenciaId = Long.valueOf(request.get("referenciaId").toString());
        
        functionsService.descontarInventarioFifo(productoId, sedeId, cantidad, referenciaTipo, referenciaId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Inventario descontado exitosamente usando FIFO");
        response.put("productoId", productoId.toString());
        response.put("cantidad", cantidad.toString());
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/inventario/fifo/descontar-venta
     * Atajo para descuento por venta
     * 
     * Body: { "productoId": 1, "sedeId": 1, "cantidad": 2.0, "ventaId": 456 }
     */
    @PostMapping("/inventario/fifo/descontar-venta")
    public ResponseEntity<Map<String, String>> descontarPorVenta(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Long sedeId = Long.valueOf(request.get("sedeId").toString());
        BigDecimal cantidad = new BigDecimal(request.get("cantidad").toString());
        Long ventaId = Long.valueOf(request.get("ventaId").toString());
        
        functionsService.descontarPorVenta(productoId, sedeId, cantidad, ventaId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Inventario descontado por venta");
        response.put("ventaId", ventaId.toString());
        return ResponseEntity.ok(response);
    }
    
    // =========================================================================
    // Alertas de vencimiento
    // =========================================================================
    
    /**
     * GET /api/inventario/proximos-vencer
     * Lista productos próximos a vencer
     * 
     * Params: tenantId (required), sedeId (optional), dias (optional, default 30)
     */
    @GetMapping("/inventario/proximos-vencer")
    public ResponseEntity<List<ProductoProximoVencerDTO>> obtenerProductosProximosVencer(
            @RequestParam Long tenantId,
            @RequestParam(required = false) Long sedeId,
            @RequestParam(defaultValue = "30") int dias) {
        
        List<ProductoProximoVencerDTO> productos = functionsService.obtenerProductosProximosVencer(tenantId, sedeId, dias);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * GET /api/inventario/criticos
     * Lista productos críticos (7 días o menos para vencer)
     */
    @GetMapping("/inventario/criticos")
    public ResponseEntity<List<ProductoProximoVencerDTO>> obtenerProductosCriticos(@RequestParam Long tenantId) {
        List<ProductoProximoVencerDTO> productos = functionsService.obtenerProductosCriticos(tenantId);
        return ResponseEntity.ok(productos);
    }
    
    // =========================================================================
    // Combos y packs
    // =========================================================================
    
    /**
     * POST /api/inventario/combos/descontar
     * Descuenta todos los componentes de un combo atómicamente
     * 
     * Body: { "comboId": 1, "sedeId": 1, "cantidadCombos": 2, "referenciaTipo": "venta", "referenciaId": 789 }
     */
    @PostMapping("/inventario/combos/descontar")
    public ResponseEntity<Map<String, String>> descontarComboInventario(@RequestBody Map<String, Object> request) {
        Long comboId = Long.valueOf(request.get("comboId").toString());
        Long sedeId = Long.valueOf(request.get("sedeId").toString());
        int cantidadCombos = Integer.parseInt(request.get("cantidadCombos").toString());
        String referenciaTipo = (String) request.get("referenciaTipo");
        Long referenciaId = Long.valueOf(request.get("referenciaId").toString());
        
        functionsService.descontarComboInventario(comboId, sedeId, cantidadCombos, referenciaTipo, referenciaId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Combo descontado exitosamente");
        response.put("comboId", comboId.toString());
        response.put("cantidadCombos", String.valueOf(cantidadCombos));
        return ResponseEntity.ok(response);
    }
    
    // =========================================================================
    // Validación de horarios de venta de alcohol
    // =========================================================================
    
    /**
     * GET /api/ventas/validar-alcohol/{sedeId}
     * Valida si se puede vender alcohol ahora
     */
    @GetMapping("/ventas/validar-alcohol/{sedeId}")
    public ResponseEntity<Map<String, Object>> validarHorarioVentaAlcohol(@PathVariable Long sedeId) {
        boolean permitido = functionsService.validarHorarioVentaAlcohol(sedeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sedeId", sedeId);
        response.put("ventaAlcoholPermitida", permitido);
        response.put("horaConsulta", LocalTime.now().toString());
        
        if (!permitido) {
            response.put("mensaje", "La venta de alcohol no está permitida en este horario");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // =========================================================================
    // Jobs programados
    // =========================================================================
    
    /**
     * POST /api/inventario/marcar-vencidos
     * Ejecuta job para marcar lotes vencidos (para cron job o ejecución manual)
     */
    @PostMapping("/inventario/marcar-vencidos")
    public ResponseEntity<Map<String, Object>> marcarLotesVencidos() {
        int cantidad = functionsService.marcarLotesVencidos();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Job de vencimientos ejecutado");
        response.put("lotesAfectados", cantidad);
        
        return ResponseEntity.ok(response);
    }
}
