package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.AlertaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para alertas de inventario (RF-INV-004..005).
 * Todas las consultas filtran por negocio_id para garantizar la seguridad de los datos.
 */
@Repository
public interface AlertaInventarioRepository extends JpaRepository<AlertaInventario, Long> {

    /** Listar todas las alertas de un negocio ordenadas por las más recientes */
    List<AlertaInventario> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    /** Buscar alerta por ID y negocio para evitar acceso no autorizado */
    Optional<AlertaInventario> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar alertas filtrando por su estado (resueltas o no) */
    List<AlertaInventario> findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(
            Long negocioId, Boolean estaResuelta);

    /** Listar alertas activas (no resueltas) - Método de conveniencia */
    default List<AlertaInventario> findActivas(Long negocioId) {
        return findByNegocioIdAndEstaResueltaOrderByCreadoEnDesc(negocioId, false);
    }

    /** Listar alertas por tipo (ej: stock_bajo, vencido) */
    List<AlertaInventario> findByNegocioIdAndTipoAlertaOrderByCreadoEnDesc(
            Long negocioId, AlertaInventario.TipoAlerta tipoAlerta);

    /** * Verificar si ya existe una alerta del mismo tipo para un producto en un almacén.
     * Útil para evitar duplicar alertas de 'stock bajo' si ya hay una pendiente.
     */
    boolean existsByNegocioIdAndProductoIdAndAlmacenIdAndTipoAlertaAndEstaResuelta(
            Long negocioId, Long productoId, Long almacenId, 
            AlertaInventario.TipoAlerta tipoAlerta, Boolean estaResuelta);
}