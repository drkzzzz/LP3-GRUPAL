package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.AreaMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaMesaRepository extends JpaRepository<AreaMesa, Long> {
    List<AreaMesa> findByNegocioId(Long negocioId);

    List<AreaMesa> findBySedeId(Long sedeId);

    List<AreaMesa> findBySedeIdAndEstaActivo(Long sedeId, Boolean estaActivo);

    List<AreaMesa> findBySedeIdOrderByOrdenAsc(Long sedeId);
}
