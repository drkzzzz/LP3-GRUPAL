package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SedeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SedeConfigRepository extends JpaRepository<SedeConfig, Long> {
    
    Optional<SedeConfig> findBySedeId(Long sedeId);
    
    List<SedeConfig> findByTenantId(Long tenantId);
    
    @Query("SELECT sc FROM SedeConfig sc WHERE sc.tenantId = :tenantId AND sc.leySecaActiva = true")
    List<SedeConfig> findSedesConLeySeca(@Param("tenantId") Long tenantId);
    
    @Query("SELECT sc FROM SedeConfig sc WHERE sc.sedeId = :sedeId AND sc.leySecaActiva = false")
    Optional<SedeConfig> findConfigSinLeySeca(@Param("sedeId") Long sedeId);
    
    boolean existsBySedeId(Long sedeId);
}
