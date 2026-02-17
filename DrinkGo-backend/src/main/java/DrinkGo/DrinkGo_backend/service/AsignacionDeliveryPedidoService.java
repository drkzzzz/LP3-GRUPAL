package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.AsignacionDeliveryPedido;
import DrinkGo.DrinkGo_backend.repository.AsignacionDeliveryPedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsignacionDeliveryPedidoService {

    private final AsignacionDeliveryPedidoRepository asignacionRepository;

    public AsignacionDeliveryPedidoService(AsignacionDeliveryPedidoRepository asignacionRepository) {
        this.asignacionRepository = asignacionRepository;
    }

    @Transactional(readOnly = true)
    public Optional<AsignacionDeliveryPedido> findById(Long id) {
        return asignacionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<AsignacionDeliveryPedido> findByPedido(Long pedidoId) {
        return asignacionRepository.findByPedidoId(pedidoId);
    }

    @Transactional(readOnly = true)
    public List<AsignacionDeliveryPedido> findByRepartidor(Long repartidorId) {
        return asignacionRepository.findByRepartidorId(repartidorId);
    }

    @Transactional(readOnly = true)
    public List<AsignacionDeliveryPedido> findByRepartidorYEstado(
            Long repartidorId, AsignacionDeliveryPedido.EstadoDelivery estado) {
        return asignacionRepository.findByRepartidorIdAndEstado(repartidorId, estado);
    }

    @Transactional(readOnly = true)
    public List<AsignacionDeliveryPedido> findByEstado(AsignacionDeliveryPedido.EstadoDelivery estado) {
        return asignacionRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public Long contarPorRepartidorYEstado(Long repartidorId, AsignacionDeliveryPedido.EstadoDelivery estado) {
        return asignacionRepository.countByRepartidorIdAndEstado(repartidorId, estado);
    }

    @Transactional
    public AsignacionDeliveryPedido crear(AsignacionDeliveryPedido asignacion) {
        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public AsignacionDeliveryPedido actualizar(AsignacionDeliveryPedido asignacion) {
        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public AsignacionDeliveryPedido aceptarPedido(Long id) {
        Optional<AsignacionDeliveryPedido> asignacionOpt = asignacionRepository.findById(id);
        if (asignacionOpt.isPresent()) {
            AsignacionDeliveryPedido asignacion = asignacionOpt.get();
            asignacion.setEstado(AsignacionDeliveryPedido.EstadoDelivery.aceptado);
            asignacion.setAceptadoEn(LocalDateTime.now());
            return asignacionRepository.save(asignacion);
        }
        return null;
    }

    @Transactional
    public AsignacionDeliveryPedido marcarRecogido(Long id) {
        Optional<AsignacionDeliveryPedido> asignacionOpt = asignacionRepository.findById(id);
        if (asignacionOpt.isPresent()) {
            AsignacionDeliveryPedido asignacion = asignacionOpt.get();
            asignacion.setEstado(AsignacionDeliveryPedido.EstadoDelivery.recogido);
            asignacion.setRecogidoEn(LocalDateTime.now());
            return asignacionRepository.save(asignacion);
        }
        return null;
    }

    @Transactional
    public AsignacionDeliveryPedido marcarEnTransito(Long id) {
        Optional<AsignacionDeliveryPedido> asignacionOpt = asignacionRepository.findById(id);
        if (asignacionOpt.isPresent()) {
            AsignacionDeliveryPedido asignacion = asignacionOpt.get();
            asignacion.setEstado(AsignacionDeliveryPedido.EstadoDelivery.en_transito);
            return asignacionRepository.save(asignacion);
        }
        return null;
    }

    @Transactional
    public AsignacionDeliveryPedido marcarEntregado(Long id) {
        Optional<AsignacionDeliveryPedido> asignacionOpt = asignacionRepository.findById(id);
        if (asignacionOpt.isPresent()) {
            AsignacionDeliveryPedido asignacion = asignacionOpt.get();
            asignacion.setEstado(AsignacionDeliveryPedido.EstadoDelivery.entregado);
            asignacion.setEntregadoEn(LocalDateTime.now());
            return asignacionRepository.save(asignacion);
        }
        return null;
    }

    @Transactional
    public AsignacionDeliveryPedido marcarFallido(Long id, String notas) {
        Optional<AsignacionDeliveryPedido> asignacionOpt = asignacionRepository.findById(id);
        if (asignacionOpt.isPresent()) {
            AsignacionDeliveryPedido asignacion = asignacionOpt.get();
            asignacion.setEstado(AsignacionDeliveryPedido.EstadoDelivery.fallido);
            asignacion.setNotas(notas);
            return asignacionRepository.save(asignacion);
        }
        return null;
    }

    @Transactional
    public void eliminar(Long id) {
        asignacionRepository.deleteById(id);
    }
}
