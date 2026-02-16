package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // --- Búsquedas básicas ---
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByUuid(String uuid);
    
    Optional<Usuario> findByNegocioIdAndEmail(Long negocioId, String email);
    
    Optional<Usuario> findByIdAndNegocioId(Long id, Long negocioId);
    
    // --- Paginación y Listados ---
    Page<Usuario> findByNegocioIdAndEstaActivoTrue(Long negocioId, Pageable pageable);
    
    Page<Usuario> findByNegocioId(Long negocioId, Pageable pageable);
    
    List<Usuario> findByNegocioId(Long negocioId);

    // --- Búsquedas Avanzadas (Roles y Sedes) ---
    @Query("SELECT u FROM Usuario u WHERE u.negocioId = :negocioId AND u.estaActivo = true " +
           "AND (LOWER(u.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Usuario> buscarUsuarios(@Param("negocioId") Long negocioId, @Param("busqueda") String busqueda);
    
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE u.negocioId = :negocioId AND r.codigo = :rolCodigo AND u.estaActivo = true")
    List<Usuario> findByNegocioIdAndRol(@Param("negocioId") Long negocioId, @Param("rolCodigo") String rolCodigo);
    
    @Query("SELECT u FROM Usuario u JOIN u.sedes s WHERE s.id = :sedeId AND u.estaActivo = true")
    List<Usuario> findBySedeId(@Param("sedeId") Long sedeId);
    
    // --- Seguridad y Actualización ---
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoAccesoEn = :fecha WHERE u.id = :id")
    void actualizarUltimoAcceso(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
    
    boolean existsByEmail(String email);
    
    boolean existsByNegocioIdAndEmail(Long negocioId, String email);
    
    long countByNegocioIdAndEstaActivoTrue(Long negocioId);
}