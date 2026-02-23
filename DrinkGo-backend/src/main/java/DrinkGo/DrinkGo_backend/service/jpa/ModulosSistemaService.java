package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.ModulosSistema;
import DrinkGo.DrinkGo_backend.repository.ModulosSistemaRepository;
import DrinkGo.DrinkGo_backend.service.IModulosSistemaService;

@Service
public class ModulosSistemaService implements IModulosSistemaService {
    @Autowired
    private ModulosSistemaRepository repoModulosSistema;

    public List<ModulosSistema> buscarTodos() {
        return repoModulosSistema.findAll();
    }

    public void guardar(ModulosSistema modulosSistema) {
        repoModulosSistema.save(modulosSistema);
    }

    public void modificar(ModulosSistema modulosSistema) {
        repoModulosSistema.save(modulosSistema);
    }

    public Optional<ModulosSistema> buscarId(Long id) {
        return repoModulosSistema.findById(id);
    }

    public void eliminar(Long id) {
        repoModulosSistema.deleteById(id);
    }
}
