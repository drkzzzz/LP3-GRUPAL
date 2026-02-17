package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.ProductoProveedorRequest;
import DrinkGo.DrinkGo_backend.dto.ProductoProveedorResponse;
import DrinkGo.DrinkGo_backend.entity.ProductoProveedor;
import DrinkGo.DrinkGo_backend.repository.ProductoProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para ProductoProveedor
 */
@Service
public class ProductoProveedorService {

    @Autowired
    private ProductoProveedorRepository productoProveedorRepository;

    @Transactional(readOnly = true)
    public List<ProductoProveedorResponse> obtenerTodosLosProductosProveedor() {
        return productoProveedorRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoProveedorResponse> obtenerProductosPorProveedor(Long proveedorId) {
        return productoProveedorRepository.findByProveedorId(proveedorId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoProveedorResponse> obtenerProveedoresPorProducto(Long productoId) {
        return productoProveedorRepository.findByProductoId(productoId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoProveedorResponse> obtenerProveedoresPreferidosPorProducto(Long productoId) {
        return productoProveedorRepository.findByProductoIdAndEsPreferido(productoId, true).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductoProveedorResponse obtenerProductoProveedorPorId(Long id) {
        ProductoProveedor productoProveedor = productoProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductoProveedor no encontrado con ID: " + id));
        return convertirAResponse(productoProveedor);
    }

    @Transactional
    public ProductoProveedorResponse crearProductoProveedor(ProductoProveedorRequest request) {
        validarRelacionUnica(request.getProveedorId(), request.getProductoId(), null);

        ProductoProveedor productoProveedor = new ProductoProveedor();
        mapearRequestAEntidad(request, productoProveedor);
        ProductoProveedor guardado = productoProveedorRepository.save(productoProveedor);
        return convertirAResponse(guardado);
    }

    @Transactional
    public ProductoProveedorResponse actualizarProductoProveedor(Long id, ProductoProveedorRequest request) {
        ProductoProveedor productoProveedor = productoProveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductoProveedor no encontrado con ID: " + id));

        mapearRequestAEntidad(request, productoProveedor);
        ProductoProveedor actualizado = productoProveedorRepository.save(productoProveedor);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarProductoProveedor(Long id) {
        if (!productoProveedorRepository.existsById(id)) {
            throw new RuntimeException("ProductoProveedor no encontrado con ID: " + id);
        }
        productoProveedorRepository.deleteById(id);
    }

    @Transactional
    public void eliminarPorProveedor(Long proveedorId) {
        productoProveedorRepository.deleteByProveedorId(proveedorId);
    }

    @Transactional
    public void eliminarPorProducto(Long productoId) {
        productoProveedorRepository.deleteByProductoId(productoId);
    }

    // ── Métodos de Validación ──

    private void validarRelacionUnica(Long proveedorId, Long productoId, Long idExcluir) {
        if (productoProveedorRepository.existsByProveedorIdAndProductoId(proveedorId, productoId)) {
            productoProveedorRepository.findByProveedorIdAndProductoId(proveedorId, productoId)
                    .ifPresent(pp -> {
                        if (idExcluir == null || !pp.getId().equals(idExcluir)) {
                            throw new RuntimeException("Ya existe una relación entre el proveedor " + proveedorId + 
                                                      " y el producto " + productoId);
                        }
                    });
        }
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(ProductoProveedorRequest request, ProductoProveedor productoProveedor) {
        productoProveedor.setProveedorId(request.getProveedorId());
        productoProveedor.setProductoId(request.getProductoId());
        productoProveedor.setSkuProveedor(request.getSkuProveedor());
        productoProveedor.setPrecioProveedor(request.getPrecioProveedor());
        productoProveedor.setDiasTiempoEntrega(request.getDiasTiempoEntrega());
        productoProveedor.setCantidadMinimaPedido(request.getCantidadMinimaPedido());
        productoProveedor.setEsPreferido(request.getEsPreferido() != null ? request.getEsPreferido() : false);
        productoProveedor.setFechaUltimaCompra(request.getFechaUltimaCompra());
    }

    private ProductoProveedorResponse convertirAResponse(ProductoProveedor productoProveedor) {
        ProductoProveedorResponse response = new ProductoProveedorResponse();
        response.setId(productoProveedor.getId());
        response.setProveedorId(productoProveedor.getProveedorId());
        response.setProductoId(productoProveedor.getProductoId());
        response.setSkuProveedor(productoProveedor.getSkuProveedor());
        response.setPrecioProveedor(productoProveedor.getPrecioProveedor());
        response.setDiasTiempoEntrega(productoProveedor.getDiasTiempoEntrega());
        response.setCantidadMinimaPedido(productoProveedor.getCantidadMinimaPedido());
        response.setEsPreferido(productoProveedor.getEsPreferido());
        response.setFechaUltimaCompra(productoProveedor.getFechaUltimaCompra());
        response.setCreadoEn(productoProveedor.getCreadoEn());
        response.setActualizadoEn(productoProveedor.getActualizadoEn());
        return response;
    }
}
