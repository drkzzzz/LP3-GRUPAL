package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SeguimientoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeguimientoPedidoRepository extends JpaRepository<SeguimientoPedido, Long> {
    
    List<SeguimientoPedido> findByPedidoIdOrderByCreadoEnDesc(Long pedidoId);
    
    @Query("SELECT sp FROM SeguimientoPedido sp WHERE sp.pedidoId = :pedidoId ORDER BY sp.creadoEn DESC LIMIT 1")
    Optional<SeguimientoPedido> findUltimoSeguimiento(@Param("pedidoId") Long pedidoId);
    
    @Query("SELECT sp FROM SeguimientoPedido sp WHERE sp.usuarioId = :usuarioId ORDER BY sp.creadoEn DESC")
    List<SeguimientoPedido> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
