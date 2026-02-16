package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.StockInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.StockInventarioResponse;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de stock de inventario (RF-INV-001).
 * Todas las operaciones filtran por negocio_id del token (multi-tenant).
 * No se permite stock negativo.
 */
@Service
public class StockInventarioService {

    private final StockInventarioRepository stockRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final LoteInventarioRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    public StockInventarioService(StockInventarioRepository stockRepository,
                                   ProductoRepository productoRepository,
                                   AlmacenRepository almacenRepository,
                                   LoteInventarioRepository loteRepository,
                                   MovimientoInventarioRepository movimientoRepository) {
        this.stockRepository = stockRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /** Listar todo el stock del negocio */
    public List<StockInventarioResponse> listar(Long negocioId) {
        return stockRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    /** Obtener stock por ID */
    public StockInventarioResponse obtenerPorId(Long id, Long negocioId) {
        StockInventario stock = stockRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Stock de inventario", id));
        return convertirAResponse(stock);
    }

    /** Crear registro de stock */
    @Transactional
    public StockInventarioResponse crear(StockInventarioRequest request, Long negocioId) {
        // Validar producto pertenece al negocio
        Producto producto = productoRepository.findByIdAndNegocioId(request.getProductoId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", request.getProductoId()));

        // Validar almacén pertenece al negocio
        Almacen almacen = almacenRepository.findByIdAndNegocioId(request.getAlmacenId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén", request.getAlmacenId()));

        // Verificar que no exista duplicado producto-almacén
        if (stockRepository.findByProductoIdAndAlmacenIdAndNegocioId(
                request.getProductoId(), request.getAlmacenId(), negocioId).isPresent()) {
            throw new OperacionInvalidaException(
                    "Ya existe un registro de stock para este producto en este almacén");
        }

        // No permitir stock negativo
        if (request.getCantidadEnMano() < 0) {
            throw new OperacionInvalidaException("La cantidad en mano no puede ser negativa");
        }

        StockInventario stock = new StockInventario();
        stock.setNegocioId(negocioId);
        stock.setProducto(producto);
        stock.setAlmacen(almacen);
        stock.setCantidadEnMano(request.getCantidadEnMano());
        stock.setCantidadReservada(request.getCantidadReservada() != null ? request.getCantidadReservada() : 0);
        stock.setUltimoMovimientoEn(LocalDateTime.now());
        stock.setCreadoEn(LocalDateTime.now());

        StockInventario guardado = stockRepository.save(stock);

        // BUG-1 FIX: Auto-crear lote de respaldo para mantener consistencia stock/lotes (FIFO)
        if (request.getCantidadEnMano() > 0) {
            LoteInventario lote = new LoteInventario();
            lote.setNegocioId(negocioId);
            lote.setProducto(producto);
            lote.setAlmacen(almacen);
            lote.setNumeroLote("STK-INIT-" + System.currentTimeMillis());
            lote.setCantidadInicial(request.getCantidadEnMano());
            lote.setCantidadRestante(request.getCantidadEnMano());
            lote.setPrecioCompra(BigDecimal.ZERO);
            lote.setFechaRecepcion(LocalDate.now());
            lote.setEstado(LoteInventario.EstadoLote.disponible);
            lote.setNotas("Lote generado automáticamente al crear registro de stock inicial");
            lote.setCreadoEn(LocalDateTime.now());
            LoteInventario loteGuardado = loteRepository.save(lote);

            // Registrar movimiento de stock_inicial
            MovimientoInventario mov = new MovimientoInventario();
            mov.setNegocioId(negocioId);
            mov.setProducto(producto);
            mov.setAlmacen(almacen);
            mov.setLote(loteGuardado);
            mov.setTipoMovimiento(MovimientoInventario.TipoMovimiento.stock_inicial);
            mov.setCantidad(request.getCantidadEnMano());
            mov.setCostoUnitario(BigDecimal.ZERO);
            mov.setTipoReferencia("stock_inventario");
            mov.setReferenciaId(guardado.getId());
            mov.setMotivo("Stock inicial creado vía POST /restful/stock");
            mov.setCreadoEn(LocalDateTime.now());
            movimientoRepository.save(mov);
        }

        return convertirAResponse(guardado);
    }

    /** Actualizar registro de stock */
    @Transactional
    public StockInventarioResponse actualizar(Long id, StockInventarioRequest request, Long negocioId) {
        StockInventario stock = stockRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Stock de inventario", id));

        // No permitir stock negativo
        if (request.getCantidadEnMano() < 0) {
            throw new OperacionInvalidaException("La cantidad en mano no puede ser negativa");
        }

        stock.setCantidadEnMano(request.getCantidadEnMano());
        if (request.getCantidadReservada() != null) {
            stock.setCantidadReservada(request.getCantidadReservada());
        }
        stock.setUltimoMovimientoEn(LocalDateTime.now());

        StockInventario guardado = stockRepository.save(stock);
        return convertirAResponse(guardado);
    }

    // ── Método auxiliar de conversión ──

    private StockInventarioResponse convertirAResponse(StockInventario stock) {
        StockInventarioResponse resp = new StockInventarioResponse();
        resp.setId(stock.getId());
        resp.setNegocioId(stock.getNegocioId());
        resp.setProductoId(stock.getProductoId());
        resp.setProductoNombre(stock.getProducto() != null ? stock.getProducto().getNombre() : null);
        resp.setAlmacenId(stock.getAlmacenId());
        resp.setAlmacenNombre(stock.getAlmacen() != null ? stock.getAlmacen().getNombre() : null);
        resp.setCantidadEnMano(stock.getCantidadEnMano());
        resp.setCantidadReservada(stock.getCantidadReservada());
        resp.setCantidadDisponible(stock.getCantidadDisponible());
        resp.setUltimoConteoEn(stock.getUltimoConteoEn());
        resp.setUltimoMovimientoEn(stock.getUltimoMovimientoEn());
        resp.setCreadoEn(stock.getCreadoEn());
        resp.setActualizadoEn(stock.getActualizadoEn());
        return resp;
    }
}
