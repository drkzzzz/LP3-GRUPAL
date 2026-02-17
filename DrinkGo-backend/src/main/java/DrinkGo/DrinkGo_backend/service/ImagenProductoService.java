package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.ImagenProducto;
import DrinkGo.DrinkGo_backend.repository.ImagenProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ImagenProductoService {

    private final ImagenProductoRepository imagenProductoRepository;

    public ImagenProductoService(ImagenProductoRepository imagenProductoRepository) {
        this.imagenProductoRepository = imagenProductoRepository;
    }

    @Transactional(readOnly = true)
    public List<ImagenProducto> findByProducto(Long productoId) {
        return imagenProductoRepository.findByProductoIdOrderByOrdenAsc(productoId);
    }

    @Transactional(readOnly = true)
    public Optional<ImagenProducto> findImagenPrincipal(Long productoId) {
        return imagenProductoRepository.findByProductoIdAndEsPrincipal(productoId, true);
    }

    @Transactional(readOnly = true)
    public Optional<ImagenProducto> findById(Long id) {
        return imagenProductoRepository.findById(id);
    }

    @Transactional
    public ImagenProducto crear(ImagenProducto imagenProducto) {
        // Si se marca como principal, desmarcar las demás
        if (imagenProducto.getEsPrincipal()) {
            Optional<ImagenProducto> principalActual = imagenProductoRepository
                    .findByProductoIdAndEsPrincipal(imagenProducto.getProductoId(), true);
            principalActual.ifPresent(img -> {
                img.setEsPrincipal(false);
                imagenProductoRepository.save(img);
            });
        }
        return imagenProductoRepository.save(imagenProducto);
    }

    @Transactional
    public ImagenProducto actualizar(ImagenProducto imagenProducto) {
        // Si se marca como principal, desmarcar las demás
        if (imagenProducto.getEsPrincipal()) {
            Optional<ImagenProducto> principalActual = imagenProductoRepository
                    .findByProductoIdAndEsPrincipal(imagenProducto.getProductoId(), true);
            principalActual.ifPresent(img -> {
                if (!img.getId().equals(imagenProducto.getId())) {
                    img.setEsPrincipal(false);
                    imagenProductoRepository.save(img);
                }
            });
        }
        return imagenProductoRepository.save(imagenProducto);
    }

    @Transactional
    public void eliminar(Long id) {
        imagenProductoRepository.deleteById(id);
    }

    @Transactional
    public void eliminarPorProducto(Long productoId) {
        imagenProductoRepository.deleteByProductoId(productoId);
    }
}
