package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.MovimientosCaja;
import DrinkGo.DrinkGo_backend.service.IMovimientosCajaService;
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
public class MovimientosCajaController {
    @Autowired
    private IMovimientosCajaService service;

    @GetMapping("/movimientos-caja")
    public List<MovimientosCaja> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/movimientos-caja")
    public MovimientosCaja guardar(@RequestBody MovimientosCaja entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/movimientos-caja")
    public MovimientosCaja modificar(@RequestBody MovimientosCaja entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/movimientos-caja/{id}")
    public Optional<MovimientosCaja> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/movimientos-caja/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
