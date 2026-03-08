package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;

public interface DetallePedidosRepository extends JpaRepository<DetallePedidos, Long> {

    List<DetallePedidos> findByPedido_Id(Long pedidoId);

}
