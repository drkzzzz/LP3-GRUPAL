package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.DetalleDocumentosFacturacion;

public interface IDetalleDocumentosFacturacionService {
    List<DetalleDocumentosFacturacion> buscarTodos();

    void guardar(DetalleDocumentosFacturacion detalleDocumentosFacturacion);

    void modificar(DetalleDocumentosFacturacion detalleDocumentosFacturacion);

    Optional<DetalleDocumentosFacturacion> buscarId(Long id);

    void eliminar(Long id);
}
