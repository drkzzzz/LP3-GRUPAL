package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.PlanesSuscripcion;
import DrinkGo.DrinkGo_backend.service.IPlanesSuscripcionService;
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
public class PlanesSuscripcionController {
    @Autowired
    private IPlanesSuscripcionService service;

    @GetMapping("/planes-suscripcion")
    public List<PlanesSuscripcion> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/planes-suscripcion")
    public PlanesSuscripcion guardar(@RequestBody PlanesSuscripcion entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/planes-suscripcion")
    public PlanesSuscripcion modificar(@RequestBody PlanesSuscripcion entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/planes-suscripcion/{id}")
    public Optional<PlanesSuscripcion> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/planes-suscripcion/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
