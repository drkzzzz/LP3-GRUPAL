package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.HorarioEspecialSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioEspecialSedeRepository extends JpaRepository<HorarioEspecialSede, Long> {
    List<HorarioEspecialSede> findBySedeId(Long sedeId);

    Optional<HorarioEspecialSede> findBySedeIdAndFecha(Long sedeId, LocalDate fecha);

    List<HorarioEspecialSede> findBySedeIdAndFechaBetween(Long sedeId, LocalDate fechaInicio, LocalDate fechaFin);
}
