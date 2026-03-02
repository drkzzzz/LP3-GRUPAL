package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import java.util.List;

public interface GastosRepository extends JpaRepository<Gastos, Long> {
    List<Gastos> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);
}
