package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.AreaMesa;
import DrinkGo.DrinkGo_backend.repository.AreaMesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaMesaService {

    private final AreaMesaRepository areaMesaRepository;

    public AreaMesaService(AreaMesaRepository areaMesaRepository) {
        this.areaMesaRepository = areaMesaRepository;
    }

    @Transactional(readOnly = true)
    public List<AreaMesa> findByNegocio(Long negocioId) {
        return areaMesaRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<AreaMesa> findBySede(Long sedeId) {
        return areaMesaRepository.findBySedeIdOrderByOrdenAsc(sedeId);
    }

    @Transactional(readOnly = true)
    public List<AreaMesa> findBySedeActivas(Long sedeId) {
        return areaMesaRepository.findBySedeIdAndEstaActivo(sedeId, true);
    }

    @Transactional(readOnly = true)
    public AreaMesa findById(Long id) {
        return areaMesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área de mesa no encontrada"));
    }

    @Transactional
    public AreaMesa crear(AreaMesa areaMesa) {
        return areaMesaRepository.save(areaMesa);
    }

    @Transactional
    public AreaMesa actualizar(Long id, AreaMesa areaMesa) {
        AreaMesa existente = findById(id);
        existente.setNombre(areaMesa.getNombre());
        existente.setDescripcion(areaMesa.getDescripcion());
        existente.setOrden(areaMesa.getOrden());
        existente.setEstaActivo(areaMesa.getEstaActivo());
        return areaMesaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        areaMesaRepository.deleteById(id);
    }
}
