package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.repository.SedesRepository;
import DrinkGo.DrinkGo_backend.service.ISedesService;

@Service
public class SedesService implements ISedesService {
    @Autowired
    private SedesRepository repoSedes;

    public List<Sedes> buscarTodos() {
        return repoSedes.findAll();
    }

    public void guardar(Sedes sedes) {
        repoSedes.save(sedes);
    }

    public void modificar(Sedes sedes) {
        repoSedes.save(sedes);
    }

    public Optional<Sedes> buscarId(Long id) {
        return repoSedes.findById(id);
    }

    public void eliminar(Long id) {
        repoSedes.deleteById(id);
    }
}
