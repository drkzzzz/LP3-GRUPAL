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

import DrinkGo.DrinkGo_backend.entity.Marcas;
import DrinkGo.DrinkGo_backend.repository.MarcasRepository;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.service.IMarcasService;

@RestController
@RequestMapping("/restful")
public class MarcasController {
    @Autowired
    private IMarcasService service;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private MarcasRepository marcasRepository;

    @GetMapping("/marcas")
    public List<Marcas> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/marcas/negocio/{negocioId}")
    public List<Marcas> buscarPorNegocio(@PathVariable Long negocioId) {
        return marcasRepository.findByNegocioId(negocioId);
    }

    @PostMapping("/marcas")
    public Marcas guardar(@RequestBody Marcas entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/marcas")
    public Marcas modificar(@RequestBody Marcas entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/marcas/{id}")
    public Optional<Marcas> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/marcas/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        long count = productosRepository.countByMarcaId(id);
        if (count > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar la marca porque tiene " + count + " producto(s) asociado(s)");
        }
        service.eliminar(id);
        return ResponseEntity.ok("Registro eliminado");
    }
}
