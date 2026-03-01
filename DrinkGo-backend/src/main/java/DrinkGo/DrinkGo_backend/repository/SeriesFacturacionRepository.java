package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.SeriesFacturacion;

public interface SeriesFacturacionRepository extends JpaRepository<SeriesFacturacion, Long> {

    List<SeriesFacturacion> findByNegocioId(Long negocioId);

    List<SeriesFacturacion> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    Optional<SeriesFacturacion> findByNegocioIdAndTipoDocumentoAndEsPredeterminada(
            Long negocioId, SeriesFacturacion.TipoDocumento tipoDocumento, Boolean esPredeterminada);

    @Query(value = "SELECT * FROM series_facturacion WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<SeriesFacturacion> findByIdForUpdate(@Param("id") Long id);

    boolean existsByNegocioIdAndSerie(Long negocioId, String serie);
}
