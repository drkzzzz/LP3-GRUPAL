package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {
    
    List<Sede> findByTenantIdAndActivoTrue(Long tenantId);
    
    List<Sede> findByTenantId(Long tenantId);
    
    Optional<Sede> findByTenantIdAndCodigo(Long tenantId, String codigo);
    
    Optional<Sede> findByIdAndTenantId(Long id, Long tenantId);
    
    @Query("SELECT s FROM Sede s WHERE s.tenantId = :tenantId AND s.activo = true AND s.hasTables = true")
    List<Sede> findSedesConMesas(@Param("tenantId") Long tenantId);
    
    @Query("SELECT s FROM Sede s WHERE s.tenantId = :tenantId AND s.activo = true AND s.hasDelivery = true")
    List<Sede> findSedesConDelivery(@Param("tenantId") Long tenantId);
    
    @Query("SELECT s FROM Sede s WHERE s.tenantId = :tenantId AND s.ciudad = :ciudad AND s.activo = true")
    List<Sede> findByTenantIdAndCiudad(@Param("tenantId") Long tenantId, @Param("ciudad") String ciudad);
    
    long countByTenantIdAndActivoTrue(Long tenantId);
    
    boolean existsByTenantIdAndCodigo(Long tenantId, String codigo);
}
