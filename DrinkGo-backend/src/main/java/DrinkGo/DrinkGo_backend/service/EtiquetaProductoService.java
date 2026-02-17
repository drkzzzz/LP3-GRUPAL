package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.EtiquetaProducto;
import DrinkGo.DrinkGo_backend.repository.EtiquetaProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EtiquetaProductoService {

    private final EtiquetaProductoRepository etiquetaProductoRepository;

    public EtiquetaProductoService(EtiquetaProductoRepository etiquetaProductoRepository) {
        this.etiquetaProductoRepository = etiquetaProductoRepository;
    }

    @Transactional(readOnly = true)
    public List<EtiquetaProducto> findByNegocio(Long negocioId) {
        return etiquetaProductoRepository.findByNegocioIdOrderByNombreAsc(negocioId);
    }

    @Transactional(readOnly = true)
    public Optional<EtiquetaProducto> findById(Long id) {
        return etiquetaProductoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<EtiquetaProducto> findBySlug(Long negocioId, String slug) {
        return etiquetaProductoRepository.findByNegocioIdAndSlug(negocioId, slug);
    }

    @Transactional
    public EtiquetaProducto crear(EtiquetaProducto etiquetaProducto) {
        return etiquetaProductoRepository.save(etiquetaProducto);
    }

    @Transactional
    public EtiquetaProducto actualizar(EtiquetaProducto etiquetaProducto) {
        return etiquetaProductoRepository.save(etiquetaProducto);
    }

    @Transactional
    public void eliminar(Long id) {
        etiquetaProductoRepository.deleteById(id);
    }
}
