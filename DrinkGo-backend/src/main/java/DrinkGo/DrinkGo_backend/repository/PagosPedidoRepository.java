package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.PagosPedido;
import java.util.List;

public interface PagosPedidoRepository extends JpaRepository<PagosPedido, Long> {

    @Query("SELECT p FROM PagosPedido p LEFT JOIN FETCH p.metodoPago WHERE p.pedido.id = :pedidoId")
    List<PagosPedido> findByPedidoId(@Param("pedidoId") Long pedidoId);

}
