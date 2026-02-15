package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.DetalleTransferenciaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleTransferenciaInventarioRepository extends JpaRepository<DetalleTransferenciaInventario, Long> {

    List<DetalleTransferenciaInventario> findByTransferenciaId(Long transferenciaId);
}
