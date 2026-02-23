package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.ProductosProveedor;
import DrinkGo.DrinkGo_backend.service.IProductosProveedorService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class ProductosProveedorController {
    @Autowired
    private IProductosProveedorService service;

    @GetMapping("/productos-proveedor")
    public List<ProductosProveedor> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/productos-proveedor")
    public ProductosProveedor guardar(@RequestBody ProductosProveedor entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/productos-proveedor")
    public ProductosProveedor modificar(@RequestBody ProductosProveedor entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/productos-proveedor/{id}")
    public Optional<ProductosProveedor> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/productos-proveedor/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
