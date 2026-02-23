package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionTiendaOnline;
import DrinkGo.DrinkGo_backend.service.IConfiguracionTiendaOnlineService;
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
public class ConfiguracionTiendaOnlineController {
    @Autowired
    private IConfiguracionTiendaOnlineService service;

    @GetMapping("/configuracion-tienda-online")
    public List<ConfiguracionTiendaOnline> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/configuracion-tienda-online")
    public ConfiguracionTiendaOnline guardar(@RequestBody ConfiguracionTiendaOnline entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/configuracion-tienda-online")
    public ConfiguracionTiendaOnline modificar(@RequestBody ConfiguracionTiendaOnline entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/configuracion-tienda-online/{id}")
    public Optional<ConfiguracionTiendaOnline> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/configuracion-tienda-online/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
