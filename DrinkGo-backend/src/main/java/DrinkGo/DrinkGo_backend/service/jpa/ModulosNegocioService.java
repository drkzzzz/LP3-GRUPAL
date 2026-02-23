package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.ModulosNegocio;
import DrinkGo.DrinkGo_backend.repository.ModulosNegocioRepository;
import DrinkGo.DrinkGo_backend.service.IModulosNegocioService;

@Service
public class ModulosNegocioService implements IModulosNegocioService {
    @Autowired
    private ModulosNegocioRepository repoModulosNegocio;

    public List<ModulosNegocio> buscarTodos() {
        return repoModulosNegocio.findAll();
    }

    public void guardar(ModulosNegocio modulosNegocio) {
        repoModulosNegocio.save(modulosNegocio);
    }

    public void modificar(ModulosNegocio modulosNegocio) {
        repoModulosNegocio.save(modulosNegocio);
    }

    public Optional<ModulosNegocio> buscarId(Long id) {
        return repoModulosNegocio.findById(id);
    }

    public void eliminar(Long id) {
        repoModulosNegocio.deleteById(id);
    }
}
