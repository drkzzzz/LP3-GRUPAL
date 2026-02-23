package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Gastos;
import DrinkGo.DrinkGo_backend.repository.GastosRepository;
import DrinkGo.DrinkGo_backend.service.IGastosService;

@Service
public class GastosService implements IGastosService {
    @Autowired
    private GastosRepository repoGastos;

    public List<Gastos> buscarTodos() {
        return repoGastos.findAll();
    }

    public void guardar(Gastos gastos) {
        repoGastos.save(gastos);
    }

    public void modificar(Gastos gastos) {
        repoGastos.save(gastos);
    }

    public Optional<Gastos> buscarId(Long id) {
        return repoGastos.findById(id);
    }

    public void eliminar(Long id) {
        repoGastos.deleteById(id);
    }
}
