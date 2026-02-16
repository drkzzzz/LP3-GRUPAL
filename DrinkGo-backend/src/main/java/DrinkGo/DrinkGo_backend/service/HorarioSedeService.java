package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.HorarioSede;
import DrinkGo.DrinkGo_backend.repository.HorarioSedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HorarioSedeService {

    private final HorarioSedeRepository horarioSedeRepository;

    public HorarioSedeService(HorarioSedeRepository horarioSedeRepository) {
        this.horarioSedeRepository = horarioSedeRepository;
    }

    @Transactional(readOnly = true)
    public List<HorarioSede> findBySede(Long sedeId) {
        return horarioSedeRepository.findBySedeId(sedeId);
    }

    @Transactional(readOnly = true)
    public List<HorarioSede> findBySedeActivos(Long sedeId) {
        return horarioSedeRepository.findBySedeIdAndEstaActivo(sedeId, true);
    }

    @Transactional(readOnly = true)
    public HorarioSede findById(Long id) {
        return horarioSedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }

    @Transactional
    public HorarioSede crear(HorarioSede horario) {
        return horarioSedeRepository.save(horario);
    }

    @Transactional
    public HorarioSede actualizar(Long id, HorarioSede horario) {
        HorarioSede existente = findById(id);
        existente.setHoraApertura(horario.getHoraApertura());
        existente.setHoraCierre(horario.getHoraCierre());
        existente.setEstaCerrado(horario.getEstaCerrado());
        existente.setEstaActivo(horario.getEstaActivo());
        return horarioSedeRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        horarioSedeRepository.deleteById(id);
    }
}
