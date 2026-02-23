package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.PaginasTiendaOnline;
import DrinkGo.DrinkGo_backend.service.IPaginasTiendaOnlineService;
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
public class PaginasTiendaOnlineController {
    @Autowired
    private IPaginasTiendaOnlineService service;

    @GetMapping("/paginas-tienda-online")
    public List<PaginasTiendaOnline> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/paginas-tienda-online")
    public PaginasTiendaOnline guardar(@RequestBody PaginasTiendaOnline entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/paginas-tienda-online")
    public PaginasTiendaOnline modificar(@RequestBody PaginasTiendaOnline entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/paginas-tienda-online/{id}")
    public Optional<PaginasTiendaOnline> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/paginas-tienda-online/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
