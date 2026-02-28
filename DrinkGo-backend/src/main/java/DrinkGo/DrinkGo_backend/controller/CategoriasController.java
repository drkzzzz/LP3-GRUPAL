package DrinkGo.DrinkGo_backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DrinkGo.DrinkGo_backend.entity.Categorias;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.service.ICategoriasService;

@RestController
@RequestMapping("/restful")
public class CategoriasController {
    @Autowired
    private ICategoriasService service;

    @Autowired
    private ProductosRepository productosRepository;

    @GetMapping("/categorias")
    public List<Categorias> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/categorias")
    public Categorias guardar(@RequestBody Categorias entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/categorias")
    public Categorias modificar(@RequestBody Categorias entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/categorias/{id}")
    public Optional<Categorias> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        long count = productosRepository.countByCategoriaId(id);
        if (count > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar la categoría porque tiene " + count + " producto(s) asociado(s)");
        }
        service.eliminar(id);
        return ResponseEntity.ok("Registro eliminado");
    }
}
