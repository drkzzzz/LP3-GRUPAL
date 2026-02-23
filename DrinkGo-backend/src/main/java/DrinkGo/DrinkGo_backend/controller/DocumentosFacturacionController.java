package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;
import DrinkGo.DrinkGo_backend.service.IDocumentosFacturacionService;
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
public class DocumentosFacturacionController {
    @Autowired
    private IDocumentosFacturacionService service;

    @GetMapping("/documentos-facturacion")
    public List<DocumentosFacturacion> buscarTodos() {
        return service.buscarTodos();
    }

    @PostMapping("/documentos-facturacion")
    public DocumentosFacturacion guardar(@RequestBody DocumentosFacturacion entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/documentos-facturacion")
    public DocumentosFacturacion modificar(@RequestBody DocumentosFacturacion entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/documentos-facturacion/{id}")
    public Optional<DocumentosFacturacion> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/documentos-facturacion/{id}")
    public String eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return "Registro eliminado";
    }
}
