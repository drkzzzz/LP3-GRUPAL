package DrinkGo.DrinkGo_backend.service;

import java.util.List;
import java.util.Optional;
import DrinkGo.DrinkGo_backend.entity.SesionesCaja;

public interface ISesionesCajaService {
    List<SesionesCaja> buscarTodos();

    void guardar(SesionesCaja sesionesCaja);

    void modificar(SesionesCaja sesionesCaja);

    Optional<SesionesCaja> buscarId(Long id);

    void eliminar(Long id);
}
