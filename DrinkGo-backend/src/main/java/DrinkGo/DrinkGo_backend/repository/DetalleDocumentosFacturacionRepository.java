package DrinkGo.DrinkGo_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import DrinkGo.DrinkGo_backend.entity.DetalleDocumentosFacturacion;

public interface DetalleDocumentosFacturacionRepository extends JpaRepository<DetalleDocumentosFacturacion, Long> {

    @Query("SELECT d FROM DetalleDocumentosFacturacion d WHERE d.documentoFacturacion.id = :docId")
    List<DetalleDocumentosFacturacion> findByDocumentoFacturacionId(@Param("docId") Long docId);

}
