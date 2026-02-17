package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PedidoAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoAdjuntoRepository extends JpaRepository<PedidoAdjunto, Long> {
    
    List<PedidoAdjunto> findByPedidoId(Long pedidoId);
    
    List<PedidoAdjunto> findByPedidoIdAndTipo(Long pedidoId, String tipo);
}
