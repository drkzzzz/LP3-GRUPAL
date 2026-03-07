package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Clientes;
import DrinkGo.DrinkGo_backend.service.IClientesService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class ClientesController {
    @Autowired
    private IClientesService service;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/clientes")
    public List<Clientes> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/clientes/por-negocio/{negocioId}")
    public List<Clientes> buscarPorNegocio(@PathVariable("negocioId") Long negocioId) {
        return service.buscarPorNegocio(negocioId);
    }

    @PostMapping("/clientes")
    public Clientes guardar(@RequestBody Clientes entity) {
        if (entity.getPasswordHash() != null && !entity.getPasswordHash().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(entity.getPasswordHash()));
        }
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/clientes")
    public Clientes modificar(@RequestBody Clientes entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/clientes/{id}")
    public Optional<Clientes> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/clientes/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
