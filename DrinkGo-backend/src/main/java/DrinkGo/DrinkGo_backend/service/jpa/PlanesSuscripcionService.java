package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.PlanesSuscripcion;
import DrinkGo.DrinkGo_backend.repository.PlanesSuscripcionRepository;
import DrinkGo.DrinkGo_backend.service.IPlanesSuscripcionService;

@Service
public class PlanesSuscripcionService implements IPlanesSuscripcionService {
    @Autowired
    private PlanesSuscripcionRepository repoPlanesSuscripcion;

    public List<PlanesSuscripcion> buscarTodos() {
        return repoPlanesSuscripcion.findAll();
    }

    public void guardar(PlanesSuscripcion planesSuscripcion) {
        repoPlanesSuscripcion.save(planesSuscripcion);
    }

    public void modificar(PlanesSuscripcion planesSuscripcion) {
        repoPlanesSuscripcion.save(planesSuscripcion);
    }

    public Optional<PlanesSuscripcion> buscarId(Long id) {
        return repoPlanesSuscripcion.findById(id);
    }

    public void eliminar(Long id) {
        repoPlanesSuscripcion.deleteById(id);
    }
}
