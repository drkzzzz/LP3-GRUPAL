package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.DetalleCombo;
import DrinkGo.DrinkGo_backend.repository.DetalleComboRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleComboService {

    private final DetalleComboRepository detalleComboRepository;

    public DetalleComboService(DetalleComboRepository detalleComboRepository) {
        this.detalleComboRepository = detalleComboRepository;
    }

    @Transactional(readOnly = true)
    public List<DetalleCombo> findByCombo(Long comboId) {
        return detalleComboRepository.findByComboIdOrderByOrdenAsc(comboId);
    }

    @Transactional(readOnly = true)
    public List<DetalleCombo> findByProducto(Long productoId) {
        return detalleComboRepository.findByProductoId(productoId);
    }

    @Transactional(readOnly = true)
    public Optional<DetalleCombo> findById(Long id) {
        return detalleComboRepository.findById(id);
    }

    @Transactional
    public DetalleCombo crear(DetalleCombo detalleCombo) {
        return detalleComboRepository.save(detalleCombo);
    }

    @Transactional
    public DetalleCombo actualizar(DetalleCombo detalleCombo) {
        return detalleComboRepository.save(detalleCombo);
    }

    @Transactional
    public void eliminar(Long id) {
        detalleComboRepository.deleteById(id);
    }

    @Transactional
    public void eliminarPorCombo(Long comboId) {
        detalleComboRepository.deleteByComboId(comboId);
    }
}
