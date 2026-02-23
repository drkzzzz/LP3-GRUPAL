package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.DetalleDevoluciones;
import DrinkGo.DrinkGo_backend.service.IDetalleDevolucionesService;
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
public class DetalleDevolucionesController {
    @Autowired
    private IDetalleDevolucionesService service;

    @GetMapping("/detalle-devoluciones")
    public List<DetalleDevoluciones> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/detalle-devoluciones")
    public DetalleDevoluciones guardar(@RequestBody DetalleDevoluciones entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/detalle-devoluciones")
    public DetalleDevoluciones modificar(@RequestBody DetalleDevoluciones entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/detalle-devoluciones/{id}")
    public Optional<DetalleDevoluciones> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/detalle-devoluciones/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
