package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.ZonasDelivery;
import DrinkGo.DrinkGo_backend.repository.ZonasDeliveryRepository;
import DrinkGo.DrinkGo_backend.service.IZonasDeliveryService;

@Service
public class ZonasDeliveryService implements IZonasDeliveryService {
    @Autowired
    private ZonasDeliveryRepository repoZonasDelivery;

    public List<ZonasDelivery> buscarTodos() {
        return repoZonasDelivery.findAll();
    }

    public void guardar(ZonasDelivery zonasDelivery) {
        repoZonasDelivery.save(zonasDelivery);
    }

    public void modificar(ZonasDelivery zonasDelivery) {
        repoZonasDelivery.save(zonasDelivery);
    }

    public Optional<ZonasDelivery> buscarId(Long id) {
        return repoZonasDelivery.findById(id);
    }

    public void eliminar(Long id) {
        repoZonasDelivery.deleteById(id);
    }
}
