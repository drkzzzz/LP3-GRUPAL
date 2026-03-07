package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import DrinkGo.DrinkGo_backend.entity.CuentasMesa;
import DrinkGo.DrinkGo_backend.entity.CuentasMesa.EstadoCuenta;

public interface CuentasMesaRepository extends JpaRepository<CuentasMesa, Long> {

    /** Cuentas activas (no cerradas) de una sede */
    @Query("SELECT c FROM CuentasMesa c WHERE c.mesa.sede.id = :sedeId AND c.estado = :estado")
    List<CuentasMesa> findBySedeIdAndEstado(@Param("sedeId") Long sedeId,
            @Param("estado") EstadoCuenta estado);

    /** Cuentas activas de una mesa específica */
    @Query("SELECT c FROM CuentasMesa c WHERE c.mesa.id = :mesaId AND c.estado = 'abierta'")
    Optional<CuentasMesa> findOpenByMesaId(@Param("mesaId") Long mesaId);

    /** Siguiente número correlativo por negocio */
    @Query("SELECT COUNT(c) + 1 FROM CuentasMesa c WHERE c.negocioId = :negocioId")
    long nextNumero(@Param("negocioId") Long negocioId);

    /** Todas las cuentas de un negocio */
    @Query("SELECT c FROM CuentasMesa c WHERE c.negocioId = :negocioId ORDER BY c.creadoEn DESC")
    List<CuentasMesa> findByNegocioId(@Param("negocioId") Long negocioId);
}
