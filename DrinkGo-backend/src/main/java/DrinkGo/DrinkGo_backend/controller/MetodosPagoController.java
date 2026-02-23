package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;
import DrinkGo.DrinkGo_backend.service.IMetodosPagoService;
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
public class MetodosPagoController {
    @Autowired
    private IMetodosPagoService service;

    @GetMapping("/metodos-pago")
    public List<MetodosPago> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/metodos-pago")
    public MetodosPago guardar(@RequestBody MetodosPago entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/metodos-pago")
    public MetodosPago modificar(@RequestBody MetodosPago entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/metodos-pago/{id}")
    public Optional<MetodosPago> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/metodos-pago/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
