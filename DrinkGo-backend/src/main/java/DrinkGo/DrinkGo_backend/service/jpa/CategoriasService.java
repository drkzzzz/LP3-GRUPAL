package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Categorias;
import DrinkGo.DrinkGo_backend.repository.CategoriasRepository;
import DrinkGo.DrinkGo_backend.service.ICategoriasService;

@Service
public class CategoriasService implements ICategoriasService {
    @Autowired
    private CategoriasRepository repoCategorias;

    public List<Categorias> buscarTodos() {
        return repoCategorias.findAll();
    }

    public void guardar(Categorias categorias) {
        repoCategorias.save(categorias);
    }

    public void modificar(Categorias categorias) {
        repoCategorias.save(categorias);
    }

    public Optional<Categorias> buscarId(Long id) {
        return repoCategorias.findById(id);
    }

    public void eliminar(Long id) {
        repoCategorias.deleteById(id);
    }
}
