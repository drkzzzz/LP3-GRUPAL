package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import DrinkGo.DrinkGo_backend.entity.Clientes;

public interface ClientesRepository extends JpaRepository<Clientes, Long> {

    Optional<Clientes> findByNegocioIdAndNumeroDocumento(Long negocioId, String numeroDocumento);

    List<Clientes> findByNegocioId(Long negocioId);
}
