package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.UnidadesMedida;
import DrinkGo.DrinkGo_backend.service.IUnidadesMedidaService;
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
public class UnidadesMedidaController {
    @Autowired
    private IUnidadesMedidaService service;

    @GetMapping("/unidades-medida")
    public List<UnidadesMedida> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/unidades-medida")
    public UnidadesMedida guardar(@RequestBody UnidadesMedida entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/unidades-medida")
    public UnidadesMedida modificar(@RequestBody UnidadesMedida entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/unidades-medida/{id}")
    public Optional<UnidadesMedida> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/unidades-medida/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
