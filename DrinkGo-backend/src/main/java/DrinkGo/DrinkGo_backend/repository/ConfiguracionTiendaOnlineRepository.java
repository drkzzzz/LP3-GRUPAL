package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionTiendaOnline;

import java.util.Optional;

public interface ConfiguracionTiendaOnlineRepository extends JpaRepository<ConfiguracionTiendaOnline, Long> {

    Optional<ConfiguracionTiendaOnline> findBySlugTienda(String slugTienda);

}
