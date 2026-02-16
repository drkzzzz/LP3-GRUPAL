package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.DocumentoFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoFacturacionRepository extends JpaRepository<DocumentoFacturacion, Long> {

    List<DocumentoFacturacion> findByNegocioId(Long negocioId);

    Optional<DocumentoFacturacion> findByIdAndNegocioId(Long id, Long negocioId);

    List<DocumentoFacturacion> findByNegocioIdAndSedeId(Long negocioId, Long sedeId);

    List<DocumentoFacturacion> findByNegocioIdAndTipoDocumento(
            Long negocioId, DocumentoFacturacion.TipoDocumento tipoDocumento);

    List<DocumentoFacturacion> findByNegocioIdAndEstado(
            Long negocioId, DocumentoFacturacion.EstadoDocumento estado);

    List<DocumentoFacturacion> findByNegocioIdAndEstadoSunat(
            Long negocioId, DocumentoFacturacion.EstadoSunat estadoSunat);

    Optional<DocumentoFacturacion> findByNegocioIdAndNumeroCompleto(
            Long negocioId, String numeroCompleto);

    @Query("SELECT d FROM DocumentoFacturacion d WHERE d.negocioId = :negocioId " +
           "AND (:sedeId IS NULL OR d.sedeId = :sedeId) " +
           "AND (:tipoDocumento IS NULL OR d.tipoDocumento = :tipoDocumento) " +
           "AND (:estado IS NULL OR d.estado = :estado) " +
           "AND (:estadoSunat IS NULL OR d.estadoSunat = :estadoSunat) " +
           "AND (:fechaDesde IS NULL OR d.fechaEmision >= :fechaDesde) " +
           "AND (:fechaHasta IS NULL OR d.fechaEmision <= :fechaHasta) " +
           "AND (:numeroCompleto IS NULL OR d.numeroCompleto LIKE CONCAT('%', :numeroCompleto, '%')) " +
           "ORDER BY d.creadoEn DESC")
    List<DocumentoFacturacion> buscarDocumentos(
            @Param("negocioId") Long negocioId,
            @Param("sedeId") Long sedeId,
            @Param("tipoDocumento") DocumentoFacturacion.TipoDocumento tipoDocumento,
            @Param("estado") DocumentoFacturacion.EstadoDocumento estado,
            @Param("estadoSunat") DocumentoFacturacion.EstadoSunat estadoSunat,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta,
            @Param("numeroCompleto") String numeroCompleto);

    List<DocumentoFacturacion> findByNegocioIdAndFechaEmisionBetween(
            Long negocioId, LocalDate fechaDesde, LocalDate fechaHasta);

    /**
     * Buscar documentos activos (no anulados) asociados a un pedido específico.
     * Usado para validar que un pedido no sea facturado más de una vez.
     */
    List<DocumentoFacturacion> findByNegocioIdAndPedidoIdAndEstadoNot(
            Long negocioId, Long pedidoId, DocumentoFacturacion.EstadoDocumento estado);

    @Query("SELECT COUNT(d) FROM DocumentoFacturacion d WHERE d.negocioId = :negocioId AND d.estado <> 'anulado'")
    long contarDocumentosActivos(@Param("negocioId") Long negocioId);
}
