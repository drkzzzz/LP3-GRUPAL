package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.AlertaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {

    List<AlertaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    List<AlertaInventario> findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(
            Long negocioId, Boolean estaResuelta);

    Optional<AlertaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    List<AlertaInventario> findByNegocioIdAndTipoAlertaOrderByCreadoEnDesc(
            Long negocioId, AlertaInventario.TipoAlerta tipoAlerta);

    // Verifica si ya existe una alerta activa para un producto/almacén/tipo
    boolean existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResuelta(
            Long negocioId, Long productoId, Long almacenId,
            AlertaInventario.TipoAlerta tipoAlerta, Boolean estaResuelta);
}
