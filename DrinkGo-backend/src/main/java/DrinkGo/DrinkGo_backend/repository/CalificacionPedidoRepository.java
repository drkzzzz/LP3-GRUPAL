package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.CalificacionPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalificacionPedidoRepository extends JpaRepository<CalificacionPedido, Long> {
    
    Optional<CalificacionPedido> findByPedidoId(Long pedidoId);
    
    @Query("SELECT AVG(c.estrellas) FROM CalificacionPedido c " +
           "JOIN Pedido p ON c.pedidoId = p.id WHERE p.tenantId = :tenantId")
    Double promedioEstrellasPorTenant(@Param("tenantId") Long tenantId);
    
    @Query("SELECT AVG(c.estrellas) FROM CalificacionPedido c " +
           "JOIN Pedido p ON c.pedidoId = p.id WHERE p.tenantId = :tenantId AND p.sedeId = :sedeId")
    Double promedioEstrellasPorSede(@Param("tenantId") Long tenantId, @Param("sedeId") Long sedeId);
    
    @Query("SELECT AVG(c.puntualidad) FROM CalificacionPedido c " +
           "JOIN Pedido p ON c.pedidoId = p.id WHERE p.tenantId = :tenantId AND p.repartidorId = :repartidorId")
    Double promedioPuntualidadRepartidor(@Param("tenantId") Long tenantId, 
                                         @Param("repartidorId") Long repartidorId);
}
