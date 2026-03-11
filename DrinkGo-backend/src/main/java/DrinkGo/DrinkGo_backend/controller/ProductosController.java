package DrinkGo.DrinkGo_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.service.IProductosService;

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

    @GetMapping("/productos/sede/{sedeId}")
    public List<Productos> buscarPorSede(@PathVariable Long sedeId) {
        return productosRepository.findBySedeId(sedeId);
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
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok("Registro eliminado");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
