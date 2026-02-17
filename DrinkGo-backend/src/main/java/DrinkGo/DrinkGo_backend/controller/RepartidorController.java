package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.Repartidor;
import DrinkGo.DrinkGo_backend.service.RepartidorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
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
        return 1L; // Modo desarrollo
    }

    @GetMapping
    public ResponseEntity<List<Repartidor>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(repartidorService.findByNegocio(negocioId));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Repartidor>> listarActivos() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(repartidorService.findActivos(negocioId));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Repartidor>> listarDisponibles() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(repartidorService.findDisponibles(negocioId));
    }

    @GetMapping("/tipo-vehiculo/{tipoVehiculo}")
    public ResponseEntity<List<Repartidor>> listarPorTipoVehiculo(@PathVariable Repartidor.TipoVehiculo tipoVehiculo) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(repartidorService.findByTipoVehiculo(negocioId, tipoVehiculo));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Repartidor>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(repartidorService.findByUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repartidor> obtenerPorId(@PathVariable Long id) {
        Optional<Repartidor> repartidor = repartidorService.findById(id);
        return repartidor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Repartidor> crear(@RequestBody Repartidor repartidor) {
        Long negocioId = obtenerNegocioId();
        repartidor.setNegocioId(negocioId);
        Repartidor nuevoRepartidor = repartidorService.crear(repartidor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoRepartidor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repartidor> actualizar(@PathVariable Long id, @RequestBody Repartidor repartidor) {
        repartidor.setId(id);
        Repartidor repartidorActualizado = repartidorService.actualizar(repartidor);
        return ResponseEntity.ok(repartidorActualizado);
    }

    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<Repartidor> actualizarDisponibilidad(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean disponible = request.get("disponible");
        Repartidor repartidor = repartidorService.actualizarDisponibilidad(id, disponible);
        if (repartidor != null) {
            return ResponseEntity.ok(repartidor);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/ubicacion")
    public ResponseEntity<Repartidor> actualizarUbicacion(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {
        BigDecimal latitud = request.get("latitud");
        BigDecimal longitud = request.get("longitud");
        Repartidor repartidor = repartidorService.actualizarUbicacion(id, latitud, longitud);
        if (repartidor != null) {
            return ResponseEntity.ok(repartidor);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repartidorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
