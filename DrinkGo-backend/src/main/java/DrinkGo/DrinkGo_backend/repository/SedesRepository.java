package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import DrinkGo.DrinkGo_backend.entity.Sedes;

public interface SedesRepository extends JpaRepository<Sedes, Long> {
    java.util.List<Sedes> findByNegocio_Id(Long negocioId);

    @Query(value = "SELECT COUNT(*) FROM sedes WHERE negocio_id = ?1", nativeQuery = true)
    long countAllByNegocioId(Long negocioId);
}
