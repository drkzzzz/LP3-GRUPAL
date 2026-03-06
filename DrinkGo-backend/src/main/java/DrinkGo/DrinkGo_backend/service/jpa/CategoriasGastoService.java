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

    public List<CategoriasGasto> buscarPorNegocio(Long negocioId) {
        return repoCategoriasGasto.findByNegocioId(negocioId);
    }

    public void guardar(CategoriasGasto categoriasGasto) {
        if (categoriasGasto.getCodigo() == null || categoriasGasto.getCodigo().isBlank()) {
            categoriasGasto.setCodigo(generarCodigo(categoriasGasto.getNombre()));
        }
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

    private String generarCodigo(String nombre) {
        if (nombre == null || nombre.isBlank()) return "CAT-" + System.currentTimeMillis();
        String base = nombre.trim().toUpperCase()
                .replaceAll("[^A-Z0-9]", "");
        if (base.length() > 5) base = base.substring(0, 5);
        return base + "-" + (System.currentTimeMillis() % 10000);
    }
}
