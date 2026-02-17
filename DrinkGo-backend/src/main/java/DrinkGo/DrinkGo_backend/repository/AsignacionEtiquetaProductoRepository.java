package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.AsignacionEtiquetaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionEtiquetaProductoRepository
        extends JpaRepository<AsignacionEtiquetaProducto, AsignacionEtiquetaProducto.AsignacionEtiquetaProductoId> {

    List<AsignacionEtiquetaProducto> findByProductoId(Long productoId);

    List<AsignacionEtiquetaProducto> findByEtiquetaId(Long etiquetaId);

    void deleteByProductoId(Long productoId);

    void deleteByEtiquetaId(Long etiquetaId);

    void deleteByProductoIdAndEtiquetaId(Long productoId, Long etiquetaId);

    @Query("SELECT aep FROM AsignacionEtiquetaProducto aep WHERE aep.etiquetaId IN :etiquetaIds")
    List<AsignacionEtiquetaProducto> findByEtiquetaIdIn(@Param("etiquetaIds") List<Long> etiquetaIds);
}
