package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import DrinkGo.DrinkGo_backend.entity.PromocionSede;
import DrinkGo.DrinkGo_backend.entity.PromocionSedeId;

@Repository
public interface PromocionSedeRepository extends JpaRepository<PromocionSede, PromocionSedeId> {

    List<PromocionSede> findByPromocionId(Long promocionId);

    void deleteByPromocionId(Long promocionId);
}