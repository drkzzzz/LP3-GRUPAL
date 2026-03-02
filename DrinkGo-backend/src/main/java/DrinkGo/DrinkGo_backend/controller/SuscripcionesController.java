package DrinkGo.DrinkGo_backend.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DrinkGo.DrinkGo_backend.entity.PlanesSuscripcion;
import DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;
import DrinkGo.DrinkGo_backend.repository.PlanesSuscripcionRepository;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.repository.SedesRepository;
import DrinkGo.DrinkGo_backend.repository.UsuariosRepository;
import DrinkGo.DrinkGo_backend.service.IRegistrosAuditoriaService;
import DrinkGo.DrinkGo_backend.service.ISuscripcionesService;

@RestController
@RequestMapping("/restful")
public class SuscripcionesController {

    @Autowired
    private ISuscripcionesService service;
    @Autowired
    private IRegistrosAuditoriaService auditoriaService;
    @Autowired
    private PlanesSuscripcionRepository planesRepo;
    @Autowired
    private SedesRepository sedesRepo;
    @Autowired
    private UsuariosRepository usuariosRepo;
    @Autowired
    private ProductosRepository productosRepo;

    @GetMapping("/suscripciones")
    public List<Suscripciones> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/suscripciones")
    public Suscripciones guardar(@RequestBody Suscripciones entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/suscripciones")
    public ResponseEntity<?> modificar(@RequestBody Suscripciones entity) {
        // ── 1. Resolver el plan nuevo ────────────────────────────────────────
        if (entity.getPlan() == null || entity.getPlan().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar un plan."));
        }
        Optional<PlanesSuscripcion> planOpt = planesRepo.findById(entity.getPlan().getId());
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Plan no encontrado."));
        }
        PlanesSuscripcion planNuevo = planOpt.get();

        // ── 2. Resolver negocioId ─────────────────────────────────────────────
        if (entity.getNegocio() == null || entity.getNegocio().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Debe especificar el negocio."));
        }
        Long negocioId = entity.getNegocio().getId();

        // ── 3. Validar límites del nuevo plan ─────────────────────────────────
        List<Map<String, Object>> errores = new ArrayList<>();

        if (planNuevo.getMaxSedes() != null) {
            long totalSedes = sedesRepo.countAllByNegocioId(negocioId);
            if (totalSedes > planNuevo.getMaxSedes()) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("campo", "sedes");
                e.put("actual", totalSedes);
                e.put("limite", planNuevo.getMaxSedes());
                e.put("mensaje", "El plan '" + planNuevo.getNombre() + "' permite máximo "
                        + planNuevo.getMaxSedes() + " sede(s). Este negocio tiene "
                        + totalSedes + " sede(s) activa(s). Reduzca las sedes antes de cambiar de plan.");
                errores.add(e);
            }
        }

        if (planNuevo.getMaxUsuarios() != null) {
            long totalUsuarios = usuariosRepo.countByNegocio_Id(negocioId);
            if (totalUsuarios > planNuevo.getMaxUsuarios()) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("campo", "usuarios");
                e.put("actual", totalUsuarios);
                e.put("limite", planNuevo.getMaxUsuarios());
                e.put("mensaje", "El plan '" + planNuevo.getNombre() + "' permite máximo "
                        + planNuevo.getMaxUsuarios() + " usuario(s). Este negocio tiene "
                        + totalUsuarios + " usuario(s). Reduzca los usuarios antes de cambiar de plan.");
                errores.add(e);
            }
        }

        if (planNuevo.getMaxProductos() != null) {
            long totalProductos = productosRepo.countByNegocioId(negocioId);
            if (totalProductos > planNuevo.getMaxProductos()) {
                Map<String, Object> e = new LinkedHashMap<>();
                e.put("campo", "productos");
                e.put("actual", totalProductos);
                e.put("limite", planNuevo.getMaxProductos());
                e.put("mensaje", "El plan '" + planNuevo.getNombre() + "' permite máximo "
                        + planNuevo.getMaxProductos() + " producto(s). Este negocio tiene "
                        + totalProductos + " producto(s). Reduzca los productos antes de cambiar de plan.");
                errores.add(e);
            }
        }

        if (!errores.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of(
                            "error", "No es posible cambiar al plan '" + planNuevo.getNombre()
                                    + "' porque el negocio supera los límites del nuevo plan.",
                            "detalles", errores));
        }

        // ── 4. Obtener plan actual para el log ────────────────────────────────
        String planAnteriorNombre = "desconocido";
        Optional<Suscripciones> actual = service.buscarId(entity.getId());
        if (actual.isPresent() && actual.get().getPlan() != null) {
            planAnteriorNombre = actual.get().getPlan().getNombre();
        }

        // ── 5. Guardar ────────────────────────────────────────────────────────
        service.modificar(entity);

        // ── 6. Registrar en auditoría ─────────────────────────────────────────
        try {
            auditoriaService.registrar(
                    "CAMBIO_PLAN",
                    "Suscripciones",
                    entity.getId(),
                    "Plan cambiado de '" + planAnteriorNombre + "' a '" + planNuevo.getNombre() + "'",
                    RegistrosAuditoria.Severidad.info,
                    "superadmin",
                    negocioId);
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok(entity);
    }

    @GetMapping("/suscripciones/{id}")
    public Optional<Suscripciones> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/suscripciones/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
