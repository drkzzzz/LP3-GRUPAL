package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.DetalleCombos;
import DrinkGo.DrinkGo_backend.repository.DetalleCombosRepository;
import DrinkGo.DrinkGo_backend.service.IDetalleCombosService;
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
public class DetalleCombosController {
    @Autowired
    private IDetalleCombosService service;

    @Autowired
    private DetalleCombosRepository detalleCombosRepository;

    @GetMapping("/detalle-combos")
    public List<DetalleCombos> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/detalle-combos/negocio/{negocioId}")
    public List<DetalleCombos> buscarPorNegocio(@PathVariable Long negocioId) {
        return detalleCombosRepository.findByComboNegocioId(negocioId);
    }

    @PostMapping("/detalle-combos")
    public DetalleCombos guardar(@RequestBody DetalleCombos entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/detalle-combos")
    public DetalleCombos modificar(@RequestBody DetalleCombos entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/detalle-combos/{id}")
    public Optional<DetalleCombos> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/detalle-combos/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
