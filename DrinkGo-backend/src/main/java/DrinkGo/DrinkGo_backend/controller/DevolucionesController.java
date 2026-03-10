package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Devoluciones;
import DrinkGo.DrinkGo_backend.service.IDevolucionesService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class DevolucionesController {
    @Autowired
    private IDevolucionesService service;

    @GetMapping("/devoluciones")
    public List<Devoluciones> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/devoluciones/negocio/{negocioId}")
    public List<Devoluciones> buscarPorNegocio(@PathVariable Long negocioId) {
        return service.buscarPorNegocio(negocioId);
    }

    @GetMapping("/devoluciones/venta/{ventaId}")
    public List<Devoluciones> buscarPorVenta(@PathVariable Long ventaId) {
        return service.buscarPorVenta(ventaId);
    }

    @PostMapping("/devoluciones")
    public Devoluciones guardar(@RequestBody Devoluciones entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/devoluciones")
    public Devoluciones modificar(@RequestBody Devoluciones entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/devoluciones/{id}")
    public Optional<Devoluciones> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/devoluciones/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }

    @PatchMapping("/devoluciones/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Long id, @RequestParam Long usuarioId) {
        try {
            Devoluciones dev = service.aprobar(id, usuarioId);
            return ResponseEntity.ok(Map.of("id", dev.getId(), "estado", dev.getEstado().name(), "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/devoluciones/{id}/rechazar")
    public ResponseEntity<?> rechazar(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String razon = body != null ? body.get("razon") : null;
            Devoluciones dev = service.rechazar(id, usuarioId, razon);
            return ResponseEntity.ok(Map.of("id", dev.getId(), "estado", dev.getEstado().name(), "ok", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
