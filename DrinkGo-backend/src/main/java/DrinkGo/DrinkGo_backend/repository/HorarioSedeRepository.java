package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.HorarioSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioSedeRepository extends JpaRepository<HorarioSede, Long> {
    List<HorarioSede> findBySedeId(Long sedeId);

    List<HorarioSede> findBySedeIdAndEstaActivo(Long sedeId, Boolean estaActivo);

    Optional<HorarioSede> findBySedeIdAndDiaSemana(Long sedeId, Integer diaSemana);
}
