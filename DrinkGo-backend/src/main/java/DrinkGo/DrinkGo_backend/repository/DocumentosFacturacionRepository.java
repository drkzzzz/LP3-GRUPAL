package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;

public interface DocumentosFacturacionRepository extends JpaRepository<DocumentosFacturacion, Long> {

    List<DocumentosFacturacion> findByNegocioIdOrderByCreadoEnDesc(Long negocioId);

    List<DocumentosFacturacion> findByNegocioIdAndEstadoDocumento(
            Long negocioId, DocumentosFacturacion.EstadoDocumento estadoDocumento);

    List<DocumentosFacturacion> findByVentaId(Long ventaId);

    /** Bandeja PSE: comprobantes electrónicos con estados pendientes/rechazados */
    List<DocumentosFacturacion> findByNegocioIdAndModoEmisionAndEstadoDocumentoInOrderByCreadoEnDesc(
            Long negocioId,
            DocumentosFacturacion.ModoEmision modoEmision,
            List<DocumentosFacturacion.EstadoDocumento> estados);

    /** Todos los comprobantes PSE de un negocio */
    List<DocumentosFacturacion> findByNegocioIdAndModoEmisionOrderByCreadoEnDesc(
            Long negocioId, DocumentosFacturacion.ModoEmision modoEmision);
}
