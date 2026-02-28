package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.SesionesCaja;

import java.util.List;
import java.util.Optional;

public interface SesionesCajaRepository extends JpaRepository<SesionesCaja, Long> {

    Optional<SesionesCaja> findByCajaIdAndEstadoSesion(Long cajaId, SesionesCaja.EstadoSesion estado);

    Optional<SesionesCaja> findByUsuarioIdAndEstadoSesion(Long usuarioId, SesionesCaja.EstadoSesion estado);

    List<SesionesCaja> findByCajaIdOrderByFechaAperturaDesc(Long cajaId);

    List<SesionesCaja> findByCajaNegocioIdOrderByFechaAperturaDesc(Long negocioId);
}
