package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.VersionPlanSuscripcionRequest;
import DrinkGo.DrinkGo_backend.dto.VersionPlanSuscripcionResponse;
import DrinkGo.DrinkGo_backend.service.VersionPlanSuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de Versiones de Planes de Suscripción
 * RF-PLT-003
 */
@RestController
@RequestMapping("/api/versiones-planes-suscripcion")
@CrossOrigin(origins = "*")
public class VersionPlanSuscripcionController {

    @Autowired
    private VersionPlanSuscripcionService versionPlanSuscripcionService;

    @GetMapping
    public ResponseEntity<List<VersionPlanSuscripcionResponse>> obtenerTodasLasVersiones() {
        return ResponseEntity.ok(versionPlanSuscripcionService.obtenerTodasLasVersiones());
    }

    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<VersionPlanSuscripcionResponse>> obtenerVersionesPorPlanId(@PathVariable Long planId) {
        return ResponseEntity.ok(versionPlanSuscripcionService.obtenerVersionesPorPlanId(planId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VersionPlanSuscripcionResponse> obtenerVersionPorId(@PathVariable Long id) {
        return ResponseEntity.ok(versionPlanSuscripcionService.obtenerVersionPorId(id));
    }

    @GetMapping("/plan/{planId}/version/{numeroVersion}")
    public ResponseEntity<VersionPlanSuscripcionResponse> obtenerVersionEspecifica(
            @PathVariable Long planId,
            @PathVariable Integer numeroVersion) {
        return ResponseEntity.ok(versionPlanSuscripcionService.obtenerVersionEspecifica(planId, numeroVersion));
    }

    @PostMapping
    public ResponseEntity<VersionPlanSuscripcionResponse> crearVersionPlan(@RequestBody VersionPlanSuscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(versionPlanSuscripcionService.crearVersionPlan(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VersionPlanSuscripcionResponse> actualizarVersion(
            @PathVariable Long id,
            @RequestBody VersionPlanSuscripcionRequest request) {
        return ResponseEntity.ok(versionPlanSuscripcionService.actualizarVersion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVersion(@PathVariable Long id) {
        versionPlanSuscripcionService.eliminarVersion(id);
        return ResponseEntity.noContent().build();
    }
}
