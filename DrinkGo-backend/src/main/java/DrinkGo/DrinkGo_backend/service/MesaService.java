package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.Mesa;
import DrinkGo.DrinkGo_backend.repository.MesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @Transactional(readOnly = true)
    public List<Mesa> findByNegocio(Long negocioId) {
        return mesaRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Mesa> findBySede(Long sedeId) {
        return mesaRepository.findBySedeId(sedeId);
    }

    @Transactional(readOnly = true)
    public List<Mesa> findBySedeActivas(Long sedeId) {
        return mesaRepository.findBySedeIdAndEstaActivo(sedeId, true);
    }

    @Transactional(readOnly = true)
    public List<Mesa> findBySedeYEstado(Long sedeId, String estado) {
        return mesaRepository.findBySedeIdAndEstado(sedeId, estado);
    }

    @Transactional(readOnly = true)
    public List<Mesa> findByArea(Long areaId) {
        return mesaRepository.findByAreaId(areaId);
    }

    @Transactional(readOnly = true)
    public Mesa findById(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
    }

    @Transactional
    public Mesa crear(Mesa mesa) {
        if (mesaRepository.findBySedeIdAndNumeroMesa(mesa.getSedeId(), mesa.getNumeroMesa()).isPresent()) {
            throw new RuntimeException("Ya existe una mesa con el número: " + mesa.getNumeroMesa());
        }
        return mesaRepository.save(mesa);
    }

    @Transactional
    public Mesa actualizar(Long id, Mesa mesa) {
        Mesa existente = findById(id);
        existente.setAreaId(mesa.getAreaId());
        existente.setEtiqueta(mesa.getEtiqueta());
        existente.setCapacidad(mesa.getCapacidad());
        existente.setCodigoQr(mesa.getCodigoQr());
        existente.setEstado(mesa.getEstado());
        existente.setPosicionX(mesa.getPosicionX());
        existente.setPosicionY(mesa.getPosicionY());
        existente.setForma(mesa.getForma());
        existente.setEstaActivo(mesa.getEstaActivo());
        return mesaRepository.save(existente);
    }

    @Transactional
    public Mesa cambiarEstado(Long id, String nuevoEstado) {
        Mesa mesa = findById(id);
        mesa.setEstado(nuevoEstado);
        return mesaRepository.save(mesa);
    }

    @Transactional
    public void eliminar(Long id) {
        mesaRepository.deleteById(id);
    }
}
