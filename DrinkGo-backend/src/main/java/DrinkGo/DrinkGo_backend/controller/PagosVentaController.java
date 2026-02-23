package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.PagosVenta;
import DrinkGo.DrinkGo_backend.service.IPagosVentaService;
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
public class PagosVentaController {
    @Autowired
    private IPagosVentaService service;

    @GetMapping("/pagos-venta")
    public List<PagosVenta> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/pagos-venta")
    public PagosVenta guardar(@RequestBody PagosVenta entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/pagos-venta")
    public PagosVenta modificar(@RequestBody PagosVenta entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/pagos-venta/{id}")
    public Optional<PagosVenta> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/pagos-venta/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
