package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Combos;
import DrinkGo.DrinkGo_backend.repository.CombosRepository;
import DrinkGo.DrinkGo_backend.service.ICombosService;
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
public class CombosController {
    @Autowired
    private ICombosService service;

    @Autowired
    private CombosRepository combosRepository;

    @GetMapping("/combos")
    public List<Combos> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/combos/negocio/{negocioId}")
    public List<Combos> buscarPorNegocio(@PathVariable Long negocioId) {
        return combosRepository.findByNegocioId(negocioId);
    }

    @PostMapping("/combos")
    public Combos guardar(@RequestBody Combos entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/combos")
    public Combos modificar(@RequestBody Combos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/combos/{id}")
    public Optional<Combos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/combos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
