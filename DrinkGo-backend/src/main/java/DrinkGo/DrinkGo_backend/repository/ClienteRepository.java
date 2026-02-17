package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente (RF-CLI-001..005).
 * Implementa seguridad multi-tenant mediante negocioId.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // --- Búsquedas Únicas ---
    
    Optional<Cliente> findByIdAndNegocioId(Long id, Long negocioId);

    Optional<Cliente> findByUuid(String uuid);

    Optional<Cliente> findByNegocioIdAndNumeroDocumento(Long negocioId, String numeroDocumento);

    Optional<Cliente> findByNegocioIdAndEmail(Long negocioId, String email);

    // --- Listados ---

    List<Cliente> findByNegocioId(Long negocioId);

    List<Cliente> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    // Nota: Usamos Cliente.TipoCliente porque es un Enum
    List<Cliente> findByNegocioIdAndTipoCliente(Long negocioId, Cliente.TipoCliente tipoCliente);

    // --- Validaciones de Existencia ---

    boolean existsByNegocioIdAndTipoDocumentoAndNumeroDocumento(
            Long negocioId, Cliente.TipoDocumento tipoDocumento, String numeroDocumento);

    boolean existsByNegocioIdAndEmail(Long negocioId, String email);
}