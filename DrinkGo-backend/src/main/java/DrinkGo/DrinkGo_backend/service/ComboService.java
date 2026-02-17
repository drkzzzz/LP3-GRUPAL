package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.Combo;
import DrinkGo.DrinkGo_backend.repository.ComboRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComboService {

    private final ComboRepository comboRepository;

    public ComboService(ComboRepository comboRepository) {
        this.comboRepository = comboRepository;
    }

    @Transactional(readOnly = true)
    public List<Combo> findByNegocio(Long negocioId) {
        return comboRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Combo> findActivos(Long negocioId) {
        return comboRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<Combo> findVigentes(Long negocioId) {
        return comboRepository.findByNegocioIdAndEstaActivoAndValidoHastaAfter(
                negocioId, true, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<Combo> findByProducto(Long productoId) {
        return comboRepository.findByProductoId(productoId);
    }

    @Transactional(readOnly = true)
    public Optional<Combo> findById(Long id) {
        return comboRepository.findById(id);
    }

    @Transactional
    public Combo crear(Combo combo) {
        return comboRepository.save(combo);
    }

    @Transactional
    public Combo actualizar(Combo combo) {
        return comboRepository.save(combo);
    }

    @Transactional
    public void eliminar(Long id) {
        comboRepository.deleteById(id);
    }

    @Transactional
    public Combo incrementarUsos(Long id) {
        Optional<Combo> combo = comboRepository.findById(id);
        if (combo.isPresent()) {
            Combo c = combo.get();
            c.setUsosActuales(c.getUsosActuales() + 1);
            return comboRepository.save(c);
        }
        return null;
    }
}
