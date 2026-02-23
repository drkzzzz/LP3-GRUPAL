package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.DetalleVentas;
import DrinkGo.DrinkGo_backend.service.IDetalleVentasService;
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
public class DetalleVentasController {
    @Autowired
    private IDetalleVentasService service;

    @GetMapping("/detalle-ventas")
    public List<DetalleVentas> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/detalle-ventas")
    public DetalleVentas guardar(@RequestBody DetalleVentas entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/detalle-ventas")
    public DetalleVentas modificar(@RequestBody DetalleVentas entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/detalle-ventas/{id}")
    public Optional<DetalleVentas> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/detalle-ventas/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
