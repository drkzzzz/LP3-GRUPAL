package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.TransferenciaInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.TransferenciaInventarioResponse;
import DrinkGo.DrinkGo_backend.security.UsuarioAutenticado;
import DrinkGo.DrinkGo_backend.service.TransferenciaInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para Transferencias entre Almacenes.
 * Base path: /restful/transferencias
 * Flujo: borrador → pendiente → en_transito → recibida
 */
@RestController
@RequestMapping("/restful/transferencias")
public class TransferenciaInventarioController {

    private final TransferenciaInventarioService transferenciaService;

    public TransferenciaInventarioController(TransferenciaInventarioService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    /** GET /restful/transferencias - Listar todas las transferencias */
    @GetMapping
    public ResponseEntity<List<TransferenciaInventarioResponse>> listar() {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(transferenciaService.listar(negocioId));
    }

    /** GET /restful/transferencias/{id} - Obtener transferencia por ID */
    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaInventarioResponse> obtenerPorId(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        return ResponseEntity.ok(transferenciaService.obtenerPorId(id, negocioId));
    }

    /** POST /restful/transferencias - Crear transferencia (estado borrador) */
    @PostMapping
    public ResponseEntity<TransferenciaInventarioResponse> crear(
            @Valid @RequestBody TransferenciaInventarioRequest request) {
        UsuarioAutenticado usuario = obtenerUsuario();
        TransferenciaInventarioResponse response = transferenciaService.crear(
                request, usuario.getNegocioId(), usuario.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** PUT /restful/transferencias/{id}/aprobar - Aprobar (borrador → pendiente) */
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<TransferenciaInventarioResponse> aprobar(@PathVariable Long id) {
        UsuarioAutenticado usuario = obtenerUsuario();
        TransferenciaInventarioResponse response = transferenciaService.aprobar(
                id, usuario.getNegocioId(), usuario.getUsuarioId());
        return ResponseEntity.ok(response);
    }

    /** PUT /restful/transferencias/{id}/despachar - Despachar (FIFO en origen, estado → en_transito) */
    @PutMapping("/{id}/despachar")
    public ResponseEntity<TransferenciaInventarioResponse> despachar(@PathVariable Long id) {
        UsuarioAutenticado usuario = obtenerUsuario();
        TransferenciaInventarioResponse response = transferenciaService.despachar(
                id, usuario.getNegocioId(), usuario.getUsuarioId());
        return ResponseEntity.ok(response);
    }

    /** PUT /restful/transferencias/{id}/recibir - Recibir (entrada en destino, estado → recibida) */
    @PutMapping("/{id}/recibir")
    public ResponseEntity<TransferenciaInventarioResponse> recibir(@PathVariable Long id) {
        UsuarioAutenticado usuario = obtenerUsuario();
        TransferenciaInventarioResponse response = transferenciaService.recibir(
                id, usuario.getNegocioId(), usuario.getUsuarioId());
        return ResponseEntity.ok(response);
    }

    /** DELETE /restful/transferencias/{id} - Cancelar transferencia (estado → cancelada) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        Long negocioId = obtenerNegocioId();
        transferenciaService.cancelar(id, negocioId);
        return ResponseEntity.noContent().build();
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
