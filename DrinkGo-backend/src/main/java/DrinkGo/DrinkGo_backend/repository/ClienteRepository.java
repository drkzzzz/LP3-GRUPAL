package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNegocioId(Long negocioId);

    Optional<Cliente> findByIdAndNegocioId(Long id, Long negocioId);

    Optional<Cliente> findByUuid(String uuid);

    boolean existsByNegocioIdAndTipoDocumentoAndNumeroDocumento(
            Long negocioId, Cliente.TipoDocumento tipoDocumento, String numeroDocumento);

    boolean existsByNegocioIdAndEmail(Long negocioId, String email);
}