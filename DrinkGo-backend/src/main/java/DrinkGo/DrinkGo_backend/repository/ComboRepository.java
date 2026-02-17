package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Long> {

    List<Combo> findByNegocioId(Long negocioId);

    List<Combo> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    List<Combo> findByNegocioIdAndEstaActivoAndValidoHastaAfter(Long negocioId, Boolean estaActivo,
            LocalDateTime fecha);

    List<Combo> findByProductoId(Long productoId);
}
