package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.service.IStockInventarioService;
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
public class StockInventarioController {
    @Autowired
    private IStockInventarioService service;

    @GetMapping("/stock-inventario")
    public List<StockInventario> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/stock-inventario")
    public StockInventario guardar(@RequestBody StockInventario entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/stock-inventario")
    public StockInventario modificar(@RequestBody StockInventario entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/stock-inventario/{id}")
    public Optional<StockInventario> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/stock-inventario/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
