package DrinkGo.DrinkGo_backend.repository;

import DrinkGo.DrinkGo_backend.entity.DetalleDocumentoFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleDocumentoFacturacionRepository extends JpaRepository<DetalleDocumentoFacturacion, Long> {

    List<DetalleDocumentoFacturacion> findByDocumentoIdOrderByNumeroItemAsc(Long documentoId);

    void deleteByDocumentoId(Long documentoId);
}
