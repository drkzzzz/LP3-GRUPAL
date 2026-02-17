package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.DetalleCombo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleComboRepository extends JpaRepository<DetalleCombo, Long> {

    List<DetalleCombo> findByComboId(Long comboId);

    List<DetalleCombo> findByProductoId(Long productoId);

    List<DetalleCombo> findByComboIdOrderByOrdenAsc(Long comboId);

    void deleteByComboId(Long comboId);
}
