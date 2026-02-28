package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.DetalleCombos;

public interface DetalleCombosRepository extends JpaRepository<DetalleCombos, Long> {

    @Query("SELECT dc FROM DetalleCombos dc JOIN FETCH dc.combo JOIN FETCH dc.producto WHERE dc.combo.negocio.id = :negocioId")
    List<DetalleCombos> findByComboNegocioId(@Param("negocioId") Long negocioId);
}
