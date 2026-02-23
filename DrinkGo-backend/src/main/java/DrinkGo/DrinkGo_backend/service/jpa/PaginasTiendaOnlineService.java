package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.PaginasTiendaOnline;
import DrinkGo.DrinkGo_backend.repository.PaginasTiendaOnlineRepository;
import DrinkGo.DrinkGo_backend.service.IPaginasTiendaOnlineService;

@Service
public class PaginasTiendaOnlineService implements IPaginasTiendaOnlineService {
    @Autowired
    private PaginasTiendaOnlineRepository repoPaginasTiendaOnline;

    public List<PaginasTiendaOnline> buscarTodos() {
        return repoPaginasTiendaOnline.findAll();
    }

    public void guardar(PaginasTiendaOnline paginasTiendaOnline) {
        repoPaginasTiendaOnline.save(paginasTiendaOnline);
    }

    public void modificar(PaginasTiendaOnline paginasTiendaOnline) {
        repoPaginasTiendaOnline.save(paginasTiendaOnline);
    }

    public Optional<PaginasTiendaOnline> buscarId(Long id) {
        return repoPaginasTiendaOnline.findById(id);
    }

    public void eliminar(Long id) {
        repoPaginasTiendaOnline.deleteById(id);
    }
}
