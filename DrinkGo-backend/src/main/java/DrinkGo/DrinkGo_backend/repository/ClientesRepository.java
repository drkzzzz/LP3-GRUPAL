package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.Clientes;

public interface ClientesRepository extends JpaRepository<Clientes, Long> {

}
