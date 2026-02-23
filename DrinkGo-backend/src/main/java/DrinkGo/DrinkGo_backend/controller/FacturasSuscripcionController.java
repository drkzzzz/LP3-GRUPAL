package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.FacturasSuscripcion;
import DrinkGo.DrinkGo_backend.service.IFacturasSuscripcionService;
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
public class FacturasSuscripcionController {
    @Autowired
    private IFacturasSuscripcionService service;

    @GetMapping("/facturas-suscripcion")
    public List<FacturasSuscripcion> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/facturas-suscripcion")
    public FacturasSuscripcion guardar(@RequestBody FacturasSuscripcion entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/facturas-suscripcion")
    public FacturasSuscripcion modificar(@RequestBody FacturasSuscripcion entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/facturas-suscripcion/{id}")
    public Optional<FacturasSuscripcion> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/facturas-suscripcion/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
