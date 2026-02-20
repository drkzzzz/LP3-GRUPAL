package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.HorarioEspecialSede;
import DrinkGo.DrinkGo_backend.repository.HorarioEspecialSedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HorarioEspecialSedeService {

    private final HorarioEspecialSedeRepository horarioEspecialRepository;

    public HorarioEspecialSedeService(HorarioEspecialSedeRepository horarioEspecialRepository) {
        this.horarioEspecialRepository = horarioEspecialRepository;
    }

    @Transactional(readOnly = true)
    public List<HorarioEspecialSede> findBySede(Long sedeId) {
        return horarioEspecialRepository.findBySedeId(sedeId);
    }

    @Transactional(readOnly = true)
    public HorarioEspecialSede findById(Long id) {
        return horarioEspecialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario especial no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<HorarioEspecialSede> findAll() {
        return horarioEspecialRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<HorarioEspecialSede> findBySedeEnRango(Long sedeId, LocalDate inicio, LocalDate fin) {
        return horarioEspecialRepository.findBySedeIdAndFechaBetween(sedeId, inicio, fin);
    }

    @Transactional
    public HorarioEspecialSede crear(HorarioEspecialSede horario) {
        return horarioEspecialRepository.save(horario);
    }

    @Transactional
    public HorarioEspecialSede actualizar(Long id, HorarioEspecialSede horario) {
        HorarioEspecialSede existente = findById(id);
        existente.setHoraApertura(horario.getHoraApertura());
        existente.setHoraCierre(horario.getHoraCierre());
        existente.setEstaCerrado(horario.getEstaCerrado());
        existente.setMotivo(horario.getMotivo());
        return horarioEspecialRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        horarioEspecialRepository.deleteById(id);
    }
}
