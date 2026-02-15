package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    
    Optional<Permiso> findByCodigo(String codigo);
    
    List<Permiso> findByActivoTrue();
    
    @Query("SELECT p FROM Permiso p WHERE p.codigo IN :codigos AND p.activo = true")
    List<Permiso> findByCodigos(@Param("codigos") Set<String> codigos);
    
    @Query("SELECT p FROM Permiso p WHERE p.codigo LIKE :prefijo% AND p.activo = true")
    List<Permiso> findByCodigoStartingWith(@Param("prefijo") String prefijo);
    
    @Query("SELECT DISTINCT p FROM Permiso p JOIN p.roles r WHERE r.id = :rolId AND p.activo = true")
    List<Permiso> findByRolId(@Param("rolId") Long rolId);
    
    boolean existsByCodigo(String codigo);
}
