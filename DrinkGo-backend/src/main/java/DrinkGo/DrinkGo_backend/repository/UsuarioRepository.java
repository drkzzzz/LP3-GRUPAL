package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByUuid(UUID uuid);
    
    Optional<Usuario> findByTenantIdAndEmail(Long tenantId, String email);
    
    Optional<Usuario> findByIdAndTenantId(Long id, Long tenantId);
    
    Page<Usuario> findByTenantIdAndActivoTrue(Long tenantId, Pageable pageable);
    
    Page<Usuario> findByTenantId(Long tenantId, Pageable pageable);
    
    @Query("SELECT u FROM Usuario u WHERE u.tenantId = :tenantId AND u.activo = true " +
           "AND (LOWER(u.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Usuario> buscarUsuarios(@Param("tenantId") Long tenantId, @Param("busqueda") String busqueda);
    
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE u.tenantId = :tenantId AND r.codigo = :rolCodigo AND u.activo = true")
    List<Usuario> findByTenantIdAndRol(@Param("tenantId") Long tenantId, @Param("rolCodigo") String rolCodigo);
    
    @Query("SELECT u FROM Usuario u JOIN u.sedes s WHERE s.id = :sedeId AND u.activo = true")
    List<Usuario> findBySedeId(@Param("sedeId") Long sedeId);
    
    @Query("SELECT u FROM Usuario u WHERE u.tenantId = :tenantId AND u.pinRapido = :pin AND u.activo = true")
    Optional<Usuario> findByPinRapido(@Param("tenantId") Long tenantId, @Param("pin") String pin);
    
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoAcceso = :fecha WHERE u.id = :id")
    void actualizarUltimoAcceso(@Param("id") Long id, @Param("fecha") OffsetDateTime fecha);
    
    boolean existsByTenantIdAndEmail(Long tenantId, String email);
    
    long countByTenantIdAndActivoTrue(Long tenantId);
}
