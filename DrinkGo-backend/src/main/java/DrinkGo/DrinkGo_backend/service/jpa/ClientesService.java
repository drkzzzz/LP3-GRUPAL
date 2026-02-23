package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.Clientes;
import DrinkGo.DrinkGo_backend.repository.ClientesRepository;
import DrinkGo.DrinkGo_backend.service.IClientesService;

@Service
public class ClientesService implements IClientesService {
    @Autowired
    private ClientesRepository repoClientes;

    public List<Clientes> buscarTodos() {
        return repoClientes.findAll();
    }

    public void guardar(Clientes clientes) {
        repoClientes.save(clientes);
    }

    public void modificar(Clientes clientes) {
        repoClientes.save(clientes);
    }

    public Optional<Clientes> buscarId(Long id) {
        return repoClientes.findById(id);
    }

    public void eliminar(Long id) {
        repoClientes.deleteById(id);
    }
}
