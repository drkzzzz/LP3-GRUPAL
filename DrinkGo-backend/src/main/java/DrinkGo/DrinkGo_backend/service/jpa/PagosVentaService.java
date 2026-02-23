package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.PagosVenta;
import DrinkGo.DrinkGo_backend.repository.PagosVentaRepository;
import DrinkGo.DrinkGo_backend.service.IPagosVentaService;

@Service
public class PagosVentaService implements IPagosVentaService {
    @Autowired
    private PagosVentaRepository repoPagosVenta;

    public List<PagosVenta> buscarTodos() {
        return repoPagosVenta.findAll();
    }

    public void guardar(PagosVenta pagosVenta) {
        repoPagosVenta.save(pagosVenta);
    }

    public void modificar(PagosVenta pagosVenta) {
        repoPagosVenta.save(pagosVenta);
    }

    public Optional<PagosVenta> buscarId(Long id) {
        return repoPagosVenta.findById(id);
    }

    public void eliminar(Long id) {
        repoPagosVenta.deleteById(id);
    }
}
