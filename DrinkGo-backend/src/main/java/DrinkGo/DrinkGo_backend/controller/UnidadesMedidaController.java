package DrinkGo.DrinkGo_backend.controller;

import java.util.List;
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

import DrinkGo.DrinkGo_backend.entity.UnidadesMedida;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.repository.UnidadesMedidaRepository;
import DrinkGo.DrinkGo_backend.service.IUnidadesMedidaService;

@RestController
@RequestMapping("/restful")
public class UnidadesMedidaController {
    @Autowired
    private IUnidadesMedidaService service;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private UnidadesMedidaRepository unidadesMedidaRepository;

    @GetMapping("/unidades-medida")
    public List<UnidadesMedida> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/unidades-medida/negocio/{negocioId}")
    public List<UnidadesMedida> buscarPorNegocio(@PathVariable Long negocioId) {
        return unidadesMedidaRepository.findByNegocioId(negocioId);
    }

    @PostMapping("/unidades-medida")
    public UnidadesMedida guardar(@RequestBody UnidadesMedida entity) {
        service.guardar(entity);
        return entity;
    }

    @PutMapping("/unidades-medida")
    public UnidadesMedida modificar(@RequestBody UnidadesMedida entity) {
        service.modificar(entity);
        return entity;
    }

    @GetMapping("/unidades-medida/{id}")
    public Optional<UnidadesMedida> buscarId(@PathVariable("id") Long id) {
        return service.buscarId(id);
    }

    @DeleteMapping("/unidades-medida/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        long count = productosRepository.countByUnidadMedidaId(id);
        if (count > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar la unidad de medida porque tiene " + count + " producto(s) asociado(s)");
        }
        service.eliminar(id);
        return ResponseEntity.ok("Registro eliminado");
    }
}
