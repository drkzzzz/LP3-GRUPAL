package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.CajasRegistradoras;
import DrinkGo.DrinkGo_backend.service.ICajasRegistradorasService;
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
public class CajasRegistradorasController {
    @Autowired
    private ICajasRegistradorasService service;

    @GetMapping("/cajas-registradoras")
    public List<CajasRegistradoras> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/cajas-registradoras")
    public CajasRegistradoras guardar(@RequestBody CajasRegistradoras entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/cajas-registradoras")
    public CajasRegistradoras modificar(@RequestBody CajasRegistradoras entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/cajas-registradoras/{id}")
    public Optional<CajasRegistradoras> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/cajas-registradoras/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
