package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.DetalleDevoluciones;
import DrinkGo.DrinkGo_backend.repository.DetalleDevolucionesRepository;
import DrinkGo.DrinkGo_backend.service.IDetalleDevolucionesService;

@Service
public class DetalleDevolucionesService implements IDetalleDevolucionesService {
    @Autowired
    private DetalleDevolucionesRepository repoDetalleDevoluciones;

    public List<DetalleDevoluciones> buscarTodos() {
        return repoDetalleDevoluciones.findAll();
    }

    public void guardar(DetalleDevoluciones detalleDevoluciones) {
        repoDetalleDevoluciones.save(detalleDevoluciones);
    }

    public void modificar(DetalleDevoluciones detalleDevoluciones) {
        repoDetalleDevoluciones.save(detalleDevoluciones);
    }

    public Optional<DetalleDevoluciones> buscarId(Long id) {
        return repoDetalleDevoluciones.findById(id);
    }

    public void eliminar(Long id) {
        repoDetalleDevoluciones.deleteById(id);
    }
}
