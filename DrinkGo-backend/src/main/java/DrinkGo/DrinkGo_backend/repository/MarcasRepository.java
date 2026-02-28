package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import DrinkGo.DrinkGo_backend.entity.Marcas;

public interface MarcasRepository extends JpaRepository<Marcas, Long> {

    List<Marcas> findByNegocioId(Long negocioId);
}
