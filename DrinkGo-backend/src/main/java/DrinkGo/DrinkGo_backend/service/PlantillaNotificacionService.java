package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.PlantillaNotificacion;
import DrinkGo.DrinkGo_backend.repository.PlantillaNotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlantillaNotificacionService {

    private final PlantillaNotificacionRepository plantillaRepository;

    public PlantillaNotificacionService(PlantillaNotificacionRepository plantillaRepository) {
        this.plantillaRepository = plantillaRepository;
    }

    @Transactional(readOnly = true)
    public List<PlantillaNotificacion> findByNegocio(Long negocioId) {
        return plantillaRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<PlantillaNotificacion> findGlobales() {
        return plantillaRepository.findByNegocioIdIsNull();
    }

    @Transactional(readOnly = true)
    public List<PlantillaNotificacion> findByNegocioActivas(Long negocioId) {
        return plantillaRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public PlantillaNotificacion findById(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));
    }

    @Transactional
    public PlantillaNotificacion crear(PlantillaNotificacion plantilla) {
        return plantillaRepository.save(plantilla);
    }

    @Transactional
    public PlantillaNotificacion actualizar(Long id, PlantillaNotificacion plantilla) {
        PlantillaNotificacion existente = findById(id);
        existente.setNombre(plantilla.getNombre());
        existente.setCanal(plantilla.getCanal());
        existente.setAsunto(plantilla.getAsunto());
        existente.setPlantillaCuerpo(plantilla.getPlantillaCuerpo());
        existente.setVariablesJson(plantilla.getVariablesJson());
        existente.setEstaActivo(plantilla.getEstaActivo());
        return plantillaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        plantillaRepository.deleteById(id);
    }
}
