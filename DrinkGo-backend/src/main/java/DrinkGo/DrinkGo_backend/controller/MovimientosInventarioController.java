package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.MovimientosInventario;
import DrinkGo.DrinkGo_backend.service.IMovimientosInventarioService;
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
public class MovimientosInventarioController {
    @Autowired
    private IMovimientosInventarioService service;

    @GetMapping("/movimientos-inventario")
    public List<MovimientosInventario> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/movimientos-inventario")
    public MovimientosInventario guardar(@RequestBody MovimientosInventario entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/movimientos-inventario")
    public MovimientosInventario modificar(@RequestBody MovimientosInventario entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/movimientos-inventario/{id}")
    public Optional<MovimientosInventario> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/movimientos-inventario/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
