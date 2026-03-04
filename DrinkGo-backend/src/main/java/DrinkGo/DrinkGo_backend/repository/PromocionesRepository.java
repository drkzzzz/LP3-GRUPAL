package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import DrinkGo.DrinkGo_backend.entity.Promociones;

public interface PromocionesRepository extends JpaRepository<Promociones, Long> {
    List<Promociones> findByNegocioId(Long negocioId);
}
