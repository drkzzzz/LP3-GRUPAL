package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.LotesInventario;
import DrinkGo.DrinkGo_backend.service.ILotesInventarioService;
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
public class LotesInventarioController {
    @Autowired
    private ILotesInventarioService service;

    @GetMapping("/lotes-inventario")
    public List<LotesInventario> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/lotes-inventario")
    public LotesInventario guardar(@RequestBody LotesInventario entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/lotes-inventario")
    public LotesInventario modificar(@RequestBody LotesInventario entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/lotes-inventario/{id}")
    public Optional<LotesInventario> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/lotes-inventario/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
