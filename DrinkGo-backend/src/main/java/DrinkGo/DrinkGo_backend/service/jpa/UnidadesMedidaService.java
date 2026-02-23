package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.UnidadesMedida;
import DrinkGo.DrinkGo_backend.repository.UnidadesMedidaRepository;
import DrinkGo.DrinkGo_backend.service.IUnidadesMedidaService;

@Service
public class UnidadesMedidaService implements IUnidadesMedidaService {
    @Autowired
    private UnidadesMedidaRepository repoUnidadesMedida;

    public List<UnidadesMedida> buscarTodos() {
        return repoUnidadesMedida.findAll();
    }

    public void guardar(UnidadesMedida unidadesMedida) {
        repoUnidadesMedida.save(unidadesMedida);
    }

    public void modificar(UnidadesMedida unidadesMedida) {
        repoUnidadesMedida.save(unidadesMedida);
    }

    public Optional<UnidadesMedida> buscarId(Long id) {
        return repoUnidadesMedida.findById(id);
    }

    public void eliminar(Long id) {
        repoUnidadesMedida.deleteById(id);
    }
}
