package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.AsignacionDeliveryPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionDeliveryPedidoRepository extends JpaRepository<AsignacionDeliveryPedido, Long> {

    Optional<AsignacionDeliveryPedido> findByPedidoId(Long pedidoId);

    List<AsignacionDeliveryPedido> findByRepartidorId(Long repartidorId);

    List<AsignacionDeliveryPedido> findByRepartidorIdAndEstado(
            Long repartidorId, AsignacionDeliveryPedido.EstadoDelivery estado);

    List<AsignacionDeliveryPedido> findByEstado(AsignacionDeliveryPedido.EstadoDelivery estado);

    Long countByRepartidorIdAndEstado(Long repartidorId, AsignacionDeliveryPedido.EstadoDelivery estado);
}
