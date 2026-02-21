package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.PedidoDTO;
import DrinkGo.DrinkGo_backend.dto.PagoPedidoDTO;
import DrinkGo.DrinkGo_backend.enums.OrderStatus;
import DrinkGo.DrinkGo_backend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants/{tenantId}/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;
    
    // Listar todos los pedidos del negocio
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodosPedidos(@PathVariable Long tenantId) {
        List<PedidoDTO> pedidos = pedidoService.listarPorNegocio(tenantId);
        return ResponseEntity.ok(pedidos);
    }
    
    // Crear pedido
    @PostMapping("/sedes/{sedeId}")
    public ResponseEntity<PedidoDTO> crearPedido(
            @PathVariable Long tenantId,
            @PathVariable Long sedeId,
            @RequestBody PedidoDTO dto) {
        PedidoDTO pedido = pedidoService.crearPedido(tenantId, sedeId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
    
    // Obtener pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPedido(
            @PathVariable Long tenantId,
            @PathVariable Long id) {
        PedidoDTO pedido = pedidoService.obtenerPedido(tenantId, id);
        return ResponseEntity.ok(pedido);
    }
    
    // Listar pedidos por sede
    @GetMapping("/sedes/{sedeId}")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorSede(
            @PathVariable Long tenantId,
            @PathVariable Long sedeId) {
        List<PedidoDTO> pedidos = pedidoService.listarPedidosPorSede(tenantId, sedeId);
        return ResponseEntity.ok(pedidos);
    }
    
    // Listar pedidos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorEstado(
            @PathVariable Long tenantId,
            @PathVariable OrderStatus estado) {
        List<PedidoDTO> pedidos = pedidoService.listarPedidosPorEstado(tenantId, estado);
        return ResponseEntity.ok(pedidos);
    }
    
    // Obtener pedidos pendientes de preparación
    @GetMapping("/sedes/{sedeId}/pendientes-preparacion")
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosPendientesPreparacion(
            @PathVariable Long tenantId,
            @PathVariable Long sedeId) {
        List<PedidoDTO> pedidos = pedidoService.obtenerPedidosPendientesPreparacion(tenantId, sedeId);
        return ResponseEntity.ok(pedidos);
    }
    
    // Cambiar estado de pedido
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> cambiarEstado(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestParam OrderStatus nuevoEstado,
            @RequestParam(required = false) String mensaje,
            @RequestParam(required = false) Long usuarioId) {
        PedidoDTO pedido = pedidoService.cambiarEstado(tenantId, id, nuevoEstado, mensaje, usuarioId);
        return ResponseEntity.ok(pedido);
    }
    
    // Marcar como en preparación
    @PostMapping("/{id}/preparar")
    public ResponseEntity<PedidoDTO> marcarEnPreparacion(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId) {
        PedidoDTO pedido = pedidoService.cambiarEstado(tenantId, id, OrderStatus.en_preparacion, 
            "Pedido en preparación", usuarioId);
        return ResponseEntity.ok(pedido);
    }
    
    // Marcar como listo
    @PostMapping("/{id}/listo")
    public ResponseEntity<PedidoDTO> marcarListo(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId) {
        PedidoDTO pedido = pedidoService.cambiarEstado(tenantId, id, OrderStatus.listo, 
            "Pedido listo para entrega", usuarioId);
        return ResponseEntity.ok(pedido);
    }
    
    // Marcar como entregado
    @PostMapping("/{id}/entregar")
    public ResponseEntity<PedidoDTO> marcarEntregado(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestParam(required = false) Long usuarioId) {
        PedidoDTO pedido = pedidoService.cambiarEstado(tenantId, id, OrderStatus.entregado, 
            "Pedido entregado", usuarioId);
        return ResponseEntity.ok(pedido);
    }
    
    // Asignar repartidor
    @PostMapping("/{id}/asignar-repartidor")
    public ResponseEntity<PedidoDTO> asignarRepartidor(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestParam Long repartidorId) {
        PedidoDTO pedido = pedidoService.asignarRepartidor(tenantId, id, repartidorId);
        return ResponseEntity.ok(pedido);
    }
    
    // Registrar pago
    @PostMapping("/{id}/pagos")
    public ResponseEntity<PedidoDTO> registrarPago(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestBody PagoPedidoDTO pago) {
        PedidoDTO pedido = pedidoService.registrarPago(tenantId, id, pago);
        return ResponseEntity.ok(pedido);
    }
    
    // Verificar edad
    @PostMapping("/{id}/verificar-edad")
    public ResponseEntity<PedidoDTO> verificarEdad(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestParam Long usuarioId) {
        PedidoDTO pedido = pedidoService.verificarEdad(tenantId, id, usuarioId);
        return ResponseEntity.ok(pedido);
    }
    
    // Anular pedido
    @PostMapping("/{id}/anular")
    public ResponseEntity<PedidoDTO> anularPedido(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestParam String motivo,
            @RequestParam(required = false) Long usuarioId) {
        PedidoDTO pedido = pedidoService.anularPedido(tenantId, id, motivo, usuarioId);
        return ResponseEntity.ok(pedido);
    }
    
    // Actualizar pedido completo
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizarPedido(
            @PathVariable Long tenantId,
            @PathVariable Long id,
            @RequestBody PedidoDTO dto) {
        PedidoDTO pedido = pedidoService.actualizarPedido(tenantId, id, dto);
        return ResponseEntity.ok(pedido);
    }
    
    // Eliminar pedido (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(
            @PathVariable Long tenantId,
            @PathVariable Long id) {
        pedidoService.eliminar(tenantId, id);
        return ResponseEntity.noContent().build();
    }
    
    // Obtener pedidos de delivery sin repartidor
    @GetMapping("/delivery/sin-repartidor")
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosDeliverySinRepartidor(
            @PathVariable Long tenantId) {
        List<PedidoDTO> pedidos = pedidoService.obtenerPedidosDeliverySinRepartidor(tenantId);
        return ResponseEntity.ok(pedidos);
    }
}
