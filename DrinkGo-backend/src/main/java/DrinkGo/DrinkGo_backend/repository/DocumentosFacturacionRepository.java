package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;

public interface DocumentosFacturacionRepository extends JpaRepository<DocumentosFacturacion, Long> {

    @Query("SELECT d FROM DocumentosFacturacion d WHERE d.negocio.id = :negocioId ORDER BY d.creadoEn DESC")
    List<DocumentosFacturacion> findByNegocioIdOrderByCreadoEnDesc(@Param("negocioId") Long negocioId);

    @Query("SELECT d FROM DocumentosFacturacion d WHERE d.negocio.id = :negocioId AND d.estadoDocumento = :estado")
    List<DocumentosFacturacion> findByNegocioIdAndEstadoDocumento(
            @Param("negocioId") Long negocioId,
            @Param("estado") DocumentosFacturacion.EstadoDocumento estado);

    @Query("SELECT d FROM DocumentosFacturacion d WHERE d.venta.id = :ventaId")
    List<DocumentosFacturacion> findByVentaId(@Param("ventaId") Long ventaId);

    /** Bandeja PSE: comprobantes electrónicos con estados pendientes/rechazados */
    @Query("SELECT d FROM DocumentosFacturacion d WHERE d.negocio.id = :negocioId AND d.modoEmision = :modo AND d.estadoDocumento IN :estados ORDER BY d.creadoEn DESC")
    List<DocumentosFacturacion> findByNegocioIdAndModoEmisionAndEstadoDocumentoInOrderByCreadoEnDesc(
            @Param("negocioId") Long negocioId,
            @Param("modo") DocumentosFacturacion.ModoEmision modo,
            @Param("estados") List<DocumentosFacturacion.EstadoDocumento> estados);

    /** Todos los comprobantes PSE de un negocio */
    @Query("SELECT d FROM DocumentosFacturacion d WHERE d.negocio.id = :negocioId AND d.modoEmision = :modo ORDER BY d.creadoEn DESC")
    List<DocumentosFacturacion> findByNegocioIdAndModoEmisionOrderByCreadoEnDesc(
            @Param("negocioId") Long negocioId,
            @Param("modo") DocumentosFacturacion.ModoEmision modo);

    /** Recovery en startup: buscar todos los docs en un estado específico */
    @Query("SELECT d FROM DocumentosFacturacion d WHERE d.estadoDocumento = :estado")
    List<DocumentosFacturacion> findByEstadoDocumento(@Param("estado") DocumentosFacturacion.EstadoDocumento estado);

    /**
     * Carga un documento con todas sus relaciones LAZY resueltas mediante JOIN FETCH.
     * Necesario para el procesamiento async (fuera de un contexto transaccional activo)
     * y para el simulador PSE que accede a negocio, cliente, serie, etc.
     */
    @Query("SELECT d FROM DocumentosFacturacion d "
            + "JOIN FETCH d.negocio "
            + "JOIN FETCH d.cliente "
            + "JOIN FETCH d.serieFacturacion "
            + "LEFT JOIN FETCH d.usuario "
            + "LEFT JOIN FETCH d.venta "
            + "WHERE d.id = :id AND d.estaActivo = true")
    Optional<DocumentosFacturacion> findByIdWithRelations(@Param("id") Long id);

    /** Busca todas las notas (NC/ND) que referencian un documento original. */
    @Query("SELECT d FROM DocumentosFacturacion d WHERE d.documentoReferencia.id = :docId AND d.estaActivo = true")
    List<DocumentosFacturacion> findNotasByDocumentoReferenciaId(@Param("docId") Long docId);

    /** Suma el total de las notas de crédito aceptadas/pendientes sobre un comprobante. */
    @Query(value = "SELECT COALESCE(SUM(d.total), 0) FROM documentos_facturacion d "
            + "WHERE d.documento_referencia_id = :docId "
            + "AND d.tipo_documento = 'nota_credito' "
            + "AND d.estado_documento <> 'anulado' "
            + "AND d.esta_activo = 1", nativeQuery = true)
    java.math.BigDecimal sumTotalNotasCreditoByDocumentoReferenciaId(@Param("docId") Long docId);
}
