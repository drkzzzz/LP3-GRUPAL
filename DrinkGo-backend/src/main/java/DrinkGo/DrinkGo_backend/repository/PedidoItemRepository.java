package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    
    List<PedidoItem> findByPedidoId(Long pedidoId);
    
    @Query("SELECT pi FROM PedidoItem pi WHERE pi.productoId = :productoId")
    List<PedidoItem> findByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT pi FROM PedidoItem pi WHERE pi.comboId = :comboId")
    List<PedidoItem> findByComboId(@Param("comboId") Long comboId);
    
    void deleteByPedidoId(Long pedidoId);
}
