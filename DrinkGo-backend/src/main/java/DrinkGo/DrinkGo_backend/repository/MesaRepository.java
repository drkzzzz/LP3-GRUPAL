package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByNegocioId(Long negocioId);

    List<Mesa> findBySedeId(Long sedeId);

    List<Mesa> findBySedeIdAndEstaActivo(Long sedeId, Boolean estaActivo);

    List<Mesa> findBySedeIdAndEstado(Long sedeId, String estado);

    List<Mesa> findByAreaId(Long areaId);

    Optional<Mesa> findBySedeIdAndNumeroMesa(Long sedeId, String numeroMesa);

    Optional<Mesa> findByCodigoQr(String codigoQr);
}
