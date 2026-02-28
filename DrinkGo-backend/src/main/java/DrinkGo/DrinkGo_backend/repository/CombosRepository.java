package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.Combos;

public interface CombosRepository extends JpaRepository<Combos, Long> {

    @Query("SELECT c FROM Combos c JOIN FETCH c.negocio WHERE c.negocio.id = :negocioId")
    List<Combos> findByNegocioId(@Param("negocioId") Long negocioId);
}
