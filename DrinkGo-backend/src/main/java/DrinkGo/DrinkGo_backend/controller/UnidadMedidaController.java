package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.UnidadMedida;
import DrinkGo.DrinkGo_backend.service.UnidadMedidaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/unidades-medida")
public class UnidadMedidaController {

    private final UnidadMedidaService unidadMedidaService;

    public UnidadMedidaController(UnidadMedidaService unidadMedidaService) {
        this.unidadMedidaService = unidadMedidaService;
    }

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) auth.getDetails();
            Object negocioId = details.get("negocioId");
            if (negocioId != null) {
                return Long.valueOf(negocioId.toString());
            }
        }
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<UnidadMedida>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(unidadMedidaService.findByNegocio(negocioId));
    }

    @GetMapping("/activas")
    public ResponseEntity<List<UnidadMedida>> listarActivas() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(unidadMedidaService.findActivas(negocioId));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<UnidadMedida>> listarPorTipo(@PathVariable UnidadMedida.TipoUnidadMedida tipo) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(unidadMedidaService.findByTipo(negocioId, tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedida> obtenerPorId(@PathVariable Long id) {
        Optional<UnidadMedida> unidadMedida = unidadMedidaService.findById(id);
        return unidadMedida.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<UnidadMedida> obtenerPorCodigo(@PathVariable String codigo) {
        Long negocioId = obtenerNegocioId();
        Optional<UnidadMedida> unidadMedida = unidadMedidaService.findByCodigo(negocioId, codigo);
        return unidadMedida.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UnidadMedida> crear(@RequestBody UnidadMedida unidadMedida) {
        Long negocioId = obtenerNegocioId();
        unidadMedida.setNegocioId(negocioId);
        UnidadMedida nuevaUnidad = unidadMedidaService.crear(unidadMedida);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaUnidad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedida> actualizar(@PathVariable Long id, @RequestBody UnidadMedida unidadMedida) {
        unidadMedida.setId(id);
        UnidadMedida unidadActualizada = unidadMedidaService.actualizar(unidadMedida);
        return ResponseEntity.ok(unidadActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        unidadMedidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
