package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.AlertaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para alertas de inventario (RF-INV-004..005).
 * Todas las consultas filtran por negocio_id (multi-tenant obligatorio).
 */
@Repository
public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {

    /** Listar todas las alertas de un negocio */
    List<AlertaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Buscar alerta por ID y negocio */
    Optional<AlertaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar alertas activas (no resueltas) de un negocio */
    List<AlertaInventario> findByNegocioIdAndEstaResueltaFalseOrderByCreadoEnDesc(Long negocioId);

    /** Listar alertas por tipo */
    List<AlertaInventario> findByNegocioIdAndTipoAlertaOrderByCreadoEnDesc(
            Long negocioId, AlertaInventario.TipoAlerta tipoAlerta);

    /** Verificar si ya existe una alerta activa del mismo tipo para un producto en un almacén */
    boolean existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResueltaFalse(
            Long negocioId, Long productoId, Long almacenId, AlertaInventario.TipoAlerta tipoAlerta);
}
