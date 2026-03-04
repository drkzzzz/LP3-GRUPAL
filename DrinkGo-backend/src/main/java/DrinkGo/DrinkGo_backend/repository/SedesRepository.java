package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import DrinkGo.DrinkGo_backend.entity.Sedes;

public interface SedesRepository extends JpaRepository<Sedes, Long> {

    @Query(value = "SELECT * FROM sedes WHERE negocio_id = ?1 AND esta_activo = 1", nativeQuery = true)
    java.util.List<Sedes> findByNegocioId(Long negocioId);

    @Query(value = "SELECT COUNT(*) FROM sedes WHERE negocio_id = ?1 AND esta_activo = 1", nativeQuery = true)
    long countAllByNegocioId(Long negocioId);

    // JPQL: Hibernate maneja el mapeo Boolean correctamente (BIT/TINYINT)
    @Query("SELECT COUNT(s) FROM Sedes s WHERE s.negocio.id = :negocioId AND s.esPrincipal = true AND s.id <> :excludeId")
    long countOtherPrincipalByNegocioId(@Param("negocioId") Long negocioId, @Param("excludeId") Long excludeId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sedes SET es_principal = 0 WHERE negocio_id = ?1 AND esta_activo = 1", nativeQuery = true)
    void resetPrincipalByNegocioId(Long negocioId);
}
