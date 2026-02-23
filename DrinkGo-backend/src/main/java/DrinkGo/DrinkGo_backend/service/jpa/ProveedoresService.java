package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Proveedores;
import DrinkGo.DrinkGo_backend.repository.ProveedoresRepository;
import DrinkGo.DrinkGo_backend.service.IProveedoresService;

@Service
public class ProveedoresService implements IProveedoresService {
    @Autowired
    private ProveedoresRepository repoProveedores;

    public List<Proveedores> buscarTodos() {
        return repoProveedores.findAll();
    }

    public void guardar(Proveedores proveedores) {
        repoProveedores.save(proveedores);
    }

    public void modificar(Proveedores proveedores) {
        repoProveedores.save(proveedores);
    }

    public Optional<Proveedores> buscarId(Long id) {
        return repoProveedores.findById(id);
    }

    public void eliminar(Long id) {
        repoProveedores.deleteById(id);
    }
}
