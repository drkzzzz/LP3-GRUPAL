package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Pedido;
import DrinkGo.DrinkGo_backend.enums.OrderModality;
import DrinkGo.DrinkGo_backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Buscar por tenant y número de pedido
    Optional<Pedido> findByTenantIdAndNumeroPedido(Long tenantId, String numeroPedido);
    
    // Listar pedidos por tenant
    List<Pedido> findByTenantIdOrderByFechaCreacionDesc(Long tenantId);
    
    // Listar pedidos por sede
    List<Pedido> findByTenantIdAndSedeIdOrderByFechaCreacionDesc(Long tenantId, Long sedeId);
    
    // Buscar por estado
    List<Pedido> findByTenantIdAndEstadoOrderByFechaCreacionDesc(Long tenantId, OrderStatus estado);
    
    // Buscar por modalidad
    List<Pedido> findByTenantIdAndModalidadOrderByFechaCreacionDesc(Long tenantId, OrderModality modalidad);
    
    // Pedidos pendientes para preparación
    @Query("SELECT p FROM Pedido p WHERE p.tenantId = :tenantId AND p.sedeId = :sedeId " +
           "AND p.estado IN ('pendiente', 'en_preparacion') ORDER BY p.fechaCreacion ASC")
    List<Pedido> findPedidosPendientesPreparacion(@Param("tenantId") Long tenantId, 
                                                   @Param("sedeId") Long sedeId);
    
    // Pedidos de delivery sin asignar repartidor
    @Query("SELECT p FROM Pedido p WHERE p.tenantId = :tenantId AND p.modalidad = 'delivery' " +
           "AND p.estado = 'listo' AND p.repartidorId IS NULL ORDER BY p.fechaCreacion ASC")
    List<Pedido> findPedidosDeliverySinRepartidor(@Param("tenantId") Long tenantId);
    
    // Pedidos por repartidor
    List<Pedido> findByTenantIdAndRepartidorIdAndEstadoIn(Long tenantId, Long repartidorId, List<OrderStatus> estados);
    
    // Buscar por cliente
    List<Pedido> findByTenantIdAndClienteIdOrderByFechaCreacionDesc(Long tenantId, Long clienteId);
    
    // Pedidos en rango de fechas
    @Query("SELECT p FROM Pedido p WHERE p.tenantId = :tenantId " +
           "AND p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaCreacion DESC")
    List<Pedido> findPedidosPorFecha(@Param("tenantId") Long tenantId,
                                     @Param("fechaInicio") OffsetDateTime fechaInicio,
                                     @Param("fechaFin") OffsetDateTime fechaFin);
    
    // Pedidos por mesa activa
    @Query("SELECT p FROM Pedido p WHERE p.tenantId = :tenantId AND p.mesaId = :mesaId " +
           "AND p.estado NOT IN ('entregado', 'anulado') ORDER BY p.fechaCreacion DESC")
    List<Pedido> findPedidosActivosPorMesa(@Param("tenantId") Long tenantId, 
                                           @Param("mesaId") Long mesaId);
    
    // Contar pedidos por estado
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.tenantId = :tenantId AND p.estado = :estado")
    Long countByTenantIdAndEstado(@Param("tenantId") Long tenantId, 
                                  @Param("estado") OrderStatus estado);
    
    // Pedidos que requieren verificación de edad
    @Query("SELECT p FROM Pedido p WHERE p.tenantId = :tenantId " +
           "AND p.requiereVerificacionEdad = true AND p.verificacionRealizada = false " +
           "AND p.estado NOT IN ('entregado', 'anulado')")
    List<Pedido> findPedidosPendientesVerificacionEdad(@Param("tenantId") Long tenantId);
    
    // Generar número de pedido
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(numero_pedido FROM 7) AS INTEGER)), 0) + 1 " +
                   "FROM drinkgo.pedido WHERE tenant_id = :tenantId AND numero_pedido LIKE :prefijo%", 
           nativeQuery = true)
    Integer obtenerSiguienteNumeroPedido(@Param("tenantId") Long tenantId, 
                                         @Param("prefijo") String prefijo);
}
