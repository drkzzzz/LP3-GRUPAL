package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.DetalleOrdenesCompra;
import DrinkGo.DrinkGo_backend.service.IDetalleOrdenesCompraService;
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
public class DetalleOrdenesCompraController {
    @Autowired
    private IDetalleOrdenesCompraService service;

    @GetMapping("/detalle-ordenes-compra")
    public List<DetalleOrdenesCompra> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/detalle-ordenes-compra")
    public DetalleOrdenesCompra guardar(@RequestBody DetalleOrdenesCompra entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/detalle-ordenes-compra")
    public DetalleOrdenesCompra modificar(@RequestBody DetalleOrdenesCompra entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/detalle-ordenes-compra/{id}")
    public Optional<DetalleOrdenesCompra> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/detalle-ordenes-compra/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
