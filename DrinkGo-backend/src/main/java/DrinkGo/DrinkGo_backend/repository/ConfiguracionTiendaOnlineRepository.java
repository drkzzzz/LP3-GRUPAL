package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ConfiguracionTiendaOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionTiendaOnlineRepository extends JpaRepository<ConfiguracionTiendaOnline, Long> {
    
    Optional<ConfiguracionTiendaOnline> findByNegocioId(Long negocioId);
    
    Optional<ConfiguracionTiendaOnline> findBySlugTienda(String slugTienda);
    
    boolean existsByNegocioId(Long negocioId);
    
    boolean existsBySlugTienda(String slugTienda);
}
