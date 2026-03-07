package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import DrinkGo.DrinkGo_backend.entity.Promociones;

public interface PromocionesRepository extends JpaRepository<Promociones, Long> {
    List<Promociones> findByNegocioId(Long negocioId);

    @Query("SELECT p FROM Promociones p WHERE p.sede.id = :sedeId")
    List<Promociones> findBySedeId(@Param("sedeId") Long sedeId);
}
