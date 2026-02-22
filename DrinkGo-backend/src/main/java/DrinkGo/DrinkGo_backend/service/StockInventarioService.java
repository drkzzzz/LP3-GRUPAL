package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.StockInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.StockInventarioResponse;
import DrinkGo.DrinkGo_backend.entity.Almacen;
import DrinkGo.DrinkGo_backend.entity.Producto;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.AlmacenRepository;
import DrinkGo.DrinkGo_backend.repository.ProductoRepository;
import DrinkGo.DrinkGo_backend.repository.StockInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockInventarioService {

    private final StockInventarioRepository stockRepo;
    private final ProductoRepository productoRepo;
    private final AlmacenRepository almacenRepo;

    public StockInventarioService(StockInventarioRepository stockRepo,
                                   ProductoRepository productoRepo,
                                   AlmacenRepository almacenRepo) {
        this.stockRepo = stockRepo;
        this.productoRepo = productoRepo;
        this.almacenRepo = almacenRepo;
    }

    /* ── Listar ───────────────────────────────────────────── */

    @Transactional(readOnly = true)
    public List<StockInventarioResponse> listarTodos(Long negocioId) {
        return stockRepo.findByNegocioId(negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StockInventarioResponse obtenerPorId(Long id, Long negocioId) {
        StockInventario stock = stockRepo.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("StockInventario", id));
        return toResponse(stock);
    }

    @Transactional(readOnly = true)
    public StockInventarioResponse obtenerPorProductoYAlmacen(Long productoId, Long almacenId, Long negocioId) {
        StockInventario stock = stockRepo.findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenId, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe stock para producto " + productoId + " en almacén " + almacenId));
        return toResponse(stock);
    }

    @Transactional(readOnly = true)
    public List<StockInventarioResponse> listarPorAlmacen(Long almacenId, Long negocioId) {
        return stockRepo.findByAlmacenIdAndNegocioId(almacenId, negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockInventarioResponse> listarStockBajo(Long negocioId, int umbral) {
        return stockRepo.findStockBajo(negocioId, umbral)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /* ── Crear ────────────────────────────────────────────── */

    @Transactional
    public StockInventarioResponse crear(StockInventarioRequest req, Long negocioId) {
        // Validar producto y almacén
        productoRepo.findByIdAndNegocioId(req.getProductoId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", req.getProductoId()));
        almacenRepo.findByIdAndNegocioId(req.getAlmacenId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacen", req.getAlmacenId()));

        // Verificar duplicado (unique key producto_id + almacen_id)
        stockRepo.findByProductoIdAndAlmacenIdAndNegocioId(req.getProductoId(), req.getAlmacenId(), negocioId)
                .ifPresent(s -> {
                    throw new OperacionInvalidaException(
                            "Ya existe un registro de stock para producto " + req.getProductoId()
                            + " en almacén " + req.getAlmacenId());
                });

        StockInventario stock = new StockInventario();
        stock.setNegocioId(negocioId);
        stock.setProductoId(req.getProductoId());
        stock.setAlmacenId(req.getAlmacenId());
        stock.setCantidadTotal(req.getCantidadTotal() != null ? req.getCantidadTotal() : 0);

        return toResponse(stockRepo.save(stock));
    }

    /* ── Actualizar ───────────────────────────────────────── */

    @Transactional
    public StockInventarioResponse actualizar(Long id, StockInventarioRequest req, Long negocioId) {
        StockInventario stock = stockRepo.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("StockInventario", id));

        if (req.getCantidadTotal() != null) {
            stock.setCantidadTotal(req.getCantidadTotal());
        }

        return toResponse(stockRepo.save(stock));
    }

    /* ── Eliminar ─────────────────────────────────────────── */

    @Transactional
    public void eliminar(Long id, Long negocioId) {
        StockInventario stock = stockRepo.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("StockInventario", id));
        stockRepo.delete(stock);
    }

    /* ── Utilidad interna (usada por otros services) ──────── */

    /**
     * Obtiene o crea automáticamente el registro de stock para un producto+almacén.
     */
    @Transactional
    public StockInventario obtenerOCrearStock(Long productoId, Long almacenId, Long negocioId) {
        return stockRepo.findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenId, negocioId)
                .orElseGet(() -> {
                    StockInventario nuevo = new StockInventario();
                    nuevo.setNegocioId(negocioId);
                    nuevo.setProductoId(productoId);
                    nuevo.setAlmacenId(almacenId);
                    nuevo.setCantidadTotal(0);
                    return stockRepo.save(nuevo);
                });
    }

    /* ── Mapper ───────────────────────────────────────────── */

    private StockInventarioResponse toResponse(StockInventario s) {
        StockInventarioResponse r = new StockInventarioResponse();
        r.setId(s.getId());
        r.setNegocioId(s.getNegocioId());
        r.setProductoId(s.getProductoId());
        r.setAlmacenId(s.getAlmacenId());
        r.setCantidadTotal(s.getCantidadTotal());
        r.setActualizadoEn(s.getActualizadoEn());

        // Nombres (lazy-load)
        if (s.getProducto() != null) {
            r.setProductoNombre(s.getProducto().getNombre());
        }
        if (s.getAlmacen() != null) {
            r.setAlmacenNombre(s.getAlmacen().getNombre());
        }
        return r;
    }
}
