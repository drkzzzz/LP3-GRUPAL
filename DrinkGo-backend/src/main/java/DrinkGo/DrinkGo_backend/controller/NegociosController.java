package DrinkGo.DrinkGo_backend.controller;

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

import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.service.INegociosService;
import DrinkGo.DrinkGo_backend.service.IRegistrosAuditoriaService;
import DrinkGo.DrinkGo_backend.service.ISedesService;

@RestController
@RequestMapping("/restful")
public class NegociosController {

    @Autowired
    private INegociosService service;
    @Autowired
    private IRegistrosAuditoriaService auditoriaService;
    @Autowired
    private ISedesService sedesService;

    @GetMapping("/negocios")
    public List<Negocios> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/negocios")
    public ResponseEntity<?> guardar(@RequestBody Negocios entity) {
        // Validar RUC duplicado
        if (entity.getRuc() != null && !entity.getRuc().isBlank()) {
            if (service.buscarPorRuc(entity.getRuc()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message",
                                "Ya existe un negocio registrado con el RUC " + entity.getRuc() + "."));
            }
        }
        // Validar email duplicado
        if (entity.getEmail() != null && !entity.getEmail().isBlank()) {
            if (service.buscarPorEmail(entity.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message",
                                "Ya existe un negocio registrado con el email " + entity.getEmail() + "."));
            }
        }

        service.guardar(entity);

        // Auto-crear sede principal con los datos del negocio
        String nomSede = entity.getNombreSede();
        if (nomSede == null || nomSede.isBlank()) {
            nomSede = entity.getNombreComercial() != null && !entity.getNombreComercial().isBlank()
                    ? entity.getNombreComercial()
                    : entity.getRazonSocial();
        }
        try {
            Sedes sedePrincipal = new Sedes();
            sedePrincipal.setNombre(nomSede);
            sedePrincipal.setDireccion(
                    entity.getDireccion() != null && !entity.getDireccion().isBlank()
                            ? entity.getDireccion()
                            : "Por definir");
            sedePrincipal.setCiudad(entity.getCiudad());
            sedePrincipal.setDepartamento(entity.getDepartamento());
            sedePrincipal.setTelefono(entity.getTelefono());
            sedePrincipal.setEsPrincipal(true);
            sedePrincipal.setEstaActivo(true);
            sedePrincipal.setDeliveryHabilitado(Boolean.TRUE.equals(entity.getSedeDelivery()));
            sedePrincipal.setRecojoHabilitado(Boolean.TRUE.equals(entity.getSedeRecojo()));
            sedePrincipal.setNegocio(entity);
            sedesService.guardar(sedePrincipal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Negocio creado pero falló la sede: " + e.getMessage()));
        }

        // Registrar creación
        try {
            auditoriaService.registrar(
                    "NEGOCIO_CREADO",
                    "Negocios",
                    entity.getId(),
                    "Negocio '" + entity.getNombreComercial() + "' creado",
                    RegistrosAuditoria.Severidad.info,
                    "superadmin",
                    entity.getId());
        } catch (Exception ignored) {
        }
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/negocios")
    public Negocios modificar(@RequestBody Negocios entity) {
        // Cargar estado actual para detectar cambios
        String estadoAnterior = null;
        String nombreAnterior = null;
        if (entity.getId() != null) {
            Optional<Negocios> anterior = service.buscarId(entity.getId());
            if (anterior.isPresent()) {
                estadoAnterior = anterior.get().getEstado() != null
                        ? anterior.get().getEstado().toString()
                        : null;
                nombreAnterior = anterior.get().getNombreComercial();
            }
        }

        service.modificar(entity);

        // Auditar cambio de estado si cambió
        try {
            String estadoNuevo = entity.getEstado() != null ? entity.getEstado().toString() : null;
            if (estadoAnterior != null && !estadoAnterior.equals(estadoNuevo)) {
                RegistrosAuditoria.Severidad severidad = "suspendido".equalsIgnoreCase(estadoNuevo)
                        || "inactivo".equalsIgnoreCase(estadoNuevo)
                                ? RegistrosAuditoria.Severidad.advertencia
                                : RegistrosAuditoria.Severidad.info;
                auditoriaService.registrar(
                        "CAMBIO_ESTADO_NEGOCIO",
                        "Negocios",
                        entity.getId(),
                        "Estado cambiado de '" + estadoAnterior + "' a '" + estadoNuevo
                                + "' para negocio '" + entity.getNombreComercial() + "'",
                        severidad,
                        "superadmin",
                        entity.getId());
            } else {
                // Cambio genérico
                auditoriaService.registrar(
                        "NEGOCIO_ACTUALIZADO",
                        "Negocios",
                        entity.getId(),
                        "Negocio '" + (entity.getNombreComercial() != null
                                ? entity.getNombreComercial()
                                : nombreAnterior)
                                + "' actualizado",
                        RegistrosAuditoria.Severidad.info,
                        "superadmin",
                        entity.getId());
            }
        } catch (Exception ignored) {
        }

        return entity;
    }

    @GetMapping("/negocios/{id}")
    public Optional<Negocios> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/negocios/{id}")
    public String eliminar(@PathVariable Long id) {
        // Auditar antes de eliminar
        try {
            service.buscarId(id).ifPresent(n -> auditoriaService.registrar(
                    "NEGOCIO_ELIMINADO",
                    "Negocios",
                    id,
                    "Negocio '" + n.getNombreComercial() + "' eliminado",
                    RegistrosAuditoria.Severidad.critico,
                    "superadmin",
                    id));
        } catch (Exception ignored) {
        }
        service.eliminar(id);
        return "Registro eliminado";
    }
}
