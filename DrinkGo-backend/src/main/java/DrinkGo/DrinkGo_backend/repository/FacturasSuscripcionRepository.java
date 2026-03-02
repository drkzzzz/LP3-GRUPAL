package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import DrinkGo.DrinkGo_backend.entity.FacturasSuscripcion;

public interface FacturasSuscripcionRepository extends JpaRepository<FacturasSuscripcion, Long> {
    List<FacturasSuscripcion> findByNegocioId(Long negocioId);

    @Query(value = "SELECT COUNT(*) FROM facturas_suscripcion WHERE negocio_id = ?1", nativeQuery = true)
    long countByNegocioId(Long negocioId);

    /**
     * Cuenta cuántas facturas existen globalmente para un año-mes dado (para
     * secuencia única).
     */
    @Query(value = "SELECT COUNT(*) FROM facturas_suscripcion WHERE YEAR(emitido_en) = ?1 AND MONTH(emitido_en) = ?2", nativeQuery = true)
    long countByYearAndMonth(int year, int month);

    Optional<FacturasSuscripcion> findByNumeroFactura(String numeroFactura);
}
