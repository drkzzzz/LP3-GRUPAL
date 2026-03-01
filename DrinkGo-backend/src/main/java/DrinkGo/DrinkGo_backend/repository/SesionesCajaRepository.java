package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.SesionesCaja;

public interface SesionesCajaRepository extends JpaRepository<SesionesCaja, Long> {

    Optional<SesionesCaja> findByUsuarioIdAndEstadoSesion(
            Long usuarioId, SesionesCaja.EstadoSesion estadoSesion);

    List<SesionesCaja> findByCajaIdOrderByFechaAperturaDesc(Long cajaId);

    List<SesionesCaja> findByCajaNegocioIdOrderByFechaAperturaDesc(Long negocioId);
}
