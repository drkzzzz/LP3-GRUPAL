package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.ProductoCreateRequest;
import DrinkGo.DrinkGo_backend.dto.ProductoUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Producto;
import DrinkGo.DrinkGo_backend.entity.Producto.TipoAlmacenamiento;
import DrinkGo.DrinkGo_backend.entity.Producto.TipoProducto;
import DrinkGo.DrinkGo_backend.repository.ProductoRepository;

/**
 * Servicio de Productos - Bloque 4 (Catálogo de Productos).
 * Implementa RF-PRO-001 a RF-PRO-004:
 * - CRUD completo de productos
 * - Filtrado por negocio_id (multi-tenant)
 * - Borrado lógico (eliminado_en vía @SQLDelete)
 * - Validación de SKU único por negocio
 */
@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // ============================================================
    // PRODUCTOS - CRUD
    // ============================================================

    /**
     * Listar todos los productos del negocio autenticado.
     */
    public List<Producto> listarProductos(Long negocioId) {
        return productoRepository.findByNegocioId(negocioId);
    }

    /**
     * Obtener un producto por ID, validando que pertenezca al negocio.
     */
    public Producto obtenerProducto(Long negocioId, Long id) {
        return productoRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado con id " + id + " para el negocio actual"));
    }

    /**
     * Crear un nuevo producto.
     */
    @Transactional
    public Producto crearProducto(Long negocioId, ProductoCreateRequest request) {
        // Validaciones obligatorias
        if (request.getSku() == null || request.getSku().isBlank()) {
            throw new RuntimeException("El SKU es obligatorio");
        }
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        // Validar unicidad de SKU dentro del negocio
        if (productoRepository.existsByNegocioIdAndSku(negocioId, request.getSku())) {
            throw new RuntimeException("Ya existe un producto con SKU " + request.getSku() + " en este negocio");
        }

        // Validar unicidad de código de barras si viene informado
        if (request.getCodigoBarras() != null && !request.getCodigoBarras().isBlank()) {
            if (productoRepository.existsByNegocioIdAndCodigoBarras(negocioId, request.getCodigoBarras())) {
                throw new RuntimeException("Ya existe un producto con código de barras " 
                        + request.getCodigoBarras() + " en este negocio");
            }
        }

        Producto producto = new Producto();
        producto.setNegocioId(negocioId);
        producto.setSku(request.getSku());
        producto.setCodigoBarras(request.getCodigoBarras());
        producto.setNombre(request.getNombre());
        producto.setDescripcionCorta(request.getDescripcionCorta());
        producto.setDescripcion(request.getDescripcion());
        producto.setUrlImagen(request.getUrlImagen());
        producto.setCategoriaId(request.getCategoriaId());
        producto.setMarcaId(request.getMarcaId());
        producto.setUnidadMedidaId(request.getUnidadMedidaId());

        // Tipo de producto
        if (request.getTipoProducto() != null) {
            producto.setTipoProducto(parseTipoProducto(request.getTipoProducto()));
        }

        // Atributos de licorería
        producto.setTipoBebida(request.getTipoBebida());
        producto.setGradoAlcoholico(request.getGradoAlcoholico());
        producto.setVolumenMl(request.getVolumenMl());
        producto.setPaisOrigen(request.getPaisOrigen());
        producto.setAnioCosecha(request.getAnioCosecha());
        producto.setAniejamiento(request.getAniejamiento());

        // Precios
        if (request.getPrecioCompra() != null) {
            producto.setPrecioCompra(request.getPrecioCompra());
        }
        if (request.getPrecioVenta() != null) {
            producto.setPrecioVenta(request.getPrecioVenta());
        }
        producto.setPrecioVentaMinimo(request.getPrecioVentaMinimo());
        producto.setPrecioMayorista(request.getPrecioMayorista());
        if (request.getTasaImpuesto() != null) {
            producto.setTasaImpuesto(request.getTasaImpuesto());
        }
        if (request.getImpuestoIncluido() != null) {
            producto.setImpuestoIncluido(request.getImpuestoIncluido());
        }

        // Stock
        if (request.getStockMinimo() != null) {
            producto.setStockMinimo(request.getStockMinimo());
        }
        producto.setStockMaximo(request.getStockMaximo());
        producto.setPuntoReorden(request.getPuntoReorden());

        // Almacenamiento
        if (request.getTipoAlmacenamiento() != null) {
            producto.setTipoAlmacenamiento(parseTipoAlmacenamiento(request.getTipoAlmacenamiento()));
        }
        producto.setTempOptimaMin(request.getTempOptimaMin());
        producto.setTempOptimaMax(request.getTempOptimaMax());
        if (request.getEsPerecible() != null) {
            producto.setEsPerecible(request.getEsPerecible());
        }
        producto.setDiasVidaUtil(request.getDiasVidaUtil());

        // Dimensiones
        producto.setPesoKg(request.getPesoKg());
        producto.setAltoCm(request.getAltoCm());
        producto.setAnchoCm(request.getAnchoCm());
        producto.setProfundidadCm(request.getProfundidadCm());

        // Flags
        if (request.getVisiblePos() != null) {
            producto.setVisiblePos(request.getVisiblePos());
        }
        if (request.getVisibleTiendaOnline() != null) {
            producto.setVisibleTiendaOnline(request.getVisibleTiendaOnline());
        }
        if (request.getEsDestacado() != null) {
            producto.setEsDestacado(request.getEsDestacado());
        }
        if (request.getRequiereVerificacionEdad() != null) {
            producto.setRequiereVerificacionEdad(request.getRequiereVerificacionEdad());
        }
        if (request.getPermiteDescuento() != null) {
            producto.setPermiteDescuento(request.getPermiteDescuento());
        }

        // SEO
        producto.setMetaTitulo(request.getMetaTitulo());
        producto.setMetaDescripcion(request.getMetaDescripcion());
        producto.setMetaPalabrasClave(request.getMetaPalabrasClave());

        return productoRepository.save(producto);
    }

    /**
     * Actualizar un producto existente.
     * Solo se actualizan los campos que vienen con valor (no null).
     */
    @Transactional
    public Producto actualizarProducto(Long negocioId, Long id, ProductoUpdateRequest request) {
        Producto producto = obtenerProducto(negocioId, id);

        // Actualizar campos opcionales
        if (request.getSku() != null) producto.setSku(request.getSku());
        if (request.getCodigoBarras() != null) producto.setCodigoBarras(request.getCodigoBarras());
        if (request.getNombre() != null) producto.setNombre(request.getNombre());
        if (request.getDescripcionCorta() != null) producto.setDescripcionCorta(request.getDescripcionCorta());
        if (request.getDescripcion() != null) producto.setDescripcion(request.getDescripcion());
        if (request.getUrlImagen() != null) producto.setUrlImagen(request.getUrlImagen());
        if (request.getCategoriaId() != null) producto.setCategoriaId(request.getCategoriaId());
        if (request.getMarcaId() != null) producto.setMarcaId(request.getMarcaId());
        if (request.getUnidadMedidaId() != null) producto.setUnidadMedidaId(request.getUnidadMedidaId());

        if (request.getTipoProducto() != null) {
            producto.setTipoProducto(parseTipoProducto(request.getTipoProducto()));
        }

        if (request.getTipoBebida() != null) producto.setTipoBebida(request.getTipoBebida());
        if (request.getGradoAlcoholico() != null) producto.setGradoAlcoholico(request.getGradoAlcoholico());
        if (request.getVolumenMl() != null) producto.setVolumenMl(request.getVolumenMl());
        if (request.getPaisOrigen() != null) producto.setPaisOrigen(request.getPaisOrigen());
        if (request.getAnioCosecha() != null) producto.setAnioCosecha(request.getAnioCosecha());
        if (request.getAniejamiento() != null) producto.setAniejamiento(request.getAniejamiento());

        if (request.getPrecioCompra() != null) producto.setPrecioCompra(request.getPrecioCompra());
        if (request.getPrecioVenta() != null) producto.setPrecioVenta(request.getPrecioVenta());
        if (request.getPrecioVentaMinimo() != null) producto.setPrecioVentaMinimo(request.getPrecioVentaMinimo());
        if (request.getPrecioMayorista() != null) producto.setPrecioMayorista(request.getPrecioMayorista());
        if (request.getTasaImpuesto() != null) producto.setTasaImpuesto(request.getTasaImpuesto());
        if (request.getImpuestoIncluido() != null) producto.setImpuestoIncluido(request.getImpuestoIncluido());

        if (request.getStockMinimo() != null) producto.setStockMinimo(request.getStockMinimo());
        if (request.getStockMaximo() != null) producto.setStockMaximo(request.getStockMaximo());
        if (request.getPuntoReorden() != null) producto.setPuntoReorden(request.getPuntoReorden());

        if (request.getTipoAlmacenamiento() != null) {
            producto.setTipoAlmacenamiento(parseTipoAlmacenamiento(request.getTipoAlmacenamiento()));
        }
        if (request.getTempOptimaMin() != null) producto.setTempOptimaMin(request.getTempOptimaMin());
        if (request.getTempOptimaMax() != null) producto.setTempOptimaMax(request.getTempOptimaMax());
        if (request.getEsPerecible() != null) producto.setEsPerecible(request.getEsPerecible());
        if (request.getDiasVidaUtil() != null) producto.setDiasVidaUtil(request.getDiasVidaUtil());

        if (request.getPesoKg() != null) producto.setPesoKg(request.getPesoKg());
        if (request.getAltoCm() != null) producto.setAltoCm(request.getAltoCm());
        if (request.getAnchoCm() != null) producto.setAnchoCm(request.getAnchoCm());
        if (request.getProfundidadCm() != null) producto.setProfundidadCm(request.getProfundidadCm());

        if (request.getVisiblePos() != null) producto.setVisiblePos(request.getVisiblePos());
        if (request.getVisibleTiendaOnline() != null) producto.setVisibleTiendaOnline(request.getVisibleTiendaOnline());
        if (request.getEsDestacado() != null) producto.setEsDestacado(request.getEsDestacado());
        if (request.getRequiereVerificacionEdad() != null) producto.setRequiereVerificacionEdad(request.getRequiereVerificacionEdad());
        if (request.getPermiteDescuento() != null) producto.setPermiteDescuento(request.getPermiteDescuento());

        if (request.getMetaTitulo() != null) producto.setMetaTitulo(request.getMetaTitulo());
        if (request.getMetaDescripcion() != null) producto.setMetaDescripcion(request.getMetaDescripcion());
        if (request.getMetaPalabrasClave() != null) producto.setMetaPalabrasClave(request.getMetaPalabrasClave());

        return productoRepository.save(producto);
    }

    /**
     * Eliminar producto (borrado lógico).
     * Establece eliminado_en = NOW() mediante @SQLDelete.
     */
    @Transactional
    public void eliminarProducto(Long negocioId, Long id) {
        Producto producto = obtenerProducto(negocioId, id);
        productoRepository.delete(producto);
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private TipoProducto parseTipoProducto(String tipo) {
        try {
            return TipoProducto.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de producto inválido: " + tipo 
                    + ". Valores permitidos: alcoholica, no_alcoholica, comida, accesorio, combo, otro");
        }
    }

    private TipoAlmacenamiento parseTipoAlmacenamiento(String tipo) {
        try {
            return TipoAlmacenamiento.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de almacenamiento inválido: " + tipo 
                    + ". Valores permitidos: ambiente, frio, congelado");
        }
    }
}
