package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.MetodoPago;
import DrinkGo.DrinkGo_backend.repository.MetodoPagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    public MetodoPagoService(MetodoPagoRepository metodoPagoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> findByNegocio(Long negocioId) {
        return metodoPagoRepository.findByNegocioIdOrderByOrdenAsc(negocioId);
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> findByNegocioActivos(Long negocioId) {
        return metodoPagoRepository.findByNegocioIdAndEstaActivo(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> findDisponiblesPos(Long negocioId) {
        return metodoPagoRepository.findByNegocioIdAndDisponiblePos(negocioId, true);
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> findDisponiblesTiendaOnline(Long negocioId) {
        return metodoPagoRepository.findByNegocioIdAndDisponibleTiendaOnline(negocioId, true);
    }

    @Transactional(readOnly = true)
    public MetodoPago findById(Long id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));
    }

    @Transactional
    public MetodoPago crear(MetodoPago metodoPago) {
        if (metodoPagoRepository.findByNegocioIdAndCodigo(metodoPago.getNegocioId(), metodoPago.getCodigo())
                .isPresent()) {
            throw new RuntimeException("Ya existe un método de pago con el código: " + metodoPago.getCodigo());
        }
        return metodoPagoRepository.save(metodoPago);
    }

    @Transactional
    public MetodoPago actualizar(Long id, MetodoPago metodoPago) {
        MetodoPago existente = findById(id);
        existente.setNombre(metodoPago.getNombre());
        existente.setTipo(metodoPago.getTipo());
        existente.setProveedor(metodoPago.getProveedor());
        existente.setConfiguracionJson(metodoPago.getConfiguracionJson());
        existente.setRequiereReferencia(metodoPago.getRequiereReferencia());
        existente.setRequiereAprobacion(metodoPago.getRequiereAprobacion());
        existente.setEstaActivo(metodoPago.getEstaActivo());
        existente.setDisponiblePos(metodoPago.getDisponiblePos());
        existente.setDisponibleTiendaOnline(metodoPago.getDisponibleTiendaOnline());
        existente.setOrden(metodoPago.getOrden());
        return metodoPagoRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        metodoPagoRepository.deleteById(id);
    }
}
