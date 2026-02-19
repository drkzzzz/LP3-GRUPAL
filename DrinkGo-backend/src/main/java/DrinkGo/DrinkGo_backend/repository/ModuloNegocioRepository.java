package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.ModuloNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para ModuloNegocio - Gestión de módulos activos por negocio
 */
@Repository
public interface ModuloNegocioRepository extends JpaRepository<ModuloNegocio, Long> {

    /**
     * Encuentra todos los módulos de un negocio específico
     */
    List<ModuloNegocio> findByNegocioId(Long negocioId);

    /**
     * Encuentra todos los módulos activos de un negocio
     */
    List<ModuloNegocio> findByNegocioIdAndEstaActivoTrue(Long negocioId);

    /**
     * Encuentra un módulo específico de un negocio
     */
    Optional<ModuloNegocio> findByNegocioIdAndModuloId(Long negocioId, Long moduloId);

    /**
     * Verifica si un negocio tiene un módulo activo
     */
    @Query("SELECT CASE WHEN COUNT(mn) > 0 THEN true ELSE false END " +
           "FROM ModuloNegocio mn " +
           "WHERE mn.negocio.id = :negocioId " +
           "AND mn.modulo.codigo = :codigoModulo " +
           "AND mn.estaActivo = true")
    boolean tieneModuloActivo(@Param("negocioId") Long negocioId, 
                              @Param("codigoModulo") String codigoModulo);

    /**
     * Encuentra todos los negocios que tienen un módulo específico activo
     */
    List<ModuloNegocio> findByModuloIdAndEstaActivoTrue(Long moduloId);

    /**
     * Cuenta cuántos módulos activos tiene un negocio
     */
    long countByNegocioIdAndEstaActivoTrue(Long negocioId);

    /**
     * Verifica si existe la relación negocio-módulo
     */
    boolean existsByNegocioIdAndModuloId(Long negocioId, Long moduloId);
}
