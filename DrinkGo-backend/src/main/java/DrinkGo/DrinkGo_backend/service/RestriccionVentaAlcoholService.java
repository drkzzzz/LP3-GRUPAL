package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.RestriccionVentaAlcohol;
import DrinkGo.DrinkGo_backend.repository.RestriccionVentaAlcoholRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestriccionVentaAlcoholService {

    private final RestriccionVentaAlcoholRepository restriccionRepository;

    public RestriccionVentaAlcoholService(RestriccionVentaAlcoholRepository restriccionRepository) {
        this.restriccionRepository = restriccionRepository;
    }

    @Transactional(readOnly = true)
    public List<RestriccionVentaAlcohol> findByNegocio(Long negocioId) {
        return restriccionRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<RestriccionVentaAlcohol> findByNegocioActivas(Long negocioId) {
        return restriccionRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<RestriccionVentaAlcohol> findBySede(Long sedeId) {
        return restriccionRepository.findBySedeId(sedeId);
    }

    @Transactional(readOnly = true)
    public RestriccionVentaAlcohol findById(Long id) {
        return restriccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restricción no encontrada"));
    }

    @Transactional
    public RestriccionVentaAlcohol crear(RestriccionVentaAlcohol restriccion) {
        return restriccionRepository.save(restriccion);
    }

    @Transactional
    public RestriccionVentaAlcohol actualizar(Long id, RestriccionVentaAlcohol restriccion) {
        RestriccionVentaAlcohol existente = findById(id);
        existente.setTipoRestriccion(restriccion.getTipoRestriccion());
        existente.setEdadMinima(restriccion.getEdadMinima());
        existente.setHoraPermitidaDesde(restriccion.getHoraPermitidaDesde());
        existente.setHoraPermitidaHasta(restriccion.getHoraPermitidaHasta());
        existente.setDiasRestringidos(restriccion.getDiasRestringidos());
        existente.setDescripcion(restriccion.getDescripcion());
        existente.setEstaActivo(restriccion.getEstaActivo());
        return restriccionRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        restriccionRepository.deleteById(id);
    }
}
