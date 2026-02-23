package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.DetalleDocumentosFacturacion;
import DrinkGo.DrinkGo_backend.repository.DetalleDocumentosFacturacionRepository;
import DrinkGo.DrinkGo_backend.service.IDetalleDocumentosFacturacionService;

@Service
public class DetalleDocumentosFacturacionService implements IDetalleDocumentosFacturacionService {
    @Autowired
    private DetalleDocumentosFacturacionRepository repoDetalleDocumentosFacturacion;

    public List<DetalleDocumentosFacturacion> buscarTodos() {
        return repoDetalleDocumentosFacturacion.findAll();
    }

    public void guardar(DetalleDocumentosFacturacion detalleDocumentosFacturacion) {
        repoDetalleDocumentosFacturacion.save(detalleDocumentosFacturacion);
    }

    public void modificar(DetalleDocumentosFacturacion detalleDocumentosFacturacion) {
        repoDetalleDocumentosFacturacion.save(detalleDocumentosFacturacion);
    }

    public Optional<DetalleDocumentosFacturacion> buscarId(Long id) {
        return repoDetalleDocumentosFacturacion.findById(id);
    }

    public void eliminar(Long id) {
        repoDetalleDocumentosFacturacion.deleteById(id);
    }
}
