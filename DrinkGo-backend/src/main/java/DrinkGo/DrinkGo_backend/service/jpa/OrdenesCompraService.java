package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.OrdenesCompra;
import DrinkGo.DrinkGo_backend.repository.OrdenesCompraRepository;
import DrinkGo.DrinkGo_backend.service.IOrdenesCompraService;

@Service
public class OrdenesCompraService implements IOrdenesCompraService {
    @Autowired
    private OrdenesCompraRepository repoOrdenesCompra;

    public List<OrdenesCompra> buscarTodos() {
        return repoOrdenesCompra.findAll();
    }

    public void guardar(OrdenesCompra ordenesCompra) {
        repoOrdenesCompra.save(ordenesCompra);
    }

    public void modificar(OrdenesCompra ordenesCompra) {
        repoOrdenesCompra.save(ordenesCompra);
    }

    public Optional<OrdenesCompra> buscarId(Long id) {
        return repoOrdenesCompra.findById(id);
    }

    public void eliminar(Long id) {
        repoOrdenesCompra.deleteById(id);
    }
}
