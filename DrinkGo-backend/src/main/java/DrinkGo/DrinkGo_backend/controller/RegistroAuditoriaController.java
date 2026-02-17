package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.RegistroAuditoriaRequest;
import DrinkGo.DrinkGo_backend.dto.RegistroAuditoriaResponse;
import DrinkGo.DrinkGo_backend.service.RegistroAuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller para gestión de Registros de Auditoría
 * RF-ADM-019
 */
@RestController
@RequestMapping("/api/registros-auditoria")
@CrossOrigin(origins = "*")
public class RegistroAuditoriaController {

    @Autowired
    private RegistroAuditoriaService registroAuditoriaService;

    @GetMapping
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerTodosLosRegistros() {
        return ResponseEntity.ok(registroAuditoriaService.obtenerTodosLosRegistros());
    }

    @GetMapping("/negocio/{negocioId}")
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerRegistrosPorNegocio(@PathVariable Long negocioId) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistrosPorNegocio(negocioId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerRegistrosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistrosPorUsuario(usuarioId));
    }

    @GetMapping("/accion/{accion}")
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerRegistrosPorAccion(@PathVariable String accion) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistrosPorAccion(accion));
    }

    @GetMapping("/tipo-entidad/{tipoEntidad}")
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerRegistrosPorTipoEntidad(@PathVariable String tipoEntidad) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistrosPorTipoEntidad(tipoEntidad));
    }

    @GetMapping("/entidad/{tipoEntidad}/{entidadId}")
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerRegistrosPorEntidad(
            @PathVariable String tipoEntidad,
            @PathVariable Long entidadId) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistrosPorEntidad(tipoEntidad, entidadId));
    }

    @GetMapping("/rango-fecha")
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerRegistrosPorRangoFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistrosPorRangoFecha(fechaInicio, fechaFin));
    }

    @GetMapping("/negocio/{negocioId}/rango-fecha")
    public ResponseEntity<List<RegistroAuditoriaResponse>> obtenerRegistrosPorNegocioYFecha(
            @PathVariable Long negocioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistrosPorNegocioYFecha(negocioId, fechaInicio, fechaFin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAuditoriaResponse> obtenerRegistroPorId(@PathVariable Long id) {
        return ResponseEntity.ok(registroAuditoriaService.obtenerRegistroPorId(id));
    }

    @PostMapping
    public ResponseEntity<RegistroAuditoriaResponse> crearRegistro(@RequestBody RegistroAuditoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registroAuditoriaService.crearRegistro(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroAuditoriaResponse> actualizarRegistro(
            @PathVariable Long id,
            @RequestBody RegistroAuditoriaRequest request) {
        return ResponseEntity.ok(registroAuditoriaService.actualizarRegistro(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRegistro(@PathVariable Long id) {
        registroAuditoriaService.eliminarRegistro(id);
        return ResponseEntity.noContent().build();
    }
}
