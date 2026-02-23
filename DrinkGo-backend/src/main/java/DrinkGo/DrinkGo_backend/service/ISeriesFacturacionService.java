package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.SeriesFacturacion;

public interface ISeriesFacturacionService {
    List<SeriesFacturacion> buscarTodos();

    void guardar(SeriesFacturacion seriesFacturacion);

    void modificar(SeriesFacturacion seriesFacturacion);

    Optional<SeriesFacturacion> buscarId(Long id);

    void eliminar(Long id);
}
