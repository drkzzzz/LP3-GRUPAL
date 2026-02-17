package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.CondicionPromocion;

@Repository
public interface CondicionPromocionRepository extends JpaRepository<CondicionPromocion, Long> {

    List<CondicionPromocion> findByPromocionId(Long promocionId);

    void deleteByPromocionId(Long promocionId);
}