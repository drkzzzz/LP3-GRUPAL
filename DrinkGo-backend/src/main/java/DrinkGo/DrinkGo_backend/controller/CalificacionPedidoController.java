package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.CalificacionPedidoDTO;
import DrinkGo.DrinkGo_backend.service.CalificacionPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tenants/{tenantId}/calificaciones")
@CrossOrigin(origins = "*")
public class CalificacionPedidoController {
    
    @Autowired
    private CalificacionPedidoService calificacionService;
    
    // Crear calificación
    @PostMapping
    public ResponseEntity<CalificacionPedidoDTO> crearCalificacion(
            @PathVariable Long tenantId,
            @RequestBody CalificacionPedidoDTO dto) {
        CalificacionPedidoDTO calificacion = calificacionService.crearCalificacion(tenantId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(calificacion);
    }
    
    // Obtener calificación de un pedido
    @GetMapping("/pedidos/{pedidoId}")
    public ResponseEntity<?> obtenerCalificacionPedido(
            @PathVariable Long tenantId,
            @PathVariable Long pedidoId) {
        return calificacionService.obtenerCalificacionPedido(tenantId, pedidoId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Obtener promedio del tenant
    @GetMapping("/promedio")
    public ResponseEntity<Map<String, Double>> obtenerPromedioTenant(
            @PathVariable Long tenantId) {
        Double promedio = calificacionService.obtenerPromedioTenant(tenantId);
        return ResponseEntity.ok(Map.of("promedio", promedio));
    }
    
    // Obtener promedio por sede
    @GetMapping("/sedes/{sedeId}/promedio")
    public ResponseEntity<Map<String, Double>> obtenerPromedioSede(
            @PathVariable Long tenantId,
            @PathVariable Long sedeId) {
        Double promedio = calificacionService.obtenerPromedioSede(tenantId, sedeId);
        return ResponseEntity.ok(Map.of("promedio", promedio));
    }
    
    // Obtener promedio de puntualidad de un repartidor
    @GetMapping("/repartidores/{repartidorId}/puntualidad")
    public ResponseEntity<Map<String, Double>> obtenerPromedioPuntualidadRepartidor(
            @PathVariable Long tenantId,
            @PathVariable Long repartidorId) {
        Double promedio = calificacionService.obtenerPromedioPuntualidadRepartidor(tenantId, repartidorId);
        return ResponseEntity.ok(Map.of("promedioPuntualidad", promedio));
    }
}
