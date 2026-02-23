package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;

public interface IDocumentosFacturacionService {
    List<DocumentosFacturacion> buscarTodos();
    void guardar(DocumentosFacturacion documentosFacturacion);
    void modificar(DocumentosFacturacion documentosFacturacion);
    Optional<DocumentosFacturacion> buscarId(Long id);
    void eliminar(Long id);
}
