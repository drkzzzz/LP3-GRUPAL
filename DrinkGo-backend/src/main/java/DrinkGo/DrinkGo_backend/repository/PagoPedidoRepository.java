package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PagoPedido;
import DrinkGo.DrinkGo_backend.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoPedidoRepository extends JpaRepository<PagoPedido, Long> {
    
    List<PagoPedido> findByPedidoId(Long pedidoId);
    
    @Query("SELECT SUM(pp.monto) FROM PagoPedido pp WHERE pp.pedidoId = :pedidoId")
    BigDecimal sumMontoPorPedido(@Param("pedidoId") Long pedidoId);
    
    List<PagoPedido> findByPedidoIdAndMetodoPago(Long pedidoId, PaymentMethod metodoPago);
    
    @Query("SELECT pp FROM PagoPedido pp WHERE pp.referencia = :referencia")
    List<PagoPedido> findByReferencia(@Param("referencia") String referencia);
}
