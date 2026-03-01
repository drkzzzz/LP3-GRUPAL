package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.LotesInventario;
import DrinkGo.DrinkGo_backend.service.ILotesInventarioService;
import DrinkGo.DrinkGo_backend.service.InventarioTransaccionalService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Controlador REST para gestión de lotes de inventario
 * Usar el servicio transaccional para operaciones que afecten stock
 */
@RestController
@RequestMapping("/restful")
public class LotesInventarioController {
    
    @Autowired
    private ILotesInventarioService service;

    @Autowired
    private InventarioTransaccionalService inventarioService;

    @GetMapping("/lotes-inventario")
    public List<LotesInventario> buscarTodos() {
        return service.buscarTodos();
    }

    /**
     * Crea un lote con sincronización automática de stock, CPP y movimientos
     * Usar este endpoint para registrar entradas de inventario
     */
    @PostMapping("/lotes-inventario")
    public ResponseEntity<?> guardar(@RequestBody Map<String, Object> request) {
        try {
            // Extraer datos del request
            Long negocioId = getLong(request, "negocio");
            Long productoId = getLong(request, "producto");
            Long almacenId = getLong(request, "almacen");
            String numeroLote = (String) request.get("numeroLote");
            Double cantidadDouble = getDouble(request, "cantidadInicial");
            Double costoDouble = getDouble(request, "costoUnitario");
            String fechaVencimientoStr = (String) request.get("fechaVencimiento");
            Long usuarioId = getLong(request, "usuario");

            // Validaciones básicas
            if (negocioId == null || productoId == null || almacenId == null) {
                return ResponseEntity.badRequest().body("Negocio, Producto y Almacén son obligatorios");
            }

            // Usar usuario por defecto (ID 1) si no se proporciona
            if (usuarioId == null) {
                usuarioId = 1L;
            }

            java.math.BigDecimal cantidad = java.math.BigDecimal.valueOf(cantidadDouble != null ? cantidadDouble : 0);
            java.math.BigDecimal costoUnitario = java.math.BigDecimal.valueOf(costoDouble != null ? costoDouble : 0);
            
            java.time.LocalDate fechaVencimiento = null;
            if (fechaVencimientoStr != null && !fechaVencimientoStr.isEmpty()) {
                fechaVencimiento = java.time.LocalDate.parse(fechaVencimientoStr);
            }

            // Usar servicio transaccional que sincroniza automáticamente
            LotesInventario lote = inventarioService.registrarEntrada(
                negocioId,
                productoId,
                almacenId,
                numeroLote,
                cantidad,
                costoUnitario,
                fechaVencimiento,
                usuarioId,
                "Entrada de inventario",
                null
            );

            return ResponseEntity.ok(lote);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al registrar entrada: " + e.getMessage()));
        }
    }

    @PutMapping("/lotes-inventario")
    public LotesInventario modificar(@RequestBody LotesInventario entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/lotes-inventario/{id}")
    public Optional<LotesInventario> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/lotes-inventario/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }

    // Métodos auxiliares para extraer datos del Map
    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Map) {
            return getLong((Map<String, Object>) value, "id");
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}

