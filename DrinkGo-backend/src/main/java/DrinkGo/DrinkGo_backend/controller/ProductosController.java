package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.service.IProductosService;
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
public class ProductosController {
    @Autowired
    private IProductosService service;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/productos")
    public List<Productos> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/productos/negocio/{negocioId}")
    public List<Productos> buscarPorNegocio(@PathVariable Long negocioId) {
        return productosRepository.findByNegocioId(negocioId);
    }

    @PostMapping("/productos")
    public Productos guardar(@RequestBody Productos entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/productos")
    public Productos modificar(@RequestBody Productos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/productos/{id}")
    public Optional<Productos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/productos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
