package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByCodigo(String codigo);
    
    Optional<Rol> findByTenantIdAndCodigo(Long tenantId, String codigo);
    
    // Roles globales (tenant_id = NULL)
    @Query("SELECT r FROM Rol r WHERE r.tenantId IS NULL AND r.activo = true")
    List<Rol> findRolesGlobales();
    
    // Roles de sistema (no editables)
    @Query("SELECT r FROM Rol r WHERE r.esSistema = true AND r.activo = true")
    List<Rol> findRolesSistema();
    
    // Roles disponibles para un tenant (globales + propios)
    @Query("SELECT r FROM Rol r WHERE (r.tenantId IS NULL OR r.tenantId = :tenantId) AND r.activo = true")
    List<Rol> findRolesDisponibles(@Param("tenantId") Long tenantId);
    
    // Roles personalizados de un tenant
    @Query("SELECT r FROM Rol r WHERE r.tenantId = :tenantId AND r.activo = true")
    List<Rol> findRolesPersonalizados(@Param("tenantId") Long tenantId);
    
    boolean existsByTenantIdAndCodigo(Long tenantId, String codigo);
    
    @Query("SELECT r FROM Rol r JOIN r.permisos p WHERE p.codigo = :permisoCodigo AND r.activo = true")
    List<Rol> findByPermisoCodigo(@Param("permisoCodigo") String permisoCodigo);
}
