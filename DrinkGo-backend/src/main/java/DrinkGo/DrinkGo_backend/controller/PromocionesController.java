package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Promociones;
import DrinkGo.DrinkGo_backend.service.IPromocionesService;
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
public class PromocionesController {
    @Autowired
    private IPromocionesService service;

    @GetMapping("/promociones")
    public List<Promociones> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/promociones")
    public Promociones guardar(@RequestBody Promociones entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/promociones")
    public Promociones modificar(@RequestBody Promociones entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/promociones/{id}")
    public Optional<Promociones> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/promociones/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
