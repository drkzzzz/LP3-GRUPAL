package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    List<Repartidor> findByNegocioId(Long negocioId);

    List<Repartidor> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    List<Repartidor> findByNegocioIdAndEstaDisponibleAndEstaActivo(
            Long negocioId, Boolean estaDisponible, Boolean estaActivo);

    List<Repartidor> findByNegocioIdAndTipoVehiculo(Long negocioId, Repartidor.TipoVehiculo tipoVehiculo);

    List<Repartidor> findByUsuarioId(Long usuarioId);
}
