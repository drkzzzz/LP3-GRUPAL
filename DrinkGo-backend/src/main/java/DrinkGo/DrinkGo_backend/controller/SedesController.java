package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.service.ISedesService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class SedesController {
    @Autowired
    private ISedesService service;

    @GetMapping("/sedes")
    public List<Sedes> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/sedes/por-negocio/{negocioId}")
    public List<Sedes> buscarPorNegocio(@PathVariable("negocioId") Long negocioId) {
        return service.buscarPorNegocio(negocioId);
    }

    @PostMapping("/sedes")
    public ResponseEntity<?> guardar(@RequestBody Sedes entity) {
        try {
            service.guardar(entity);
            return ResponseEntity.ok(entity);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/sedes")
    public ResponseEntity<?> modificar(@RequestBody Sedes entity) {
        try {
            service.modificar(entity);
            return ResponseEntity.ok(entity);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/sedes/{id}")
    public Optional<Sedes> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/sedes/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
