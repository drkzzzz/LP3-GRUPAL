package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.CategoriasGasto;
import DrinkGo.DrinkGo_backend.repository.CategoriasGastoRepository;
import DrinkGo.DrinkGo_backend.service.ICategoriasGastoService;

@Service
public class CategoriasGastoService implements ICategoriasGastoService {
    @Autowired
    private CategoriasGastoRepository repoCategoriasGasto;

    public List<CategoriasGasto> buscarTodos() {
        return repoCategoriasGasto.findAll();
    }

    public void guardar(CategoriasGasto categoriasGasto) {
        repoCategoriasGasto.save(categoriasGasto);
    }

    public void modificar(CategoriasGasto categoriasGasto) {
        repoCategoriasGasto.save(categoriasGasto);
    }

    public Optional<CategoriasGasto> buscarId(Long id) {
        return repoCategoriasGasto.findById(id);
    }

    public void eliminar(Long id) {
        repoCategoriasGasto.deleteById(id);
    }
}
