package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;
import DrinkGo.DrinkGo_backend.repository.DetallePedidosRepository;
import DrinkGo.DrinkGo_backend.service.IDetallePedidosService;

@Service
public class DetallePedidosService implements IDetallePedidosService {
    @Autowired
    private DetallePedidosRepository repoDetallePedidos;

    @Transactional(readOnly = true)
    public List<DetallePedidos> buscarTodos() {
        List<DetallePedidos> detalles = repoDetallePedidos.findAll();
        // ✅ CRÍTICO: Forzar carga de producto (LAZY) dentro de transacción
        for (DetallePedidos detalle : detalles) {
            if (detalle.getProducto() != null) {
                detalle.getProducto().getNombre(); // Trigger lazy load
            }
            if (detalle.getCombo() != null) {
                detalle.getCombo().getNombre();
            }
        }
        return detalles;
    }

    public void guardar(DetallePedidos detallePedidos) {
        repoDetallePedidos.save(detallePedidos);
    }

    public void modificar(DetallePedidos detallePedidos) {
        repoDetallePedidos.save(detallePedidos);
    }

    @Transactional(readOnly = true)
    public Optional<DetallePedidos> buscarId(Long id) {
        Optional<DetallePedidos> detalle = repoDetallePedidos.findById(id);
        detalle.ifPresent(d -> {
            if (d.getProducto() != null) {
                d.getProducto().getNombre(); // Trigger lazy load
            }
            if (d.getCombo() != null) {
                d.getCombo().getNombre();
            }
        });
        return detalle;
    }

    public void eliminar(Long id) {
        repoDetallePedidos.deleteById(id);
    }
}
