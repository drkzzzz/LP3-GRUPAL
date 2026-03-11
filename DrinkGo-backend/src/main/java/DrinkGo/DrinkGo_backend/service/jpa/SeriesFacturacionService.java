package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.SeriesFacturacion;
import DrinkGo.DrinkGo_backend.repository.SeriesFacturacionRepository;
import DrinkGo.DrinkGo_backend.service.ISeriesFacturacionService;

@Service
public class SeriesFacturacionService implements ISeriesFacturacionService {
    @Autowired
    private SeriesFacturacionRepository repoSeriesFacturacion;

    public List<SeriesFacturacion> buscarTodos() {
        return repoSeriesFacturacion.findAll();
    }

    public void guardar(SeriesFacturacion seriesFacturacion) {
        // Auto-set predeterminada si no existe otra para ese negocio + tipo
        if (seriesFacturacion.getNegocio() != null && seriesFacturacion.getTipoDocumento() != null) {
            boolean existePredeterminada = repoSeriesFacturacion
                    .existsByNegocioIdAndTipoDocumentoAndEsPredeterminada(
                            seriesFacturacion.getNegocio().getId(),
                            seriesFacturacion.getTipoDocumento(),
                            true);
            if (!existePredeterminada) {
                seriesFacturacion.setEsPredeterminada(true);
            }
        }
        repoSeriesFacturacion.save(seriesFacturacion);
    }

    public void modificar(SeriesFacturacion seriesFacturacion) {
        repoSeriesFacturacion.save(seriesFacturacion);
    }

    public Optional<SeriesFacturacion> buscarId(Long id) {
        return repoSeriesFacturacion.findById(id);
    }

    public void eliminar(Long id) {
        repoSeriesFacturacion.deleteById(id);
    }
}
