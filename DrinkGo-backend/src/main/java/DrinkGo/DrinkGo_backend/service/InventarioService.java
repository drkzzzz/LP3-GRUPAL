package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.LoteEntradaRequest;
import DrinkGo.DrinkGo_backend.dto.LoteUpdateRequest;
import DrinkGo.DrinkGo_backend.dto.MovimientoRequest;
import DrinkGo.DrinkGo_backend.dto.MovimientoUpdateRequest;
import DrinkGo.DrinkGo_backend.dto.StockCreateRequest;
import DrinkGo.DrinkGo_backend.dto.StockUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.LoteInventario;
import DrinkGo.DrinkGo_backend.entity.LoteInventario.LoteEstado;
import DrinkGo.DrinkGo_backend.entity.MovimientoInventario;
import DrinkGo.DrinkGo_backend.entity.MovimientoInventario.TipoMovimiento;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.repository.LoteInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.MovimientoInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.StockInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio principal de inventario - Bloque 5.
 * Implementa RF-INV-001 a RF-INV-009:
 * - Stock consolidado por producto
 * - Entrada de lotes con trazabilidad
 * - Sistema FIFO para descuento de inventario
 * - Movimientos con validación de disponibilidad
 * - No permite stock negativo
 */
@Service
public class InventarioService {

    @Autowired
    private StockInventarioRepository stockRepository;

    @Autowired
    private LoteInventarioRepository loteRepository;

    @Autowired
    private MovimientoInventarioRepository movimientoRepository;

    @Autowired
    private AlertaInventarioService alertaService;

    // ============================================================
    // STOCK (RF-INV-001)
    // ============================================================

    /**
     * Listar todo el stock del negocio autenticado.
     */
    public List<StockInventario> listarStock(Long negocioId) {
        return stockRepository.findByNegocioId(negocioId);
    }

    /**
     * Obtener stock de un producto en todos los almacenes.
     */
    public List<StockInventario> listarStockProducto(Long negocioId, Long productoId) {
        return stockRepository.findByNegocioIdAndProductoId(negocioId, productoId);
    }

    /**
     * Obtener stock de un producto en un almacén específico.
     */
    public StockInventario obtenerStockProductoAlmacen(Long negocioId, Long productoId, Long almacenId) {
        return stockRepository.findByNegocioIdAndProductoIdAndAlmacenId(negocioId, productoId, almacenId)
                .orElseThrow(() -> new RuntimeException(
                        "Stock no encontrado para producto " + productoId +
                                " en almacén " + almacenId));
    }

    /**
     * Obtener stock por almacén.
     */
    public List<StockInventario> listarStockAlmacen(Long negocioId, Long almacenId) {
        return stockRepository.findByNegocioIdAndAlmacenId(negocioId, almacenId);
    }

    /**
     * Obtener o crear registro de stock para un producto/almacén.
     */
    private StockInventario obtenerOCrearStock(Long negocioId, Long productoId, Long almacenId) {
        return stockRepository.findByNegocioIdAndProductoIdAndAlmacenId(negocioId, productoId, almacenId)
                .orElseGet(() -> {
                    StockInventario nuevoStock = new StockInventario();
                    nuevoStock.setNegocioId(negocioId);
                    nuevoStock.setProductoId(productoId);
                    nuevoStock.setAlmacenId(almacenId);
                    nuevoStock.setCantidadEnMano(0);
                    nuevoStock.setCantidadReservada(0);
                    return stockRepository.save(nuevoStock);
                });
    }

    // ============================================================
    // LOTES (RF-INV-002, RF-INV-003)
    // ============================================================

    /**
     * Registrar entrada de un nuevo lote de inventario (RF-INV-002).
     * Crea el lote, actualiza stock, y registra movimiento de entrada.
     */
    @Transactional
    public LoteInventario registrarEntradaLote(Long negocioId, LoteEntradaRequest request) {
        // Validaciones
        if (request.getProductoId() == null) {
            throw new RuntimeException("El campo 'productoId' es obligatorio");
        }
        if (request.getAlmacenId() == null) {
            throw new RuntimeException("El campo 'almacenId' es obligatorio");
        }
        if (request.getNumeroLote() == null || request.getNumeroLote().isBlank()) {
            throw new RuntimeException("El campo 'numeroLote' es obligatorio");
        }
        if (request.getCantidadInicial() == null || request.getCantidadInicial() <= 0) {
            throw new RuntimeException("La cantidad inicial debe ser mayor a 0");
        }
        if (request.getPrecioCompra() == null) {
            throw new RuntimeException("El campo 'precioCompra' es obligatorio");
        }
        if (request.getFechaRecepcion() == null) {
            throw new RuntimeException("El campo 'fechaRecepcion' es obligatorio");
        }

        // Verificar que no exista un lote con el mismo número en el negocio
        if (loteRepository.existsByNegocioIdAndNumeroLote(negocioId, request.getNumeroLote())) {
            throw new RuntimeException("Ya existe un lote con el número: " + request.getNumeroLote());
        }

        // Crear el lote
        LoteInventario lote = new LoteInventario();
        lote.setNegocioId(negocioId);
        lote.setProductoId(request.getProductoId());
        lote.setAlmacenId(request.getAlmacenId());
        lote.setNumeroLote(request.getNumeroLote());
        lote.setCantidadInicial(request.getCantidadInicial());
        lote.setCantidadRestante(request.getCantidadInicial());
        lote.setPrecioCompra(request.getPrecioCompra());
        lote.setFechaFabricacion(request.getFechaFabricacion());
        lote.setFechaVencimiento(request.getFechaVencimiento());
        lote.setFechaRecepcion(request.getFechaRecepcion());
        lote.setProveedorId(request.getProveedorId());
        lote.setOrdenCompraId(request.getOrdenCompraId());
        lote.setEstado(LoteEstado.disponible);
        lote.setNotas(request.getNotas());

        LoteInventario loteGuardado = loteRepository.save(lote);

        // Actualizar stock
        StockInventario stock = obtenerOCrearStock(negocioId, request.getProductoId(), request.getAlmacenId());
        stock.setCantidadEnMano(stock.getCantidadEnMano() + request.getCantidadInicial());
        stock.setUltimoMovimientoEn(LocalDateTime.now());
        stockRepository.save(stock);

        // Registrar movimiento de entrada
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocioId(negocioId);
        movimiento.setProductoId(request.getProductoId());
        movimiento.setAlmacenId(request.getAlmacenId());
        movimiento.setLoteId(loteGuardado.getId());
        movimiento.setTipoMovimiento(TipoMovimiento.entrada_compra);
        movimiento.setCantidad(request.getCantidadInicial());
        movimiento.setCostoUnitario(request.getPrecioCompra());
        movimiento.setTipoReferencia("lote_inventario");
        movimiento.setReferenciaId(loteGuardado.getId());
        movimiento.setMotivo("Entrada de lote: " + request.getNumeroLote());
        movimientoRepository.save(movimiento);

        // Verificar alertas después de la entrada
        alertaService.verificarAlertasProducto(negocioId, request.getProductoId(), request.getAlmacenId());

        return loteGuardado;
    }

    /**
     * Listar lotes de un producto (orden FIFO).
     */
    public List<LoteInventario> listarLotesProducto(Long negocioId, Long productoId) {
        return loteRepository.findByNegocioIdAndProductoIdOrderByFechaRecepcionAsc(negocioId, productoId);
    }

    /**
     * Listar lotes de un producto en un almacén (orden FIFO).
     */
    public List<LoteInventario> listarLotesProductoAlmacen(Long negocioId, Long productoId, Long almacenId) {
        return loteRepository.findByNegocioIdAndProductoIdAndAlmacenIdOrderByFechaRecepcionAsc(
                negocioId, productoId, almacenId);
    }

    // ============================================================
    // SISTEMA FIFO (RF-INV-003)
    // ============================================================

    /**
     * Consumir stock usando FIFO (First In, First Out).
     * Descuenta del lote más antiguo primero.
     * Marca lotes como agotados cuando cantidad_restante llega a 0.
     * No permite stock negativo.
     */
    @Transactional
    public void consumirStockFIFO(Long negocioId, Long productoId, Long almacenId,
            int cantidadAConsumir, TipoMovimiento tipoMov,
            String motivo, String tipoReferencia,
            Long referenciaId, Long realizadoPor) {
        if (cantidadAConsumir <= 0) {
            throw new RuntimeException("La cantidad a consumir debe ser mayor a 0");
        }

        // Obtener lotes disponibles en orden FIFO
        List<LoteInventario> lotesFIFO = loteRepository.findLotesDisponiblesFIFO(
                negocioId, productoId, almacenId);

        // Calcular stock total disponible
        int stockDisponible = lotesFIFO.stream()
                .mapToInt(LoteInventario::getCantidadRestante)
                .sum();

        if (stockDisponible < cantidadAConsumir) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + stockDisponible +
                    ", Solicitado: " + cantidadAConsumir);
        }

        int restante = cantidadAConsumir;

        for (LoteInventario lote : lotesFIFO) {
            if (restante <= 0)
                break;

            int consumirDeLote = Math.min(restante, lote.getCantidadRestante());

            // Actualizar lote
            lote.setCantidadRestante(lote.getCantidadRestante() - consumirDeLote);
            if (lote.getCantidadRestante() == 0) {
                lote.setEstado(LoteEstado.agotado);
            }
            loteRepository.save(lote);

            // Registrar movimiento por cada lote afectado
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setNegocioId(negocioId);
            movimiento.setProductoId(productoId);
            movimiento.setAlmacenId(almacenId);
            movimiento.setLoteId(lote.getId());
            movimiento.setTipoMovimiento(tipoMov);
            movimiento.setCantidad(-consumirDeLote); // Negativo para salidas
            movimiento.setCostoUnitario(lote.getPrecioCompra());
            movimiento.setTipoReferencia(tipoReferencia);
            movimiento.setReferenciaId(referenciaId);
            movimiento.setMotivo(motivo);
            movimiento.setRealizadoPor(realizadoPor);
            movimientoRepository.save(movimiento);

            restante -= consumirDeLote;
        }

        // Actualizar stock
        StockInventario stock = obtenerOCrearStock(negocioId, productoId, almacenId);
        stock.setCantidadEnMano(stock.getCantidadEnMano() - cantidadAConsumir);

        // Validación: no permitir stock negativo
        if (stock.getCantidadEnMano() < 0) {
            throw new RuntimeException("Error: stock negativo detectado para producto " + productoId);
        }

        stock.setUltimoMovimientoEn(LocalDateTime.now());
        stockRepository.save(stock);

        // Verificar alertas después del consumo
        alertaService.verificarAlertasProducto(negocioId, productoId, almacenId);
    }

    // ============================================================
    // MOVIMIENTOS (RF-INV-006)
    // ============================================================

    /**
     * Registrar un movimiento de inventario manual.
     * Soporta ajustes, mermas, roturas, vencimientos, etc.
     */
    @Transactional
    public MovimientoInventario registrarMovimiento(Long negocioId, MovimientoRequest request, Long realizadoPor) {
        // Validaciones
        if (request.getProductoId() == null) {
            throw new RuntimeException("El campo 'productoId' es obligatorio");
        }
        if (request.getAlmacenId() == null) {
            throw new RuntimeException("El campo 'almacenId' es obligatorio");
        }
        if (request.getTipoMovimiento() == null) {
            throw new RuntimeException("El campo 'tipoMovimiento' es obligatorio");
        }
        if (request.getCantidad() == null || request.getCantidad() == 0) {
            throw new RuntimeException("La cantidad debe ser distinta de 0");
        }

        TipoMovimiento tipo = request.getTipoMovimiento();

        // Determinar si es entrada o salida
        boolean esEntrada = esMovimientoEntrada(tipo);
        boolean esSalida = esMovimientoSalida(tipo);

        if (esSalida) {
            // Para salidas, usar FIFO si no se especifica lote
            int cantidadAbsoluta = Math.abs(request.getCantidad());

            if (request.getLoteId() != null) {
                // Salida de un lote específico
                LoteInventario lote = loteRepository.findById(request.getLoteId())
                        .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + request.getLoteId()));

                if (!lote.getNegocioId().equals(negocioId)) {
                    throw new RuntimeException("El lote no pertenece al negocio autenticado");
                }
                if (lote.getCantidadRestante() < cantidadAbsoluta) {
                    throw new RuntimeException("Cantidad insuficiente en el lote. Disponible: " +
                            lote.getCantidadRestante());
                }

                // Actualizar lote
                lote.setCantidadRestante(lote.getCantidadRestante() - cantidadAbsoluta);
                if (lote.getCantidadRestante() == 0) {
                    lote.setEstado(LoteEstado.agotado);
                }
                // Si es vencimiento o merma, cambiar estado
                if (tipo == TipoMovimiento.vencimiento) {
                    lote.setEstado(LoteEstado.vencido);
                }
                loteRepository.save(lote);

                // Actualizar stock
                StockInventario stock = obtenerOCrearStock(negocioId, request.getProductoId(), request.getAlmacenId());
                stock.setCantidadEnMano(stock.getCantidadEnMano() - cantidadAbsoluta);
                if (stock.getCantidadEnMano() < 0) {
                    throw new RuntimeException("No se permite stock negativo");
                }
                stock.setUltimoMovimientoEn(LocalDateTime.now());
                stockRepository.save(stock);

                // Registrar movimiento
                MovimientoInventario movimiento = crearMovimiento(negocioId, request, -cantidadAbsoluta, realizadoPor);
                movimiento = movimientoRepository.save(movimiento);

                alertaService.verificarAlertasProducto(negocioId, request.getProductoId(), request.getAlmacenId());
                return movimiento;

            } else {
                // Salida FIFO - delegar
                consumirStockFIFO(negocioId, request.getProductoId(), request.getAlmacenId(),
                        cantidadAbsoluta, tipo, request.getMotivo(),
                        request.getTipoReferencia(), request.getReferenciaId(), realizadoPor);

                // Crear un registro de movimiento resumen
                MovimientoInventario movimiento = crearMovimiento(negocioId, request, -cantidadAbsoluta, realizadoPor);
                // No guardar duplicado, FIFO ya registró movimientos por lote
                return movimiento;
            }

        } else if (esEntrada) {
            // Para entradas de ajuste (no lote nuevo)
            int cantidadAbsoluta = Math.abs(request.getCantidad());

            // Actualizar stock
            StockInventario stock = obtenerOCrearStock(negocioId, request.getProductoId(), request.getAlmacenId());
            stock.setCantidadEnMano(stock.getCantidadEnMano() + cantidadAbsoluta);
            stock.setUltimoMovimientoEn(LocalDateTime.now());
            stockRepository.save(stock);

            // Si se especifica un lote, actualizar su cantidad
            if (request.getLoteId() != null) {
                LoteInventario lote = loteRepository.findById(request.getLoteId())
                        .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + request.getLoteId()));

                if (!lote.getNegocioId().equals(negocioId)) {
                    throw new RuntimeException("El lote no pertenece al negocio autenticado");
                }

                lote.setCantidadRestante(lote.getCantidadRestante() + cantidadAbsoluta);
                if (lote.getEstado() == LoteEstado.agotado) {
                    lote.setEstado(LoteEstado.disponible);
                }
                loteRepository.save(lote);
            }

            // Registrar movimiento
            MovimientoInventario movimiento = crearMovimiento(negocioId, request, cantidadAbsoluta, realizadoPor);
            movimiento = movimientoRepository.save(movimiento);

            alertaService.verificarAlertasProducto(negocioId, request.getProductoId(), request.getAlmacenId());
            return movimiento;

        } else {
            throw new RuntimeException("Tipo de movimiento no soportado: " + tipo);
        }
    }

    /**
     * Listar todos los movimientos del negocio.
     */
    public List<MovimientoInventario> listarMovimientos(Long negocioId) {
        return movimientoRepository.findByNegocioIdOrderByCreadoEnDesc(negocioId);
    }

    /**
     * Listar movimientos de un producto.
     */
    public List<MovimientoInventario> listarMovimientosProducto(Long negocioId, Long productoId) {
        return movimientoRepository.findByNegocioIdAndProductoIdOrderByCreadoEnDesc(negocioId, productoId);
    }

    /**
     * Listar movimientos de un almacén.
     */
    public List<MovimientoInventario> listarMovimientosAlmacen(Long negocioId, Long almacenId) {
        return movimientoRepository.findByNegocioIdAndAlmacenIdOrderByCreadoEnDesc(negocioId, almacenId);
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private boolean esMovimientoEntrada(TipoMovimiento tipo) {
        return tipo == TipoMovimiento.entrada_compra ||
                tipo == TipoMovimiento.entrada_devolucion ||
                tipo == TipoMovimiento.entrada_transferencia ||
                tipo == TipoMovimiento.ajuste_entrada ||
                tipo == TipoMovimiento.stock_inicial ||
                tipo == TipoMovimiento.entrada_produccion;
    }

    private boolean esMovimientoSalida(TipoMovimiento tipo) {
        return tipo == TipoMovimiento.salida_venta ||
                tipo == TipoMovimiento.salida_devolucion ||
                tipo == TipoMovimiento.salida_transferencia ||
                tipo == TipoMovimiento.ajuste_salida ||
                tipo == TipoMovimiento.merma ||
                tipo == TipoMovimiento.rotura ||
                tipo == TipoMovimiento.vencimiento ||
                tipo == TipoMovimiento.salida_produccion;
    }

    private MovimientoInventario crearMovimiento(Long negocioId, MovimientoRequest request,
            int cantidad, Long realizadoPor) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setNegocioId(negocioId);
        movimiento.setProductoId(request.getProductoId());
        movimiento.setAlmacenId(request.getAlmacenId());
        movimiento.setLoteId(request.getLoteId());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setCantidad(cantidad);
        movimiento.setCostoUnitario(request.getCostoUnitario());
        movimiento.setTipoReferencia(request.getTipoReferencia());
        movimiento.setReferenciaId(request.getReferenciaId());
        movimiento.setMotivo(request.getMotivo());
        movimiento.setRealizadoPor(realizadoPor);
        return movimiento;
    }

    // ============================================================
    // ACTUALIZAR STOCK (PUT)
    // ============================================================

    /**
     * Actualizar stock manualmente.
     */
    @Transactional
    public StockInventario actualizarStock(Long negocioId, Long stockId, StockUpdateRequest request) {
        StockInventario stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado: " + stockId));

        if (!stock.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("El stock no pertenece al negocio autenticado");
        }

        if (request.getCantidadEnMano() != null) {
            stock.setCantidadEnMano(request.getCantidadEnMano());
        }
        if (request.getCantidadReservada() != null) {
            stock.setCantidadReservada(request.getCantidadReservada());
        }
        stock.setUltimoMovimientoEn(LocalDateTime.now());

        return stockRepository.save(stock);
    }

    /**
     * Eliminar stock (borrado físico).
     * NOTA: La tabla stock_inventario NO tiene columna eliminado_en.
     */
    @Transactional
    public void eliminarStock(Long negocioId, Long stockId) {
        StockInventario stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado: " + stockId));

        if (!stock.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("El stock no pertenece al negocio autenticado");
        }

        stockRepository.delete(stock);
    }

    // ============================================================
    // ACTUALIZAR LOTE (PUT)
    // ============================================================

    /**
     * Actualizar un lote de inventario.
     */
    @Transactional
    public LoteInventario actualizarLote(Long negocioId, Long loteId, LoteUpdateRequest request) {
        LoteInventario lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + loteId));

        if (!lote.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("El lote no pertenece al negocio autenticado");
        }

        if (request.getNotas() != null) {
            lote.setNotas(request.getNotas());
        }
        if (request.getFechaVencimiento() != null) {
            lote.setFechaVencimiento(request.getFechaVencimiento());
        }
        if (request.getFechaFabricacion() != null) {
            lote.setFechaFabricacion(request.getFechaFabricacion());
        }
        if (request.getPrecioCompra() != null) {
            lote.setPrecioCompra(request.getPrecioCompra());
        }
        if (request.getEstado() != null) {
            lote.setEstado(LoteEstado.valueOf(request.getEstado()));
        }

        return loteRepository.save(lote);
    }

    /**
     * Eliminar lote (borrado físico).
     * NOTA: La tabla lotes_inventario NO tiene columna eliminado_en.
     */
    @Transactional
    public void eliminarLote(Long negocioId, Long loteId) {
        LoteInventario lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado: " + loteId));

        if (!lote.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("El lote no pertenece al negocio autenticado");
        }

        loteRepository.delete(lote);
    }

    /**
     * Listar todos los lotes del negocio.
     */
    public List<LoteInventario> listarLotes(Long negocioId) {
        return loteRepository.findByNegocioId(negocioId);
    }

    // ============================================================
    // CREAR STOCK (POST)
    // ============================================================

    /**
     * Crear un registro de stock manualmente.
     */
    @Transactional
    public StockInventario crearStock(Long negocioId, StockCreateRequest request) {
        if (request.getProductoId() == null) {
            throw new RuntimeException("El campo 'productoId' es obligatorio");
        }
        if (request.getAlmacenId() == null) {
            throw new RuntimeException("El campo 'almacenId' es obligatorio");
        }

        // Verificar que no exista ya un stock para este producto/almacén
        if (stockRepository.findByNegocioIdAndProductoIdAndAlmacenId(
                negocioId, request.getProductoId(), request.getAlmacenId()).isPresent()) {
            throw new RuntimeException("Ya existe un registro de stock para producto " +
                    request.getProductoId() + " en almacén " + request.getAlmacenId());
        }

        StockInventario stock = new StockInventario();
        stock.setNegocioId(negocioId);
        stock.setProductoId(request.getProductoId());
        stock.setAlmacenId(request.getAlmacenId());
        stock.setCantidadEnMano(request.getCantidadEnMano() != null ? request.getCantidadEnMano() : 0);
        stock.setCantidadReservada(request.getCantidadReservada() != null ? request.getCantidadReservada() : 0);
        stock.setUltimoMovimientoEn(LocalDateTime.now());

        return stockRepository.save(stock);
    }

    // ============================================================
    // ACTUALIZAR / ELIMINAR MOVIMIENTO (PUT / DELETE)
    // ============================================================

    /**
     * Actualizar un movimiento de inventario (solo campos editables).
     */
    @Transactional
    public MovimientoInventario actualizarMovimiento(Long negocioId, Long movimientoId,
            MovimientoUpdateRequest request) {
        MovimientoInventario movimiento = movimientoRepository.findByIdAndNegocioId(movimientoId, negocioId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado: " + movimientoId));

        if (request.getMotivo() != null) {
            movimiento.setMotivo(request.getMotivo());
        }
        if (request.getTipoReferencia() != null) {
            movimiento.setTipoReferencia(request.getTipoReferencia());
        }
        if (request.getReferenciaId() != null) {
            movimiento.setReferenciaId(request.getReferenciaId());
        }

        return movimientoRepository.save(movimiento);
    }

    /**
     * Eliminar movimiento (borrado físico).
     * NOTA: La tabla movimientos_inventario NO tiene columna eliminado_en.
     */
    @Transactional
    public void eliminarMovimiento(Long negocioId, Long movimientoId) {
        MovimientoInventario movimiento = movimientoRepository.findByIdAndNegocioId(movimientoId, negocioId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado: " + movimientoId));

        movimientoRepository.delete(movimiento);
    }
}
