package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.Repartidor;
import DrinkGo.DrinkGo_backend.repository.RepartidorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;

    public RepartidorService(RepartidorRepository repartidorRepository) {
        this.repartidorRepository = repartidorRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Repartidor> findById(Long id) {
        return repartidorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Repartidor> findByNegocio(Long negocioId) {
        return repartidorRepository.findByNegocioId(negocioId);
    }

    @Transactional(readOnly = true)
    public List<Repartidor> findActivos(Long negocioId) {
        return repartidorRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<Repartidor> findDisponibles(Long negocioId) {
        return repartidorRepository.findByNegocioIdAndEstaDisponibleAndEstaActivo(negocioId, true, true);
    }

    @Transactional(readOnly = true)
    public List<Repartidor> findByTipoVehiculo(Long negocioId, Repartidor.TipoVehiculo tipoVehiculo) {
        return repartidorRepository.findByNegocioIdAndTipoVehiculo(negocioId, tipoVehiculo);
    }

    @Transactional(readOnly = true)
    public List<Repartidor> findByUsuario(Long usuarioId) {
        return repartidorRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Repartidor crear(Repartidor repartidor) {
        return repartidorRepository.save(repartidor);
    }

    @Transactional
    public Repartidor actualizar(Repartidor repartidor) {
        return repartidorRepository.save(repartidor);
    }

    @Transactional
    public Repartidor actualizarDisponibilidad(Long id, Boolean disponible) {
        Optional<Repartidor> repartidorOpt = repartidorRepository.findById(id);
        if (repartidorOpt.isPresent()) {
            Repartidor repartidor = repartidorOpt.get();
            repartidor.setEstaDisponible(disponible);
            return repartidorRepository.save(repartidor);
        }
        return null;
    }

    @Transactional
    public Repartidor actualizarUbicacion(Long id, BigDecimal latitud, BigDecimal longitud) {
        Optional<Repartidor> repartidorOpt = repartidorRepository.findById(id);
        if (repartidorOpt.isPresent()) {
            Repartidor repartidor = repartidorOpt.get();
            repartidor.setLatitudActual(latitud);
            repartidor.setLongitudActual(longitud);
            repartidor.setUltimaUbicacionEn(LocalDateTime.now());
            return repartidorRepository.save(repartidor);
        }
        return null;
    }

    @Transactional
    public void eliminar(Long id) {
        repartidorRepository.deleteById(id);
    }
}
