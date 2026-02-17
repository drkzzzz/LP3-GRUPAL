package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para ventas (RF-VEN-003..008).
 * Bloque 8 - Ventas, POS y Cajas.
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

        /** Buscar venta por ID y negocio (Seguridad multi-tenant) */
        Optional<Venta> findByIdAndNegocioId(Long id, Long negocioId);

        /** Listar todas las ventas de un negocio */
        List<Venta> findByNegocioId(Long negocioId);

        /** Listar ventas de una sede */
        List<Venta> findByNegocioIdAndSedeId(Long negocioId, Long sedeId);

        /** Listar ventas de una sesión de caja */
        List<Venta> findByNegocioIdAndSesionId(Long negocioId, Long sesionId);

        /** Buscar venta por código */
        Optional<Venta> findByNegocioIdAndCodigoVenta(Long negocioId, String codigoVenta);

        /** Listar ventas por estado */
        List<Venta> findByNegocioIdAndEstado(Long negocioId, String estado);

        /** Listar ventas por tipo */
        List<Venta> findByNegocioIdAndTipoVenta(Long negocioId, String tipoVenta);

        /** Listar ventas de un cliente */
        List<Venta> findByNegocioIdAndClienteId(Long negocioId, Long clienteId);

        /** Listar ventas de un vendedor */
        List<Venta> findByNegocioIdAndVendedorId(Long negocioId, Long vendedorId);

        /** Listar ventas en rango de fechas */
        @Query("SELECT v FROM Venta v WHERE v.negocioId = :negocioId AND v.creadoEn BETWEEN :fechaInicio AND :fechaFin")
        List<Venta> findVentasPorRangoFecha(
                        @Param("negocioId") Long negocioId,
                        @Param("fechaInicio") LocalDateTime fechaInicio,
                        @Param("fechaFin") LocalDateTime fechaFin);

        /** Calcular total vendido en una sesión */
        @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.negocioId = :negocioId AND v.sesionId = :sesionId AND v.estado NOT IN ('CANCELADA', 'ANULADA')")
        BigDecimal calcularTotalVentasSesion(@Param("negocioId") Long negocioId, @Param("sesionId") Long sesionId);

        /** Calcular total vendido por sede en rango de fechas */
        @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.negocioId = :negocioId AND v.sedeId = :sedeId AND v.creadoEn BETWEEN :fechaInicio AND :fechaFin AND v.estado NOT IN ('CANCELADA', 'ANULADA')")
        BigDecimal calcularTotalVentasSedePorFecha(
                        @Param("negocioId") Long negocioId,
                        @Param("sedeId") Long sedeId,
                        @Param("fechaInicio") LocalDateTime fechaInicio,
                        @Param("fechaFin") LocalDateTime fechaFin);

        /** Calcular total de compras de un cliente */
        @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.negocioId = :negocioId AND v.clienteId = :clienteId AND v.estado NOT IN ('CANCELADA', 'ANULADA')")
        BigDecimal calcularTotalComprasCliente(@Param("negocioId") Long negocioId, @Param("clienteId") Long clienteId);

        /** Contar ventas por estado */
        @Query("SELECT COUNT(v) FROM Venta v WHERE v.negocioId = :negocioId AND v.estado = :estado")
        long contarVentasPorEstado(@Param("negocioId") Long negocioId, @Param("estado") String estado);
}
