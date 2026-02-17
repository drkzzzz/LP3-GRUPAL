package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para clientes (RF-CLI-001..005).
 * Bloque 7 - Gestionado en Bloque 8 por relación con ventas.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Buscar cliente por ID y negocio (Seguridad multi-tenant) */
    Optional<Cliente> findByIdAndNegocioId(Long id, Long negocioId);

    /** Listar todos los clientes de un negocio */
    List<Cliente> findByNegocioId(Long negocioId);

    /** Buscar cliente por documento en un negocio */
    Optional<Cliente> findByNegocioIdAndNumeroDocumento(Long negocioId, String numeroDocumento);

    /** Buscar cliente por email en un negocio */
    Optional<Cliente> findByNegocioIdAndEmail(Long negocioId, String email);

    /** Listar clientes activos de un negocio */
    List<Cliente> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    /** Listar clientes por tipo */
    List<Cliente> findByNegocioIdAndTipoCliente(Long negocioId, String tipoCliente);
}
