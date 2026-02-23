package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.FacturasSuscripcion;
import DrinkGo.DrinkGo_backend.repository.FacturasSuscripcionRepository;
import DrinkGo.DrinkGo_backend.service.IFacturasSuscripcionService;

@Service
public class FacturasSuscripcionService implements IFacturasSuscripcionService {
    @Autowired
    private FacturasSuscripcionRepository repoFacturasSuscripcion;

    public List<FacturasSuscripcion> buscarTodos() {
        return repoFacturasSuscripcion.findAll();
    }

    public void guardar(FacturasSuscripcion facturasSuscripcion) {
        repoFacturasSuscripcion.save(facturasSuscripcion);
    }

    public void modificar(FacturasSuscripcion facturasSuscripcion) {
        repoFacturasSuscripcion.save(facturasSuscripcion);
    }

    public Optional<FacturasSuscripcion> buscarId(Long id) {
        return repoFacturasSuscripcion.findById(id);
    }

    public void eliminar(Long id) {
        repoFacturasSuscripcion.deleteById(id);
    }
}
