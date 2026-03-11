package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.OrdenesCompra;

public interface OrdenesCompraRepository extends JpaRepository<OrdenesCompra, Long> {

    List<OrdenesCompra> findByNegocioId(Long negocioId);

}
