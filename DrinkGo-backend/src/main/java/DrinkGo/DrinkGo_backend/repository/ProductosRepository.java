package DrinkGo.DrinkGo_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import DrinkGo.DrinkGo_backend.entity.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Long> {

    @Query("SELECT COUNT(p) FROM Productos p WHERE p.categoria.id = :categoriaId")
    long countByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query("SELECT COUNT(p) FROM Productos p WHERE p.marca.id = :marcaId")
    long countByMarcaId(@Param("marcaId") Long marcaId);
}
