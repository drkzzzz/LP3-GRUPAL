package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ZonaDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZonaDeliveryRepository extends JpaRepository<ZonaDelivery, Long> {
    
    List<ZonaDelivery> findByTenantId(Long tenantId);
    
    List<ZonaDelivery> findByTenantIdAndSedeIdAndActivoTrue(Long tenantId, Long sedeId);
    
    List<ZonaDelivery> findByTenantIdAndSedeId(Long tenantId, Long sedeId);
    
    @Query(value = "SELECT * FROM drinkgo.zona_delivery WHERE tenant_id = :tenantId " +
                   "AND sede_id = :sedeId AND activo = true AND :distrito = ANY(distritos)", 
           nativeQuery = true)
    Optional<ZonaDelivery> findByDistrito(@Param("tenantId") Long tenantId,
                                          @Param("sedeId") Long sedeId,
                                          @Param("distrito") String distrito);
    
    @Query("SELECT z FROM ZonaDelivery z WHERE z.tenantId = :tenantId AND z.activo = true")
    List<ZonaDelivery> findZonasActivas(@Param("tenantId") Long tenantId);
}
