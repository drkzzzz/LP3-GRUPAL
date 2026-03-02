package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;
import DrinkGo.DrinkGo_backend.repository.MetodosPagoRepository;
import DrinkGo.DrinkGo_backend.service.IMetodosPagoService;

@Service
public class MetodosPagoService implements IMetodosPagoService {
    @Autowired
    private MetodosPagoRepository repoMetodosPago;

    public List<MetodosPago> buscarTodos() {
        return repoMetodosPago.findAll();
    }

    @Override
    @Transactional
    public void guardar(MetodosPago metodosPago) {
        repoMetodosPago.save(metodosPago);
        repoMetodosPago.flush();
        renumerarOrdenes(metodosPago.getNegocio().getId(), metodosPago.getId());
    }

    @Override
    @Transactional
    public void modificar(MetodosPago metodosPago) {
        repoMetodosPago.save(metodosPago);
        repoMetodosPago.flush();
        renumerarOrdenes(metodosPago.getNegocio().getId(), metodosPago.getId());
    }

    public Optional<MetodosPago> buscarId(Long id) {
        return repoMetodosPago.findById(id);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Optional<MetodosPago> opt = repoMetodosPago.findById(id);
        Long negocioId = opt.map(m -> m.getNegocio().getId()).orElse(null);
        repoMetodosPago.deleteById(id);
        if (negocioId != null) {
            repoMetodosPago.flush();
            renumerarOrdenes(negocioId, null);
        }
    }

    /**
     * Renumera secuencialmente (0, 1, 2, ...) todos los métodos de pago
     * del negocio. El método con priorityId tiene prioridad en caso de empate
     * de orden (para que el que acabas de editar conserve la posición que elegiste).
     */
    private void renumerarOrdenes(Long negocioId, Long priorityId) {
        if (negocioId == null) return;

        List<MetodosPago> todos = repoMetodosPago.findByNegocioId(negocioId);
        todos.sort(Comparator
                .comparingInt((MetodosPago m) -> m.getOrden() != null ? m.getOrden() : Integer.MAX_VALUE)
                .thenComparingInt((MetodosPago m) -> (priorityId != null && m.getId().equals(priorityId)) ? 0 : 1)
                .thenComparingLong(MetodosPago::getId));

        boolean changed = false;
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getOrden() == null || todos.get(i).getOrden() != i) {
                todos.get(i).setOrden(i);
                changed = true;
            }
        }
        if (changed) {
            repoMetodosPago.saveAll(todos);
            repoMetodosPago.flush();
        }
    }
}
