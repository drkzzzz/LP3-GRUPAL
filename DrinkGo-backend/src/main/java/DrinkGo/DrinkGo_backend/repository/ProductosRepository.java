package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Long> {

}
