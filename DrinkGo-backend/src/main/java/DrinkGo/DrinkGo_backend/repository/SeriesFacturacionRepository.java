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

    Optional<SeriesFacturacion> findFirstByNegocioIdAndTipoDocumentoAndEsPredeterminada(
            Long negocioId, SeriesFacturacion.TipoDocumento tipoDocumento, Boolean esPredeterminada);

    /**
     * Finds the default active serie for a negocio + tipoDocumento whose serie code
     * starts with the given prefix (e.g. "BC" for NC on boleta, "FC" for NC on factura).
     */
    @Query(value = "SELECT * FROM series_facturacion " +
                   "WHERE negocio_id = :negocioId " +
                   "AND tipo_documento = :tipoDoc " +
                   "AND serie LIKE CONCAT(:prefix, '%') " +
                   "AND es_predeterminada = 1 " +
                   "AND esta_activo = 1 " +
                   "LIMIT 1", nativeQuery = true)
    Optional<SeriesFacturacion> findDefaultByNegocioIdAndTipoAndPrefix(
            @Param("negocioId") Long negocioId,
            @Param("tipoDoc") String tipoDoc,
            @Param("prefix") String prefix);

    @Query(value = "SELECT * FROM series_facturacion WHERE id = :id FOR UPDATE", nativeQuery = true)
    Optional<SeriesFacturacion> findByIdForUpdate(@Param("id") Long id);

    boolean existsByNegocioIdAndSerie(Long negocioId, String serie);
}
