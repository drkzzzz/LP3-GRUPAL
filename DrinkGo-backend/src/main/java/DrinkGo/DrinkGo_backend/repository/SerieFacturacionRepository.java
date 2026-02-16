package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.SerieFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieFacturacionRepository extends JpaRepository<SerieFacturacion, Long> {

    List<SerieFacturacion> findByNegocioId(Long negocioId);

    List<SerieFacturacion> findByNegocioIdAndSedeId(Long negocioId, Long sedeId);

    List<SerieFacturacion> findByNegocioIdAndEstaActivo(Long negocioId, Boolean estaActivo);

    Optional<SerieFacturacion> findByIdAndNegocioId(Long id, Long negocioId);

    /**
     * Bloqueo pesimista para incrementar correlativo de forma atómica.
     * Evita race condition cuando dos requests concurrentes emiten con la misma serie.
     * Usa query nativa porque MariaDB (XAMPP) no soporta "FOR UPDATE OF alias" de Hibernate.
     */
    @Query(value = "SELECT * FROM series_facturacion WHERE id = :id AND negocio_id = :negocioId FOR UPDATE",
            nativeQuery = true)
    Optional<SerieFacturacion> findByIdAndNegocioIdConBloqueo(
            @Param("id") Long id, @Param("negocioId") Long negocioId);

    Optional<SerieFacturacion> findByNegocioIdAndSedeIdAndTipoDocumentoAndPrefijoSerie(
            Long negocioId, Long sedeId,
            SerieFacturacion.TipoDocumento tipoDocumento, String prefijoSerie);

    boolean existsByNegocioIdAndSedeIdAndTipoDocumentoAndPrefijoSerie(
            Long negocioId, Long sedeId,
            SerieFacturacion.TipoDocumento tipoDocumento, String prefijoSerie);

    List<SerieFacturacion> findByNegocioIdAndTipoDocumento(
            Long negocioId, SerieFacturacion.TipoDocumento tipoDocumento);
}
