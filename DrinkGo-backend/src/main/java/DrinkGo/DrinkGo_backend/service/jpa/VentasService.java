package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Ventas;
import DrinkGo.DrinkGo_backend.repository.VentasRepository;
import DrinkGo.DrinkGo_backend.service.IVentasService;

@Service
public class VentasService implements IVentasService {
    @Autowired
    private VentasRepository repoVentas;

    public List<Ventas> buscarTodos() {
        return repoVentas.findAll();
    }

    public void guardar(Ventas ventas) {
        repoVentas.save(ventas);
    }

    public void modificar(Ventas ventas) {
        repoVentas.save(ventas);
    }

    public Optional<Ventas> buscarId(Long id) {
        return repoVentas.findById(id);
    }

    public void eliminar(Long id) {
        repoVentas.deleteById(id);
    }
}
