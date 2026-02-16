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
    
    /** Buscar almacén por ID y negocio (validación multi-tenant) */
    Optional<Almacen> findByIdAndNegocioId(Long id, Long negocioId);

    List<Almacen> findBySedeIdAndEstaActivoTrue(Long sedeId);
    
    List<Almacen> findByNegocioIdAndEstaActivoTrue(Long negocioId);
    
    Optional<Almacen> findBySedeIdAndCodigo(Long sedeId, String codigo);
    
    @Query("SELECT a FROM Almacen a WHERE a.sedeId = :sedeId AND a.esPrincipal = true AND a.estaActivo = true")
    Optional<Almacen> findAlmacenPrincipal(@Param("sedeId") Long sedeId);
    
    @Query("SELECT a FROM Almacen a WHERE a.sedeId = :sedeId AND a.tipoAlmacenamiento = :tipo AND a.estaActivo = true")
    List<Almacen> findBySedeIdAndTipo(@Param("sedeId") Long sedeId, @Param("tipo") String tipo);
    
    @Query("SELECT a FROM Almacen a WHERE a.negocioId = :negocioId AND a.tipoAlmacenamiento = 'frio' AND a.estaActivo = true")
    List<Almacen> findAlmacenesFrios(@Param("negocioId") Long negocioId);
    
    boolean existsBySedeIdAndCodigo(Long sedeId, String codigo);
    
    long countBySedeIdAndEstaActivoTrue(Long sedeId);
}