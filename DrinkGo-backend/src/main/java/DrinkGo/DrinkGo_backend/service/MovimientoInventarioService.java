package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.MovimientoInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.MovimientoInventarioResponse;
import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;
import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import DrinkGo.DrinkGo_backend.entity.MovimientoInventario.TipoMovimiento;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.exception.StockInsuficienteException;
import DrinkGo.DrinkGo_backend.repository.AlmacenRepository;
import DrinkGo.DrinkGo_backend.repository.LoteInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.MovimientoInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MovimientoInventarioService {

    private static final Set<TipoMovimiento> TIPOS_ENTRADA = Set.of(
            TipoMovimiento.entrada_compra,
            TipoMovimiento.ajuste_entrada,
            TipoMovimiento.stock_inicial
    );

    private static final Set<TipoMovimiento> TIPOS_SALIDA = Set.of(
            TipoMovimiento.salida_venta,
            TipoMovimiento.ajuste_salida
    );

    private final MovimientoInventarioRepository movRepo;
    private final ProductoRepository productoRepo;
    private final AlmacenRepository almacenRepo;
    private final LoteInventarioRepository loteRepo;
    private final StockInventarioService stockService;

    public MovimientoInventarioService(MovimientoInventarioRepository movRepo,
                                        ProductoRepository productoRepo,
                                        AlmacenRepository almacenRepo,
                                        LoteInventarioRepository loteRepo,
                                        StockInventarioService stockService) {
        this.movRepo = movRepo;
        this.productoRepo = productoRepo;
        this.almacenRepo = almacenRepo;
        this.loteRepo = loteRepo;
        this.stockService = stockService;
    }

    /* ── Listar ───────────────────────────────────────────── */

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarTodos(Long negocioId) {
        return movRepo.findByNegocioIdOrderByCreadoEnDesc(negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovimientoInventarioResponse obtenerPorId(Long id, Long negocioId) {
        MovimientoInventario mov = movRepo.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("MovimientoInventario", id));
        return toResponse(mov);
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarPorProductoYAlmacen(Long productoId, Long almacenId, Long negocioId) {
        return movRepo.findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenId, negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarPorTipo(String tipo, Long negocioId) {
        TipoMovimiento tm = parseTipo(tipo);
        return movRepo.findByTipoMovimientoAndNegocioId(tm, negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventarioResponse> listarPorRangoFechas(Long negocioId, LocalDateTime desde, LocalDateTime hasta) {
        return movRepo.findByNegocioIdAndCreadoEnBetween(negocioId, desde, hasta)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /* ── Registrar movimiento ─────────────────────────────── */

    @Transactional
    public MovimientoInventarioResponse registrar(MovimientoInventarioRequest req, Long negocioId, Long realizadoPor) {
        // Validar producto y almacén
        productoRepo.findByIdAndNegocioId(req.getProductoId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", req.getProductoId()));
        almacenRepo.findByIdAndNegocioId(req.getAlmacenId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacen", req.getAlmacenId()));

        TipoMovimiento tipo = parseTipo(req.getTipoMovimiento());
        int cantidad = req.getCantidad();

        // Obtener o crear stock
        StockInventario stock = stockService.obtenerOCrearStock(
                req.getProductoId(), req.getAlmacenId(), negocioId);

        // ── Entradas ──
        if (TIPOS_ENTRADA.contains(tipo)) {
            stock.setCantidadTotal(stock.getCantidadTotal() + cantidad);

            // Si se indica un lote, sumar al lote
            if (req.getLoteId() != null) {
                LoteInventario lote = loteRepo.findByIdAndNegocioId(req.getLoteId(), negocioId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("LoteInventario", req.getLoteId()));
                lote.setCantidadRestante(lote.getCantidadRestante() + cantidad);
                if (lote.getEstado() == LoteEstado.agotado) {
                    lote.setEstado(LoteEstado.disponible);
                }
                loteRepo.save(lote);
            }
        }
        // ── Salidas ──
        else if (TIPOS_SALIDA.contains(tipo)) {
            if (stock.getCantidadTotal() < cantidad) {
                throw new StockInsuficienteException(
                        "Stock insuficiente: disponible=" + stock.getCantidadTotal()
                        + ", solicitado=" + cantidad);
            }
            stock.setCantidadTotal(stock.getCantidadTotal() - cantidad);

            // Si se indica un lote concreto, descontar de ese lote
            if (req.getLoteId() != null) {
                LoteInventario lote = loteRepo.findByIdAndNegocioId(req.getLoteId(), negocioId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("LoteInventario", req.getLoteId()));
                descontarDeLote(lote, cantidad);
            } else {
                // FIFO: descontar de lotes disponibles en orden
                descontarFIFO(req.getProductoId(), req.getAlmacenId(), negocioId, cantidad);
            }
        }

        // Guardar movimiento
        MovimientoInventario mov = new MovimientoInventario();
        mov.setNegocioId(negocioId);
        mov.setProductoId(req.getProductoId());
        mov.setAlmacenId(req.getAlmacenId());
        mov.setLoteId(req.getLoteId());
        mov.setTipoMovimiento(tipo);
        mov.setCantidad(cantidad);
        mov.setMotivo(req.getMotivo());
        mov.setRealizadoPor(realizadoPor);

        return toResponse(movRepo.save(mov));
    }

    /* ── FIFO de salida ───────────────────────────────────── */

    private void descontarFIFO(Long productoId, Long almacenId, Long negocioId, int cantidadRequerida) {
        List<LoteInventario> lotes = loteRepo.findLotesDisponiblesFIFO(productoId, almacenId, negocioId);
        int restante = cantidadRequerida;

        for (LoteInventario lote : lotes) {
            if (restante <= 0) break;
            int disponible = lote.getCantidadRestante();
            int descuento = Math.min(disponible, restante);

            lote.setCantidadRestante(disponible - descuento);
            if (lote.getCantidadRestante() <= 0) {
                lote.setEstado(LoteEstado.agotado);
            }
            loteRepo.save(lote);
            restante -= descuento;
        }
        // Si no hay lotes suficientes, lo permitimos (stock ya fue validado).
    }

    private void descontarDeLote(LoteInventario lote, int cantidad) {
        if (lote.getCantidadRestante() < cantidad) {
            throw new StockInsuficienteException(
                    "Lote " + lote.getNumeroLote() + " tiene solo "
                    + lote.getCantidadRestante() + " unidades disponibles");
        }
        lote.setCantidadRestante(lote.getCantidadRestante() - cantidad);
        if (lote.getCantidadRestante() <= 0) {
            lote.setEstado(LoteEstado.agotado);
        }
        loteRepo.save(lote);
    }

    /* ── Helpers ──────────────────────────────────────────── */

    private TipoMovimiento parseTipo(String valor) {
        try {
            return TipoMovimiento.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException(
                    "Tipo de movimiento inválido: '" + valor
                    + "'. Valores permitidos: entrada_compra, salida_venta, "
                    + "ajuste_entrada, ajuste_salida, stock_inicial");
        }
    }

    /* ── Mapper ───────────────────────────────────────────── */

    private MovimientoInventarioResponse toResponse(MovimientoInventario m) {
        MovimientoInventarioResponse r = new MovimientoInventarioResponse();
        r.setId(m.getId());
        r.setNegocioId(m.getNegocioId());
        r.setProductoId(m.getProductoId());
        r.setAlmacenId(m.getAlmacenId());
        r.setLoteId(m.getLoteId());
        r.setTipoMovimiento(m.getTipoMovimiento() != null ? m.getTipoMovimiento().name() : null);
        r.setCantidad(m.getCantidad());
        r.setMotivo(m.getMotivo());
        r.setRealizadoPor(m.getRealizadoPor());
        r.setCreadoEn(m.getCreadoEn());

        if (m.getProducto() != null) {
            r.setProductoNombre(m.getProducto().getNombre());
        }
        if (m.getAlmacen() != null) {
            r.setAlmacenNombre(m.getAlmacen().getNombre());
        }
        if (m.getLote() != null) {
            r.setNumeroLote(m.getLote().getNumeroLote());
        }
        return r;
    }
}
