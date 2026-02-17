package DrinkGo.DrinkGo_backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.Promocion;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByNegocioId(Long negocioId);

    Optional<Promocion> findByIdAndNegocioId(Long id, Long negocioId);

    boolean existsByNegocioIdAndCodigo(Long negocioId, String codigo);

    /**
     * Promociones vigentes (activas y dentro de rango de fechas).
     */
    @Query("SELECT p FROM Promocion p WHERE p.negocioId = :negocioId " +
            "AND p.validoDesde <= :ahora AND p.validoHasta >= :ahora")
    List<Promocion> findVigentes(@Param("negocioId") Long negocioId,
                                  @Param("ahora") LocalDateTime ahora);
}