package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.DetalleOrdenesCompra;
import DrinkGo.DrinkGo_backend.repository.DetalleOrdenesCompraRepository;
import DrinkGo.DrinkGo_backend.service.IDetalleOrdenesCompraService;

@Service
public class DetalleOrdenesCompraService implements IDetalleOrdenesCompraService {
    @Autowired
    private DetalleOrdenesCompraRepository repoDetalleOrdenesCompra;

    public List<DetalleOrdenesCompra> buscarTodos() {
        return repoDetalleOrdenesCompra.findAll();
    }

    public void guardar(DetalleOrdenesCompra detalleOrdenesCompra) {
        repoDetalleOrdenesCompra.save(detalleOrdenesCompra);
    }

    public void modificar(DetalleOrdenesCompra detalleOrdenesCompra) {
        repoDetalleOrdenesCompra.save(detalleOrdenesCompra);
    }

    public Optional<DetalleOrdenesCompra> buscarId(Long id) {
        return repoDetalleOrdenesCompra.findById(id);
    }

    public void eliminar(Long id) {
        repoDetalleOrdenesCompra.deleteById(id);
    }
}
