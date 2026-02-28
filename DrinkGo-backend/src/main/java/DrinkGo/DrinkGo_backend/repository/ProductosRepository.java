package DrinkGo.DrinkGo_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import DrinkGo.DrinkGo_backend.entity.Productos;

public interface ProductosRepository extends JpaRepository<Productos, Long> {

    List<Productos> findByNegocioId(Long negocioId);

    @Query("SELECT COUNT(p) FROM Productos p WHERE p.categoria.id = :categoriaId")
    long countByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query("SELECT COUNT(p) FROM Productos p WHERE p.marca.id = :marcaId")
    long countByMarcaId(@Param("marcaId") Long marcaId);

    @Query("SELECT COUNT(p) FROM Productos p WHERE p.unidadMedida.id = :unidadMedidaId")
    long countByUnidadMedidaId(@Param("unidadMedidaId") Long unidadMedidaId);
}
