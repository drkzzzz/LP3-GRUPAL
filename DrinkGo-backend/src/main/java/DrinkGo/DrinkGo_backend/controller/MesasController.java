package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Mesas;
import DrinkGo.DrinkGo_backend.service.IMesasService;
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
public class MesasController {
    @Autowired
    private IMesasService service;

    @GetMapping("/mesas")
    public List<Mesas> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/mesas/por-negocio/{negocioId}")
    public ResponseEntity<?> buscarPorNegocio(@PathVariable("negocioId") Long negocioId) {
        try {
            return ResponseEntity.ok(service.buscarPorNegocio(negocioId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/mesas")
    public ResponseEntity<?> guardar(@RequestBody Mesas entity) {
        try {
            service.guardar(entity);
            return ResponseEntity.ok(entity);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/mesas")
    public ResponseEntity<?> modificar(@RequestBody Mesas entity) {
        try {
            service.modificar(entity);
            return ResponseEntity.ok(entity);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/mesas/{id}")
    public Optional<Mesas> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/mesas/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
