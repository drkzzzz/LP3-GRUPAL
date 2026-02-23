package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.PermisosSistema;
import DrinkGo.DrinkGo_backend.repository.PermisosSistemaRepository;
import DrinkGo.DrinkGo_backend.service.IPermisosSistemaService;

@Service
public class PermisosSistemaService implements IPermisosSistemaService {
    @Autowired
    private PermisosSistemaRepository repoPermisosSistema;

    public List<PermisosSistema> buscarTodos() {
        return repoPermisosSistema.findAll();
    }

    public void guardar(PermisosSistema permisosSistema) {
        repoPermisosSistema.save(permisosSistema);
    }

    public void modificar(PermisosSistema permisosSistema) {
        repoPermisosSistema.save(permisosSistema);
    }

    public Optional<PermisosSistema> buscarId(Long id) {
        return repoPermisosSistema.findById(id);
    }

    public void eliminar(Long id) {
        repoPermisosSistema.deleteById(id);
    }
}
