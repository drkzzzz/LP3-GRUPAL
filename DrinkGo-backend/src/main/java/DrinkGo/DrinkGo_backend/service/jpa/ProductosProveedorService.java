package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.ProductosProveedor;
import DrinkGo.DrinkGo_backend.repository.ProductosProveedorRepository;
import DrinkGo.DrinkGo_backend.service.IProductosProveedorService;

@Service
public class ProductosProveedorService implements IProductosProveedorService {
    @Autowired
    private ProductosProveedorRepository repoProductosProveedor;

    public List<ProductosProveedor> buscarTodos() {
        return repoProductosProveedor.findAll();
    }

    public void guardar(ProductosProveedor productosProveedor) {
        repoProductosProveedor.save(productosProveedor);
    }

    public void modificar(ProductosProveedor productosProveedor) {
        repoProductosProveedor.save(productosProveedor);
    }

    public Optional<ProductosProveedor> buscarId(Long id) {
        return repoProductosProveedor.findById(id);
    }

    public void eliminar(Long id) {
        repoProductosProveedor.deleteById(id);
    }
}
