package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.ProductoSede;
import DrinkGo.DrinkGo_backend.repository.ProductoSedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoSedeService {

    private final ProductoSedeRepository productoSedeRepository;

    public ProductoSedeService(ProductoSedeRepository productoSedeRepository) {
        this.productoSedeRepository = productoSedeRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoSede> findByProducto(Long productoId) {
        return productoSedeRepository.findByProductoId(productoId);
    }

    @Transactional(readOnly = true)
    public List<ProductoSede> findBySede(Long sedeId) {
        return productoSedeRepository.findBySedeId(sedeId);
    }

    @Transactional(readOnly = true)
    public List<ProductoSede> findDisponiblesBySede(Long sedeId) {
        return productoSedeRepository.findBySedeIdAndEstaDisponible(sedeId, true);
    }

    @Transactional(readOnly = true)
    public Optional<ProductoSede> findByProductoAndSede(Long productoId, Long sedeId) {
        return productoSedeRepository.findByProductoIdAndSedeId(productoId, sedeId);
    }

    @Transactional(readOnly = true)
    public Optional<ProductoSede> findById(Long id) {
        return productoSedeRepository.findById(id);
    }

    @Transactional
    public ProductoSede crear(ProductoSede productoSede) {
        return productoSedeRepository.save(productoSede);
    }

    @Transactional
    public ProductoSede actualizar(ProductoSede productoSede) {
        return productoSedeRepository.save(productoSede);
    }

    @Transactional
    public void eliminar(Long id) {
        productoSedeRepository.deleteById(id);
    }

    @Transactional
    public void eliminarPorProducto(Long productoId) {
        productoSedeRepository.deleteByProductoId(productoId);
    }

    @Transactional
    public void eliminarPorSede(Long sedeId) {
        productoSedeRepository.deleteBySedeId(sedeId);
    }
}
