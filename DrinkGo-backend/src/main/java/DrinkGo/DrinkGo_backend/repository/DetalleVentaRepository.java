package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para detalle de ventas.
 * Bloque 8 - Ventas, POS y Cajas.
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    /** Buscar detalle por ID y negocio (Seguridad multi-tenant) */
    Optional<DetalleVenta> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar detalles de una venta */
    List<DetalleVenta> findByNegocioIdAndVentaId(Long negocioId, Long ventaId);

    /** Listar ventas que incluyen un producto específico */
    List<DetalleVenta> findByNegocioIdAndProductoId(Long negocioId, Long productoId);

    /** Contar cantidad vendida de un producto */
    @Query("SELECT COALESCE(SUM(dv.cantidad), 0) FROM DetalleVenta dv JOIN dv.venta v WHERE dv.negocioId = :negocioId AND dv.productoId = :productoId AND v.estado NOT IN ('CANCELADA', 'ANULADA')")
    Integer contarCantidadVendidaProducto(@Param("negocioId") Long negocioId, @Param("productoId") Long productoId);
}
