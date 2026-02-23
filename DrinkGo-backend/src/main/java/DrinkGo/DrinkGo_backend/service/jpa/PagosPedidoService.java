package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.PagosPedido;
import DrinkGo.DrinkGo_backend.repository.PagosPedidoRepository;
import DrinkGo.DrinkGo_backend.service.IPagosPedidoService;

@Service
public class PagosPedidoService implements IPagosPedidoService {
    @Autowired
    private PagosPedidoRepository repoPagosPedido;

    public List<PagosPedido> buscarTodos() {
        return repoPagosPedido.findAll();
    }

    public void guardar(PagosPedido pagosPedido) {
        repoPagosPedido.save(pagosPedido);
    }

    public void modificar(PagosPedido pagosPedido) {
        repoPagosPedido.save(pagosPedido);
    }

    public Optional<PagosPedido> buscarId(Long id) {
        return repoPagosPedido.findById(id);
    }

    public void eliminar(Long id) {
        repoPagosPedido.deleteById(id);
    }
}
