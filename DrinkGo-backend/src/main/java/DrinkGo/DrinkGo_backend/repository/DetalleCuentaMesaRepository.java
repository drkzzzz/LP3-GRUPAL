package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import DrinkGo.DrinkGo_backend.entity.DetalleCuentaMesa;

public interface DetalleCuentaMesaRepository extends JpaRepository<DetalleCuentaMesa, Long> {

    @Query("SELECT d FROM DetalleCuentaMesa d WHERE d.cuenta.id = :cuentaId")
    List<DetalleCuentaMesa> findByCuentaId(@Param("cuentaId") Long cuentaId);
}
