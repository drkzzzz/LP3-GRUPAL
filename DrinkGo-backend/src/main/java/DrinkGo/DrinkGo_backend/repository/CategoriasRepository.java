package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import DrinkGo.DrinkGo_backend.entity.Categorias;

public interface CategoriasRepository extends JpaRepository<Categorias, Long> {

    List<Categorias> findByNegocioId(Long negocioId);
}
