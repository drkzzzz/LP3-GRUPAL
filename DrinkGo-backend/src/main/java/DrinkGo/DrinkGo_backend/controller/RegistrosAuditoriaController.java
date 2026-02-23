package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.RegistrosAuditoria;
import DrinkGo.DrinkGo_backend.service.IRegistrosAuditoriaService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class RegistrosAuditoriaController {
    @Autowired
    private IRegistrosAuditoriaService service;

    @GetMapping("/registros-auditoria")
    public List<RegistrosAuditoria> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/registros-auditoria")
    public RegistrosAuditoria guardar(@RequestBody RegistrosAuditoria entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/registros-auditoria")
    public RegistrosAuditoria modificar(@RequestBody RegistrosAuditoria entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/registros-auditoria/{id}")
    public Optional<RegistrosAuditoria> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/registros-auditoria/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
