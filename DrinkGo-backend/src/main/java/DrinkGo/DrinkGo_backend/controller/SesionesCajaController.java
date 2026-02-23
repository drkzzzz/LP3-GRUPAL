package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.SesionesCaja;
import DrinkGo.DrinkGo_backend.service.ISesionesCajaService;
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
public class SesionesCajaController {
    @Autowired
    private ISesionesCajaService service;

    @GetMapping("/sesiones-caja")
    public List<SesionesCaja> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/sesiones-caja")
    public SesionesCaja guardar(@RequestBody SesionesCaja entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/sesiones-caja")
    public SesionesCaja modificar(@RequestBody SesionesCaja entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/sesiones-caja/{id}")
    public Optional<SesionesCaja> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/sesiones-caja/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
