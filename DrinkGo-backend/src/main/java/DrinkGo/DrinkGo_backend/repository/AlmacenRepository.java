package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    
    List<Almacen> findBySedeIdAndActivoTrue(Long sedeId);
    
    List<Almacen> findByTenantIdAndActivoTrue(Long tenantId);
    
    Optional<Almacen> findBySedeIdAndCodigo(Long sedeId, String codigo);
    
    Optional<Almacen> findByIdAndTenantId(Long id, Long tenantId);
    
    @Query("SELECT a FROM Almacen a WHERE a.sedeId = :sedeId AND a.esPrincipal = true AND a.activo = true")
    Optional<Almacen> findAlmacenPrincipal(@Param("sedeId") Long sedeId);
    
    @Query("SELECT a FROM Almacen a WHERE a.sedeId = :sedeId AND a.tipo = :tipo AND a.activo = true")
    List<Almacen> findBySedeIdAndTipo(@Param("sedeId") Long sedeId, @Param("tipo") String tipo);
    
    @Query("SELECT a FROM Almacen a WHERE a.tenantId = :tenantId AND a.tipo = 'frio' AND a.activo = true")
    List<Almacen> findAlmacenesFrios(@Param("tenantId") Long tenantId);
    
    boolean existsBySedeIdAndCodigo(Long sedeId, String codigo);
    
    long countBySedeIdAndActivoTrue(Long sedeId);
}
