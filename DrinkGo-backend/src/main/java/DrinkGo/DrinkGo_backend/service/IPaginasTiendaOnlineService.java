package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.PaginasTiendaOnline;

public interface IPaginasTiendaOnlineService {
    List<PaginasTiendaOnline> buscarTodos();

    void guardar(PaginasTiendaOnline paginasTiendaOnline);

    void modificar(PaginasTiendaOnline paginasTiendaOnline);

    Optional<PaginasTiendaOnline> buscarId(Long id);

    void eliminar(Long id);
}
