package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import DrinkGo.DrinkGo_backend.entity.ZonasDelivery;

import java.util.List;

public interface ZonasDeliveryRepository extends JpaRepository<ZonasDelivery, Long> {

    @Query("SELECT z FROM ZonasDelivery z WHERE z.negocio.id = :negocioId")
    List<ZonasDelivery> findByNegocioId(Long negocioId);

    @Query("SELECT z FROM ZonasDelivery z WHERE z.sede.id = :sedeId")
    List<ZonasDelivery> findBySedeId(Long sedeId);

}
