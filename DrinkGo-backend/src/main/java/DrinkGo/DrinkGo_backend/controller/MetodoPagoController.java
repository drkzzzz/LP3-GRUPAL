package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.MetodoPago;
import DrinkGo.DrinkGo_backend.service.MetodoPagoService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Métodos de Pago
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;
    private final UsuarioService usuarioService;

    public MetodoPagoController(MetodoPagoService metodoPagoService, UsuarioService usuarioService) {
        this.metodoPagoService = metodoPagoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(metodoPagoService.findByNegocio(negocioId));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<MetodoPago>> listarActivos() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(metodoPagoService.findByNegocioActivos(negocioId));
    }

    @GetMapping("/pos")
    public ResponseEntity<List<MetodoPago>> listarDisponiblesPos() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(metodoPagoService.findDisponiblesPos(negocioId));
    }

    @GetMapping("/tienda-online")
    public ResponseEntity<List<MetodoPago>> listarDisponiblesTiendaOnline() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(metodoPagoService.findDisponiblesTiendaOnline(negocioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> obtener(@PathVariable Long id) {
        verificarAutenticacion();
        return ResponseEntity.ok(metodoPagoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MetodoPago> crear(@RequestBody MetodoPago metodoPago) {
        Long negocioId = obtenerNegocioId();
        metodoPago.setNegocioId(negocioId);
        return ResponseEntity.ok(metodoPagoService.crear(metodoPago));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Long id, @RequestBody MetodoPago metodoPago) {
        verificarAutenticacion();
        return ResponseEntity.ok(metodoPagoService.actualizar(id, metodoPago));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        verificarAutenticacion();
        metodoPagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    private void verificarAutenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
    }
}
