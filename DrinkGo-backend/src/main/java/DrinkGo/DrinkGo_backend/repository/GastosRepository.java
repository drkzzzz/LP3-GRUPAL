package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import java.time.LocalDate;
import java.util.List;

public interface GastosRepository extends JpaRepository<Gastos, Long> {
    List<Gastos> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    List<Gastos> findByNegocioIdAndEsRecurrenteTrueAndProximaEjecucionLessThanEqual(
            Long negocioId, LocalDate fecha);

    /**
     * Gastos pendientes (no recurrentes) cuya fecha ya pasó o es hoy — para
     * auto-pago.
     */
    @Query("SELECT g FROM Gastos g WHERE g.estado = 'pendiente' AND g.esRecurrente = false AND g.fechaGasto <= :hoy")
    List<Gastos> findGastosPendientesDue(LocalDate hoy);

    /**
     * Todos los gastos recurrentes activos con proximaEjecucion vencida — para
     * generar copias pendientes.
     */
    List<Gastos> findByEsRecurrenteTrueAndProximaEjecucionLessThanEqual(LocalDate fecha);
}
