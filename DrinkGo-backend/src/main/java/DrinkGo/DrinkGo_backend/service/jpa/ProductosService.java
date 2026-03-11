package DrinkGo.DrinkGo_backend.service.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.repository.LotesInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.service.IProductosService;

@Service
public class ProductosService implements IProductosService {
    @Autowired
    private ProductosRepository repoProductos;

    @Autowired
    private LotesInventarioRepository repoLotes;

    public List<Productos> buscarTodos() {
        return repoProductos.findAll();
    }

    public void guardar(Productos productos) {
        repoProductos.save(productos);
    }

    public void modificar(Productos productos) {
        repoProductos.save(productos);
    }

    public Optional<Productos> buscarId(Long id) {
        return repoProductos.findById(id);
    }

    public void eliminar(Long id) {
        if (repoLotes.existsByProductoIdAndCantidadActualGreaterThan(id, BigDecimal.ZERO)) {
            throw new IllegalStateException(
                "No se puede eliminar el producto porque tiene lotes con stock en inventario. "
                + "Primero debe agotar o dar de baja el stock existente.");
        }
        repoProductos.deleteById(id);
    }
}
