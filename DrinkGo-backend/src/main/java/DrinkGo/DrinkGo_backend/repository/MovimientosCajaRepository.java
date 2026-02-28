package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.MovimientosCaja;

import java.util.List;

public interface MovimientosCajaRepository extends JpaRepository<MovimientosCaja, Long> {

    List<MovimientosCaja> findBySesionCajaIdOrderByFechaMovimientoDesc(Long sesionCajaId);
}
