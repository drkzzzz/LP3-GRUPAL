package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.service.IProductosService;

@Service
public class ProductosService implements IProductosService {
    @Autowired
    private ProductosRepository repoProductos;

    @Transactional(readOnly = true)
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
        repoProductos.deleteById(id);
    }
}
