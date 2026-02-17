package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.AsignacionEtiquetaProducto;
import DrinkGo.DrinkGo_backend.repository.AsignacionEtiquetaProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignacionEtiquetaProductoService {

    private final AsignacionEtiquetaProductoRepository asignacionRepository;

    public AsignacionEtiquetaProductoService(AsignacionEtiquetaProductoRepository asignacionRepository) {
        this.asignacionRepository = asignacionRepository;
    }

    @Transactional(readOnly = true)
    public List<AsignacionEtiquetaProducto> findByProducto(Long productoId) {
        return asignacionRepository.findByProductoId(productoId);
    }

    @Transactional(readOnly = true)
    public List<AsignacionEtiquetaProducto> findByEtiqueta(Long etiquetaId) {
        return asignacionRepository.findByEtiquetaId(etiquetaId);
    }

    @Transactional(readOnly = true)
    public List<AsignacionEtiquetaProducto> findByEtiquetas(List<Long> etiquetaIds) {
        return asignacionRepository.findByEtiquetaIdIn(etiquetaIds);
    }

    @Transactional
    public AsignacionEtiquetaProducto asignar(Long productoId, Long etiquetaId) {
        AsignacionEtiquetaProducto asignacion = new AsignacionEtiquetaProducto();
        asignacion.setProductoId(productoId);
        asignacion.setEtiquetaId(etiquetaId);
        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public void desasignar(Long productoId, Long etiquetaId) {
        asignacionRepository.deleteByProductoIdAndEtiquetaId(productoId, etiquetaId);
    }

    @Transactional
    public void eliminarPorProducto(Long productoId) {
        asignacionRepository.deleteByProductoId(productoId);
    }

    @Transactional
    public void eliminarPorEtiqueta(Long etiquetaId) {
        asignacionRepository.deleteByEtiquetaId(etiquetaId);
    }
}
