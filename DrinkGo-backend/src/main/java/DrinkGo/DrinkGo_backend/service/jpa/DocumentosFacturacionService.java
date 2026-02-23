package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;
import DrinkGo.DrinkGo_backend.repository.DocumentosFacturacionRepository;
import DrinkGo.DrinkGo_backend.service.IDocumentosFacturacionService;

@Service
public class DocumentosFacturacionService implements IDocumentosFacturacionService {
    @Autowired
    private DocumentosFacturacionRepository repoDocumentosFacturacion;

    public List<DocumentosFacturacion> buscarTodos() {
        return repoDocumentosFacturacion.findAll();
    }

    public void guardar(DocumentosFacturacion documentosFacturacion) {
        repoDocumentosFacturacion.save(documentosFacturacion);
    }

    public void modificar(DocumentosFacturacion documentosFacturacion) {
        repoDocumentosFacturacion.save(documentosFacturacion);
    }

    public Optional<DocumentosFacturacion> buscarId(Long id) {
        return repoDocumentosFacturacion.findById(id);
    }

    public void eliminar(Long id) {
        repoDocumentosFacturacion.deleteById(id);
    }
}
