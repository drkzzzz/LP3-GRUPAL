package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.PagoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para pagos de ventas.
 * Bloque 8 - Ventas, POS y Cajas.
 */
@Repository
public interface PagoVentaRepository extends JpaRepository<PagoVenta, Long> {

    /** Buscar pago por ID y negocio (Seguridad multi-tenant) */
    Optional<PagoVenta> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar pagos de una venta */
    List<PagoVenta> findByNegocioIdAndVentaId(Long negocioId, Long ventaId);

    /** Listar pagos por método de pago */
    List<PagoVenta> findByNegocioIdAndMetodoPagoId(Long negocioId, Long metodoPagoId);

    /** Listar pagos por estado */
    List<PagoVenta> findByNegocioIdAndEstado(Long negocioId, String estado);

    /** Calcular total pagado en una venta */
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoVenta p WHERE p.negocioId = :negocioId AND p.ventaId = :ventaId AND p.estado = 'COMPLETADO'")
    BigDecimal calcularTotalPagadoVenta(@Param("negocioId") Long negocioId, @Param("ventaId") Long ventaId);
}
