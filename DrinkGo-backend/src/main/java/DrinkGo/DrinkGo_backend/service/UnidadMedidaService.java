package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.UnidadMedida;
import DrinkGo.DrinkGo_backend.repository.UnidadMedidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    public UnidadMedidaService(UnidadMedidaRepository unidadMedidaRepository) {
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    @Transactional(readOnly = true)
    public List<UnidadMedida> findByNegocio(Long negocioId) {
        return unidadMedidaRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<UnidadMedida> findActivas(Long negocioId) {
        return unidadMedidaRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<UnidadMedida> findByTipo(Long negocioId, UnidadMedida.TipoUnidadMedida tipo) {
        return unidadMedidaRepository.findByNegocioIdAndTipo(negocioId, tipo);
    }

    @Transactional(readOnly = true)
    public Optional<UnidadMedida> findById(Long id) {
        return unidadMedidaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UnidadMedida> findByCodigo(Long negocioId, String codigo) {
        return unidadMedidaRepository.findByNegocioIdAndCodigo(negocioId, codigo);
    }

    @Transactional
    public UnidadMedida crear(UnidadMedida unidadMedida) {
        return unidadMedidaRepository.save(unidadMedida);
    }

    @Transactional
    public UnidadMedida actualizar(UnidadMedida unidadMedida) {
        return unidadMedidaRepository.save(unidadMedida);
    }

    @Transactional
    public void eliminar(Long id) {
        unidadMedidaRepository.deleteById(id);
    }
}
