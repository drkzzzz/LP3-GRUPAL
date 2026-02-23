package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.CondicionesPromocion;
import DrinkGo.DrinkGo_backend.service.ICondicionesPromocionService;
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
public class CondicionesPromocionController {
    @Autowired
    private ICondicionesPromocionService service;

    @GetMapping("/condiciones-promocion")
    public List<CondicionesPromocion> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/condiciones-promocion")
    public CondicionesPromocion guardar(@RequestBody CondicionesPromocion entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/condiciones-promocion")
    public CondicionesPromocion modificar(@RequestBody CondicionesPromocion entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/condiciones-promocion/{id}")
    public Optional<CondicionesPromocion> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/condiciones-promocion/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
