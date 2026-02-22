package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.LoteInventarioRequest;
import DrinkGo.DrinkGo_backend.dto.LoteInventarioResponse;
import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.AlmacenRepository;
import DrinkGo.DrinkGo_backend.repository.LoteInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoteInventarioService {

    private final LoteInventarioRepository loteRepo;
    private final ProductoRepository productoRepo;
    private final AlmacenRepository almacenRepo;
    private final StockInventarioService stockService;

    public LoteInventarioService(LoteInventarioRepository loteRepo,
                                  ProductoRepository productoRepo,
                                  AlmacenRepository almacenRepo,
                                  StockInventarioService stockService) {
        this.loteRepo = loteRepo;
        this.productoRepo = productoRepo;
        this.almacenRepo = almacenRepo;
        this.stockService = stockService;
    }

    /* ── Listar ───────────────────────────────────────────── */

    @Transactional(readOnly = true)
    public List<LoteInventarioResponse> listarTodos(Long negocioId) {
        return loteRepo.findByNegocioId(negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoteInventarioResponse obtenerPorId(Long id, Long negocioId) {
        LoteInventario lote = loteRepo.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("LoteInventario", id));
        return toResponse(lote);
    }

    @Transactional(readOnly = true)
    public List<LoteInventarioResponse> listarPorProductoYAlmacen(Long productoId, Long almacenId, Long negocioId) {
        return loteRepo.findByProductoIdAndAlmacenIdAndNegocioId(productoId, almacenId, negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoteInventarioResponse> listarDisponiblesFIFO(Long productoId, Long almacenId, Long negocioId) {
        return loteRepo.findLotesDisponiblesFIFO(productoId, almacenId, negocioId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoteInventarioResponse> listarProximosAVencer(Long negocioId, int diasAnticipacion) {
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAnticipacion);
        return loteRepo.findLotesProximosAVencer(negocioId, fechaLimite)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /* ── Crear ────────────────────────────────────────────── */

    @Transactional
    public LoteInventarioResponse crear(LoteInventarioRequest req, Long negocioId) {
        // Validar producto y almacén
        productoRepo.findByIdAndNegocioId(req.getProductoId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", req.getProductoId()));
        almacenRepo.findByIdAndNegocioId(req.getAlmacenId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacen", req.getAlmacenId()));

        // Verificar numero_lote único
        if (loteRepo.existsByNumeroLoteAndNegocioId(req.getNumeroLote(), negocioId)) {
            throw new OperacionInvalidaException(
                    "Ya existe un lote con número '" + req.getNumeroLote() + "'");
        }

        // Asegurar que existe stock_inventario (FK compuesta requiere el registro)
        stockService.obtenerOCrearStock(req.getProductoId(), req.getAlmacenId(), negocioId);

        LoteInventario lote = new LoteInventario();
        lote.setNegocioId(negocioId);
        lote.setProductoId(req.getProductoId());
        lote.setAlmacenId(req.getAlmacenId());
        lote.setNumeroLote(req.getNumeroLote());
        lote.setCantidadRestante(req.getCantidadRestante());
        lote.setCostoUnitarioCompra(req.getCostoUnitarioCompra());
        lote.setFechaVencimiento(req.getFechaVencimiento());
        lote.setEstado(LoteEstado.disponible);

        LoteInventario saved = loteRepo.save(lote);

        // Sumar al stock total
        var stock = stockService.obtenerOCrearStock(req.getProductoId(), req.getAlmacenId(), negocioId);
        stock.setCantidadTotal(stock.getCantidadTotal() + req.getCantidadRestante());

        return toResponse(saved);
    }

    /* ── Actualizar ───────────────────────────────────────── */

    @Transactional
    public LoteInventarioResponse actualizar(Long id, LoteInventarioRequest req, Long negocioId) {
        LoteInventario lote = loteRepo.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("LoteInventario", id));

        if (req.getNumeroLote() != null) {
            lote.setNumeroLote(req.getNumeroLote());
        }
        if (req.getCantidadRestante() != null) {
            lote.setCantidadRestante(req.getCantidadRestante());
            // Si se agotó, marcar como agotado
            if (lote.getCantidadRestante() <= 0) {
                lote.setEstado(LoteEstado.agotado);
            }
        }
        if (req.getCostoUnitarioCompra() != null) {
            lote.setCostoUnitarioCompra(req.getCostoUnitarioCompra());
        }
        if (req.getFechaVencimiento() != null) {
            lote.setFechaVencimiento(req.getFechaVencimiento());
        }

        return toResponse(loteRepo.save(lote));
    }

    /* ── Eliminar ─────────────────────────────────────────── */

    @Transactional
    public void eliminar(Long id, Long negocioId) {
        LoteInventario lote = loteRepo.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("LoteInventario", id));
        loteRepo.delete(lote);
    }

    /* ── Marcar vencidos (puede invocarse periódicamente) ── */

    @Transactional
    public int marcarLotesVencidos(Long negocioId) {
        List<LoteInventario> vencidos = loteRepo.findLotesVencidos(negocioId, LocalDate.now());
        for (LoteInventario l : vencidos) {
            l.setEstado(LoteEstado.vencido);
        }
        loteRepo.saveAll(vencidos);
        return vencidos.size();
    }

    /* ── Mapper ───────────────────────────────────────────── */

    private LoteInventarioResponse toResponse(LoteInventario l) {
        LoteInventarioResponse r = new LoteInventarioResponse();
        r.setId(l.getId());
        r.setNegocioId(l.getNegocioId());
        r.setProductoId(l.getProductoId());
        r.setAlmacenId(l.getAlmacenId());
        r.setNumeroLote(l.getNumeroLote());
        r.setCantidadRestante(l.getCantidadRestante());
        r.setCostoUnitarioCompra(l.getCostoUnitarioCompra());
        r.setFechaVencimiento(l.getFechaVencimiento());
        r.setEstado(l.getEstado() != null ? l.getEstado().name() : null);
        r.setCreadoEn(l.getCreadoEn());

        if (l.getProducto() != null) {
            r.setProductoNombre(l.getProducto().getNombre());
        }
        if (l.getAlmacen() != null) {
            r.setAlmacenNombre(l.getAlmacen().getNombre());
        }
        return r;
    }
}
