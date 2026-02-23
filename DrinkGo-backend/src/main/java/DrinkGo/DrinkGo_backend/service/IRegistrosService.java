package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;

import DrinkGo.DrinkGo_backend.entity.Registros;

public interface IRegistrosService {
    List<Registros> BuscarTodos(); // devuelve datos

    void guardar(Registros registro); // registra datos

    void modificar(Registros registro); // modifica datos

    Optional<Registros> BuscarId(Integer id);// devuelve registros por id

    void eliminar(Integer id); // elimina registros por id

}
