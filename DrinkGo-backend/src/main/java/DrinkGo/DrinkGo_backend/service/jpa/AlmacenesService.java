package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Almacenes;
import DrinkGo.DrinkGo_backend.repository.AlmacenesRepository;
import DrinkGo.DrinkGo_backend.service.IAlmacenesService;

@Service
public class AlmacenesService implements IAlmacenesService {
    @Autowired
    private AlmacenesRepository repoAlmacenes;

    public List<Almacenes> buscarTodos() {
        return repoAlmacenes.findAll();
    }

    public void guardar(Almacenes almacenes) {
        repoAlmacenes.save(almacenes);
    }

    public void modificar(Almacenes almacenes) {
        repoAlmacenes.save(almacenes);
    }

    public Optional<Almacenes> buscarId(Long id) {
        return repoAlmacenes.findById(id);
    }

    public void eliminar(Long id) {
        repoAlmacenes.deleteById(id);
    }
}
