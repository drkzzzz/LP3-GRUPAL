package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.SeriesFacturacion;
import DrinkGo.DrinkGo_backend.service.ISeriesFacturacionService;
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
public class SeriesFacturacionController {
    @Autowired
    private ISeriesFacturacionService service;

    @GetMapping("/series-facturacion")
    public List<SeriesFacturacion> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/series-facturacion")
    public SeriesFacturacion guardar(@RequestBody SeriesFacturacion entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/series-facturacion")
    public SeriesFacturacion modificar(@RequestBody SeriesFacturacion entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/series-facturacion/{id}")
    public Optional<SeriesFacturacion> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/series-facturacion/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
