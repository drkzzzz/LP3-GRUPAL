package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.DetalleVentas;
import DrinkGo.DrinkGo_backend.repository.DetalleVentasRepository;
import DrinkGo.DrinkGo_backend.service.IDetalleVentasService;

@Service
public class DetalleVentasService implements IDetalleVentasService {
    @Autowired
    private DetalleVentasRepository repoDetalleVentas;

    public List<DetalleVentas> buscarTodos() {
        return repoDetalleVentas.findAll();
    }

    public void guardar(DetalleVentas detalleVentas) {
        repoDetalleVentas.save(detalleVentas);
    }

    public void modificar(DetalleVentas detalleVentas) {
        repoDetalleVentas.save(detalleVentas);
    }

    public Optional<DetalleVentas> buscarId(Long id) {
        return repoDetalleVentas.findById(id);
    }

    public void eliminar(Long id) {
        repoDetalleVentas.deleteById(id);
    }
}
