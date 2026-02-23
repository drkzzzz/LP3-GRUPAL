package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Gastos;

public interface GastosRepository extends JpaRepository<Gastos, Long> {

}
