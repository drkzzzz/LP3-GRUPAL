package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.MovimientoInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.MovimientoInventarioResponse;
import DrinkGo.DrinkGo_backend.security.UsuarioAutenticado;
import DrinkGo.DrinkGo_backend.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Movimientos de Inventario.
 * Base path: /restful/movimientos
 * Los movimientos son inmutables (no se pueden editar ni eliminar).
 */
@RestController
@RequestMapping("/restful/movimientos")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    /** GET /restful/movimientos - Listar todos los movimientos del negocio */
    @GetMapping
    public ResponseEntity<List<MovimientoInventarioResponse>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(movimientoService.listar(negocioId));
    }

    /** GET /restful/movimientos/{id} - Obtener movimiento por ID */
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioResponse> obtenerPorId(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(movimientoService.obtenerPorId(id, negocioId));
    }

    /**
     * POST /restful/movimientos - Registrar movimiento (entrada o salida).
     * Para salidas se aplica FIFO automáticamente sobre lotes.
     * No existen PUT ni DELETE: los movimientos son inmutables.
     */
    @PostMapping
    public ResponseEntity<MovimientoInventarioResponse> crear(
            @Valid @RequestBody MovimientoInventarioRequest request) {
        UsuarioAutenticado usuario = obtenerUsuario();
        MovimientoInventarioResponse response = movimientoService.crear(
                request, usuario.getNegocioId(), usuario.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── Métodos auxiliares ──

    private Long obtenerNegocioId() {
        return obtenerUsuario().getNegocioId();
    }

    private UsuarioAutenticado obtenerUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UsuarioAutenticado) auth.getPrincipal();
    }
}
