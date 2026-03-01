package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.pos.AbrirCajaRequest;
import DrinkGo.DrinkGo_backend.dto.pos.AnularVentaRequest;
import DrinkGo.DrinkGo_backend.dto.pos.CerrarCajaRequest;
import DrinkGo.DrinkGo_backend.dto.pos.CrearVentaPosRequest;
import DrinkGo.DrinkGo_backend.dto.pos.MovimientoCajaRequest;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;

/**
 * Servicio principal del módulo POS (Punto de Venta).
 * Contiene toda la lógica de negocio para:
 * <ul>
 *   <li>Creación y anulación de ventas</li>
 *   <li>Gestión de sesiones de caja</li>
 *   <li>Movimientos de caja</li>
 *   <li>Validación y deducción de stock (integración con Inventario)</li>
 *   <li>Productos enriquecidos con stock para el POS</li>
 * </ul>
 */
@Service
public class PosService {

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    @Autowired private VentasRepository ventasRepo;
    @Autowired private DetalleVentasRepository detalleVentasRepo;
    @Autowired private PagosVentaRepository pagosVentaRepo;
    @Autowired private CajasRegistradorasRepository cajasRepo;
    @Autowired private SesionesCajaRepository sesionesRepo;
    @Autowired private MovimientosCajaRepository movimientosRepo;
    @Autowired private MetodosPagoRepository metodosPagoRepo;
    @Autowired private NegociosRepository negociosRepo;
    @Autowired private SedesRepository sedesRepo;
    @Autowired private CategoriasGastoRepository categoriasGastoRepo;
    @Autowired private UsuariosRepository usuariosRepo;
    @Autowired private ProductosRepository productosRepo;
    @Autowired private ClientesRepository clientesRepo;
    @Autowired private StockInventarioRepository stockRepo;
    @Autowired private LotesInventarioRepository lotesRepo;
    @Autowired private MovimientosInventarioRepository movInventarioRepo;
    @Autowired private AlmacenesRepository almacenesRepo;
    @Autowired private CombosRepository combosRepo;
    @Autowired private DetalleCombosRepository detalleCombosRepo;

    @Autowired private FacturacionService facturacionService;

    // ═══════════════════════════════════════════════════════════════════
    //  PRODUCTOS POS (enriquecidos con stock)
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Retorna los productos del negocio enriquecidos con stock disponible
     * y precio de venta. Esto es lo que el frontend POS consume.
     */
    @Transactional(readOnly = true)
    public List<Productos> getProductosPosConStock(Long negocioId) {
        List<Productos> productos = productosRepo.findByNegocioId(negocioId);
        List<StockInventario> allStock = stockRepo.findByNegocioId(negocioId);

        // Mapear stock por productoId (sumar todos los almacenes)
        Map<Long, BigDecimal> stockMap = new HashMap<>();
        for (StockInventario si : allStock) {
            Long pid = si.getProducto().getId();
            stockMap.merge(pid,
                    si.getCantidadDisponible() != null ? si.getCantidadDisponible() : BigDecimal.ZERO,
                    BigDecimal::add);
        }

        // Enriquecer cada producto con stock
        for (Productos p : productos) {
            BigDecimal stockDisponible = stockMap.getOrDefault(p.getId(), BigDecimal.ZERO);
            p.setStock(stockDisponible);
        }

        return productos;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CREACIÓN DE VENTA POS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Crea una venta POS completa:
     * 1. Valida datos de entrada
     * 2. Valida stock disponible para cada ítem
     * 3. Calcula subtotal, descuentos, IGV y total
     * 4. Persiste Ventas + DetalleVentas + PagosVenta
     * 5. Registra movimiento de caja (ingreso)
     * 6. Deduce stock del inventario (FIFO por lotes)
     * 7. Genera comprobante de facturación (en transacción separada)
     *
     * @return La venta creada
     * @throws IllegalArgumentException si hay datos inválidos
     * @throws IllegalStateException si no hay stock suficiente
     */
    @Transactional
    public Ventas crearVentaPos(CrearVentaPosRequest request) {
        // ── 1. Validar campos requeridos ──
        validarRequestVenta(request);

        // ── 2. Resolver entidades referenciadas ──
        Negocios negocio = negociosRepo.getReferenceById(request.getNegocioId());
        Sedes sede = sedesRepo.getReferenceById(request.getSedeId());
        Usuarios usuario = usuariosRepo.getReferenceById(request.getUsuarioId());
        SesionesCaja sesion = sesionesRepo.getReferenceById(request.getSesionCajaId());

        // ── 3. Resolver almacén predeterminado del negocio ──
        Almacenes almacenDefault = almacenesRepo
                .findByNegocioIdAndEsPredeterminado(request.getNegocioId(), true)
                .orElse(null);

        // ── 4. Validar stock de cada ítem ──
        if (almacenDefault != null) {
            validarStockItems(request.getItems(), almacenDefault.getId(), request.getNegocioId());
        }

        // ── 5. Generar número de venta ──
        long count = ventasRepo.countByNegocioId(request.getNegocioId());
        String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String numeroVenta = "VTA-" + fechaStr + "-" + String.format("%04d", count + 1);

        // ── 6. Calcular totales ──
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CrearVentaPosRequest.ItemVenta item : request.getItems()) {
            BigDecimal precioUnit = item.getPrecioUnitario();
            if (precioUnit == null && item.getProductoId() != null) {
                Productos prod = productosRepo.findById(item.getProductoId()).orElse(null);
                if (prod != null && prod.getPrecioVenta() != null) {
                    precioUnit = prod.getPrecioVenta();
                    item.setPrecioUnitario(precioUnit);
                }
            }
            if (precioUnit == null) {
                precioUnit = BigDecimal.ZERO;
            }
            BigDecimal descItem = item.getDescuento() != null ? item.getDescuento() : BigDecimal.ZERO;
            BigDecimal itemSubtotal = precioUnit
                    .multiply(BigDecimal.valueOf(item.getCantidad()))
                    .subtract(descItem);
            subtotal = subtotal.add(itemSubtotal);
        }

        BigDecimal descuentoGlobal = request.getDescuentoGlobal() != null
                ? request.getDescuentoGlobal() : BigDecimal.ZERO;
        BigDecimal baseImponible = subtotal.subtract(descuentoGlobal);
        BigDecimal igv = baseImponible.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = baseImponible.add(igv);

        // ── 7. Crear entidad Ventas ──
        Ventas venta = new Ventas();
        venta.setNegocio(negocio);
        venta.setSede(sede);
        venta.setNumeroVenta(numeroVenta);
        venta.setTipoVenta(Ventas.TipoVenta.pos);
        venta.setSesionCaja(sesion);
        venta.setUsuario(usuario);
        venta.setVendedor(usuario);
        venta.setSubtotal(subtotal);
        venta.setMontoDescuento(descuentoGlobal);
        venta.setRazonDescuento(request.getRazonDescuento());
        venta.setMontoImpuesto(igv);
        venta.setTotal(total);
        venta.setEstado(Ventas.Estado.completada);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setCompletadoEn(LocalDateTime.now());

        // Tipo comprobante
        if (request.getTipoComprobante() != null) {
            venta.setTipoComprobante(Ventas.TipoComprobante.valueOf(request.getTipoComprobante()));
        } else {
            venta.setTipoComprobante(Ventas.TipoComprobante.boleta);
        }
        venta.setDocClienteNumero(request.getDocClienteNumero());
        venta.setDocClienteNombre(request.getDocClienteNombre());

        // Resolver cliente opcional
        if (request.getClienteId() != null) {
            Clientes cliente = clientesRepo.findById(request.getClienteId()).orElse(null);
            venta.setCliente(cliente);
        }

        venta = ventasRepo.save(venta);

        // ── 8. Crear detalle de ítems ──
        for (CrearVentaPosRequest.ItemVenta item : request.getItems()) {
            DetalleVentas detalle = new DetalleVentas();
            detalle.setVenta(venta);

            if (item.getProductoId() != null) {
                Productos producto = productosRepo.getReferenceById(item.getProductoId());
                detalle.setProducto(producto);
            }

            BigDecimal cantidad = BigDecimal.valueOf(item.getCantidad());
            BigDecimal precioUnit = item.getPrecioUnitario() != null
                    ? item.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal descItem = item.getDescuento() != null ? item.getDescuento() : BigDecimal.ZERO;
            BigDecimal itemSubtotal = precioUnit.multiply(cantidad).subtract(descItem);
            BigDecimal itemIgv = itemSubtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);

            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnit);
            detalle.setDescuento(descItem);
            detalle.setSubtotal(itemSubtotal);
            detalle.setImpuesto(itemIgv);
            detalle.setTotal(itemSubtotal.add(itemIgv));

            detalleVentasRepo.save(detalle);
        }

        // ── 9. Crear pagos ──
        for (CrearVentaPosRequest.PagoVenta pago : request.getPagos()) {
            PagosVenta pagoEntity = new PagosVenta();
            pagoEntity.setVenta(venta);
            pagoEntity.setMetodoPago(metodosPagoRepo.getReferenceById(pago.getMetodoPagoId()));
            pagoEntity.setMonto(pago.getMonto());
            pagoEntity.setNumeroReferencia(pago.getNumeroReferencia());
            pagoEntity.setFechaPago(LocalDateTime.now());
            pagosVentaRepo.save(pagoEntity);
        }

        // ── 10. Movimiento de caja (ingreso por venta) ──
        MovimientosCaja movimiento = new MovimientosCaja();
        movimiento.setSesionCaja(sesion);
        movimiento.setTipoMovimiento(MovimientosCaja.TipoMovimiento.ingreso_venta);
        movimiento.setMonto(total);
        movimiento.setDescripcion("Venta POS " + numeroVenta);
        movimiento.setVenta(venta);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimientosRepo.save(movimiento);

        // ── 11. Deducir stock del inventario ──
        if (almacenDefault != null) {
            deducirStockVenta(request.getItems(), almacenDefault, negocio, usuario, venta.getNumeroVenta());
        }

        // ── 12. Generar comprobante de facturación (transacción separada) ──
        try {
            facturacionService.emitirComprobanteDesdeVenta(venta, usuario);
        } catch (Exception ex) {
            System.err.println("WARN: No se pudo generar comprobante automático: " + ex.getMessage());
        }

        return venta;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ANULACIÓN DE VENTA
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public Ventas anularVenta(AnularVentaRequest request) {
        Ventas venta = ventasRepo.findById(request.getVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        if (venta.getEstado() == Ventas.Estado.anulada || venta.getEstado() == Ventas.Estado.cancelada) {
            throw new IllegalStateException("La venta ya está anulada/cancelada");
        }

        venta.setEstado(Ventas.Estado.anulada);
        venta.setCanceladoEn(LocalDateTime.now());
        venta.setRazonCancelacion(request.getRazonCancelacion());
        if (request.getUsuarioId() != null) {
            venta.setCanceladoPor(usuariosRepo.getReferenceById(request.getUsuarioId()));
        }
        ventasRepo.save(venta);

        // Movimiento de caja (egreso por anulación)
        if (venta.getSesionCaja() != null) {
            MovimientosCaja movimiento = new MovimientosCaja();
            movimiento.setSesionCaja(venta.getSesionCaja());
            movimiento.setTipoMovimiento(MovimientosCaja.TipoMovimiento.egreso_otro);
            movimiento.setMonto(venta.getTotal());
            movimiento.setDescripcion("Anulación de venta " + venta.getNumeroVenta());
            movimiento.setVenta(venta);
            movimiento.setFechaMovimiento(LocalDateTime.now());
            movimientosRepo.save(movimiento);
        }

        // Restaurar stock
        restaurarStockVenta(venta);

        // Anular comprobantes asociados
        facturacionService.anularComprobantesPorVenta(venta.getId());

        return venta;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SESIONES DE CAJA
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public SesionesCaja abrirCaja(AbrirCajaRequest request) {
        // Verificar que el usuario no tenga sesión abierta
        Optional<SesionesCaja> existente = sesionesRepo
                .findByUsuarioIdAndEstadoSesion(request.getUsuarioId(), SesionesCaja.EstadoSesion.abierta);
        if (existente.isPresent()) {
            throw new IllegalStateException("El usuario ya tiene una sesión de caja abierta");
        }

        CajasRegistradoras caja = cajasRepo.getReferenceById(request.getCajaId());
        Usuarios usuario = usuariosRepo.getReferenceById(request.getUsuarioId());

        SesionesCaja sesion = new SesionesCaja();
        sesion.setCaja(caja);
        sesion.setUsuario(usuario);
        sesion.setMontoApertura(request.getMontoApertura() != null
                ? request.getMontoApertura() : BigDecimal.ZERO);
        sesion.setEstadoSesion(SesionesCaja.EstadoSesion.abierta);
        sesion.setFechaApertura(LocalDateTime.now());
        sesion = sesionesRepo.save(sesion);

        // Movimiento de apertura
        MovimientosCaja movApertura = new MovimientosCaja();
        movApertura.setSesionCaja(sesion);
        movApertura.setTipoMovimiento(MovimientosCaja.TipoMovimiento.apertura);
        movApertura.setMonto(sesion.getMontoApertura());
        movApertura.setDescripcion("Apertura de caja");
        movApertura.setFechaMovimiento(LocalDateTime.now());
        movimientosRepo.save(movApertura);

        return sesion;
    }

    @Transactional
    public SesionesCaja cerrarCaja(CerrarCajaRequest request) {
        SesionesCaja sesion = sesionesRepo.findById(request.getSesionCajaId())
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        if (sesion.getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
            throw new IllegalStateException("La sesión no está abierta");
        }

        // Calcular totales desde movimientos
        List<MovimientosCaja> movimientos = movimientosRepo
                .findBySesionCajaIdOrderByFechaMovimientoDesc(sesion.getId());
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;
        for (MovimientosCaja mov : movimientos) {
            if (mov.getTipoMovimiento().name().startsWith("ingreso")) {
                totalIngresos = totalIngresos.add(mov.getMonto());
            } else if (mov.getTipoMovimiento().name().startsWith("egreso")) {
                totalEgresos = totalEgresos.add(mov.getMonto());
            }
        }

        sesion.setFechaCierre(LocalDateTime.now());
        sesion.setMontoCierre(request.getMontoCierre() != null ? request.getMontoCierre() : BigDecimal.ZERO);
        sesion.setTotalEfectivo(request.getTotalEfectivo() != null ? request.getTotalEfectivo() : BigDecimal.ZERO);
        sesion.setTotalTarjeta(request.getTotalTarjeta() != null ? request.getTotalTarjeta() : BigDecimal.ZERO);
        sesion.setTotalYape(request.getTotalYape() != null ? request.getTotalYape() : BigDecimal.ZERO);
        sesion.setTotalPlin(request.getTotalPlin() != null ? request.getTotalPlin() : BigDecimal.ZERO);
        sesion.setTotalOtros(request.getTotalOtros() != null ? request.getTotalOtros() : BigDecimal.ZERO);
        sesion.setTotalIngresos(totalIngresos);
        sesion.setTotalEgresos(totalEgresos);
        sesion.setObservaciones(request.getObservaciones());

        // Diferencia esperado vs real
        BigDecimal esperado = sesion.getMontoApertura().add(totalIngresos).subtract(totalEgresos);
        BigDecimal diferencia = sesion.getMontoCierre().subtract(esperado);
        sesion.setDiferenciaEsperadoReal(diferencia);

        sesion.setEstadoSesion(diferencia.compareTo(BigDecimal.ZERO) == 0
                ? SesionesCaja.EstadoSesion.cerrada
                : SesionesCaja.EstadoSesion.con_diferencia);

        sesionesRepo.save(sesion);

        // Movimiento de cierre
        MovimientosCaja movCierre = new MovimientosCaja();
        movCierre.setSesionCaja(sesion);
        movCierre.setTipoMovimiento(MovimientosCaja.TipoMovimiento.cierre);
        movCierre.setMonto(sesion.getMontoCierre());
        movCierre.setDescripcion("Cierre de caja");
        movCierre.setFechaMovimiento(LocalDateTime.now());
        movimientosRepo.save(movCierre);

        return sesion;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getResumenTurno(Long sesionCajaId) {
        SesionesCaja sesion = sesionesRepo.findById(sesionCajaId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        List<MovimientosCaja> movimientos = movimientosRepo
                .findBySesionCajaIdOrderByFechaMovimientoDesc(sesionCajaId);
        List<Ventas> ventas = ventasRepo.findBySesionCajaIdOrderByCreadoEnDesc(sesionCajaId);

        // ── Conteo y total de ventas ──
        BigDecimal totalVentas = BigDecimal.ZERO;
        int cantVentas = 0;
        int cantAnuladas = 0;
        for (Ventas v : ventas) {
            if (v.getEstado() == Ventas.Estado.completada) {
                totalVentas = totalVentas.add(v.getTotal());
                cantVentas++;
            } else if (v.getEstado() == Ventas.Estado.anulada) {
                cantAnuladas++;
            }
        }

        // ── Desglose por método de pago (solo ventas completadas) ──
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalYape = BigDecimal.ZERO;
        BigDecimal totalPlin = BigDecimal.ZERO;
        BigDecimal totalOtros = BigDecimal.ZERO;

        for (Ventas v : ventas) {
            if (v.getEstado() == Ventas.Estado.completada) {
                List<PagosVenta> pagos = pagosVentaRepo.findByVentaId(v.getId());
                for (PagosVenta pago : pagos) {
                    MetodosPago mp = pago.getMetodoPago();
                    BigDecimal monto = pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO;
                    if (mp != null && mp.getTipo() != null) {
                        switch (mp.getTipo()) {
                            case efectivo:
                                totalEfectivo = totalEfectivo.add(monto);
                                break;
                            case tarjeta_credito:
                            case tarjeta_debito:
                                totalTarjeta = totalTarjeta.add(monto);
                                break;
                            case yape:
                                totalYape = totalYape.add(monto);
                                break;
                            case plin:
                                totalPlin = totalPlin.add(monto);
                                break;
                            default:
                                totalOtros = totalOtros.add(monto);
                                break;
                        }
                    } else {
                        totalOtros = totalOtros.add(monto);
                    }
                }
            }
        }

        // ── Movimientos manuales (sin apertura, cierre ni ingreso_venta) ──
        BigDecimal totalIngresosManuales = BigDecimal.ZERO;
        BigDecimal totalEgresosManuales = BigDecimal.ZERO;
        for (MovimientosCaja mov : movimientos) {
            BigDecimal monto = mov.getMonto() != null ? mov.getMonto() : BigDecimal.ZERO;
            switch (mov.getTipoMovimiento()) {
                case ingreso_otro:
                    totalIngresosManuales = totalIngresosManuales.add(monto);
                    break;
                case egreso_gasto:
                case egreso_otro:
                    totalEgresosManuales = totalEgresosManuales.add(monto);
                    break;
                default:
                    break;
            }
        }

        // ── Efectivo esperado = apertura + efectivo ventas + ingresos manuales - egresos manuales ──
        BigDecimal montoApertura = sesion.getMontoApertura() != null
                ? sesion.getMontoApertura() : BigDecimal.ZERO;
        BigDecimal efectivoEsperado = montoApertura
                .add(totalEfectivo)
                .add(totalIngresosManuales)
                .subtract(totalEgresosManuales);

        // ── Construir resumen completo ──
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("sesion", sesion);
        resumen.put("movimientos", movimientos);

        // Ventas
        resumen.put("totalVentas", totalVentas);
        resumen.put("cantidadVentas", cantVentas);
        resumen.put("cantidadAnuladas", cantAnuladas);
        resumen.put("totalVentasCompletadas", cantVentas);
        resumen.put("totalVentasAnuladas", cantAnuladas);

        // Desglose por método de pago
        resumen.put("totalEfectivo", totalEfectivo);
        resumen.put("totalTarjeta", totalTarjeta);
        resumen.put("totalYape", totalYape);
        resumen.put("totalPlin", totalPlin);
        resumen.put("totalOtros", totalOtros);

        // Movimientos manuales
        resumen.put("totalIngresos", totalIngresosManuales);
        resumen.put("totalEgresos", totalEgresosManuales);

        // Efectivo esperado en caja
        resumen.put("efectivoEsperado", efectivoEsperado);

        return resumen;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  MOVIMIENTOS DE CAJA
    // ═══════════════════════════════════════════════════════════════════

    @Transactional
    public MovimientosCaja registrarMovimiento(MovimientoCajaRequest request) {
        SesionesCaja sesion = sesionesRepo.getReferenceById(request.getSesionCajaId());

        MovimientosCaja movimiento = new MovimientosCaja();
        movimiento.setSesionCaja(sesion);
        movimiento.setMonto(request.getMonto());
        movimiento.setDescripcion(request.getConcepto());
        movimiento.setFechaMovimiento(LocalDateTime.now());

        // Intentar usar el valor enum directamente, si no, fallback a ingreso/egreso
        String tipoStr = request.getTipo();
        MovimientosCaja.TipoMovimiento tipoMov = null;
        if (tipoStr != null) {
            try {
                tipoMov = MovimientosCaja.TipoMovimiento.valueOf(tipoStr);
            } catch (IllegalArgumentException ignored) {
                // Fallback: "ingreso" → ingreso_otro, anything else → egreso_otro
                tipoMov = tipoStr.toLowerCase().startsWith("ingreso")
                        ? MovimientosCaja.TipoMovimiento.ingreso_otro
                        : MovimientosCaja.TipoMovimiento.egreso_otro;
            }
        } else {
            tipoMov = MovimientosCaja.TipoMovimiento.egreso_otro;
        }
        movimiento.setTipoMovimiento(tipoMov);

        // Si es egreso_gasto y se proporcionó una categoría, vincularla
        if (request.getCategoriaGastoId() != null) {
            categoriasGastoRepo.findById(request.getCategoriaGastoId())
                    .ifPresent(movimiento::setCategoriaGasto);
        }

        return movimientosRepo.save(movimiento);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HELPERS PRIVADOS: STOCK
    // ═══════════════════════════════════════════════════════════════════

    private void validarRequestVenta(CrearVentaPosRequest request) {
        if (request.getNegocioId() == null || request.getSedeId() == null
                || request.getUsuarioId() == null || request.getSesionCajaId() == null) {
            throw new IllegalArgumentException("negocioId, sedeId, usuarioId y sesionCajaId son requeridos");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un item");
        }
        if (request.getPagos() == null || request.getPagos().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un pago");
        }
    }

    /**
     * Valida que haya stock suficiente para todos los ítems de la venta.
     */
    private void validarStockItems(List<CrearVentaPosRequest.ItemVenta> items, Long almacenId, Long negocioId) {
        for (CrearVentaPosRequest.ItemVenta item : items) {
            if (item.getProductoId() == null) continue;

            Optional<StockInventario> stockOpt = stockRepo
                    .findByProductoIdAndAlmacenId(item.getProductoId(), almacenId);

            if (stockOpt.isEmpty()) {
                // No hay registro de stock — permitir la venta (puede no tener inventario configurado)
                continue;
            }

            StockInventario stock = stockOpt.get();
            BigDecimal disponible = stock.getCantidadDisponible() != null
                    ? stock.getCantidadDisponible() : BigDecimal.ZERO;
            BigDecimal cantidadRequerida = BigDecimal.valueOf(item.getCantidad());

            if (disponible.compareTo(cantidadRequerida) < 0) {
                String nombreProducto = item.getNombreProducto() != null
                        ? item.getNombreProducto() : "ID " + item.getProductoId();
                throw new IllegalStateException(
                        "Stock insuficiente para '" + nombreProducto
                                + "'. Disponible: " + disponible + ", Requerido: " + cantidadRequerida);
            }
        }
    }

    /**
     * Deduce stock del inventario usando FIFO por lotes.
     * Crea movimientos de inventario tipo SALIDA por cada deducción.
     */
    private void deducirStockVenta(List<CrearVentaPosRequest.ItemVenta> items,
                                    Almacenes almacen, Negocios negocio,
                                    Usuarios usuario, String referenciaVenta) {
        for (CrearVentaPosRequest.ItemVenta item : items) {
            if (item.getProductoId() == null) continue;

            BigDecimal cantidadADeducir = BigDecimal.valueOf(item.getCantidad());
            Productos producto = productosRepo.getReferenceById(item.getProductoId());

            // Actualizar stock consolidado
            Optional<StockInventario> stockOpt = stockRepo
                    .findByProductoIdAndAlmacenIdForUpdate(item.getProductoId(), almacen.getId());

            if (stockOpt.isPresent()) {
                StockInventario stock = stockOpt.get();
                stock.setCantidadActual(stock.getCantidadActual().subtract(cantidadADeducir));
                stock.setCantidadDisponible(stock.getCantidadDisponible().subtract(cantidadADeducir));
                stockRepo.save(stock);
            }

            // Deducir de lotes (FIFO)
            List<LotesInventario> lotes = lotesRepo
                    .findLotesConStockFIFOForUpdate(item.getProductoId(), almacen.getId());

            BigDecimal restante = cantidadADeducir;
            for (LotesInventario lote : lotes) {
                if (restante.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal disponibleLote = lote.getCantidadActual();
                BigDecimal aDeducirDeLote = restante.min(disponibleLote);

                lote.setCantidadActual(disponibleLote.subtract(aDeducirDeLote));
                lotesRepo.save(lote);

                // Registrar movimiento de inventario
                MovimientosInventario movInv = new MovimientosInventario();
                movInv.setNegocio(negocio);
                movInv.setProducto(producto);
                movInv.setAlmacenOrigen(almacen);
                movInv.setLote(lote);
                movInv.setTipoMovimiento(MovimientosInventario.TipoMovimiento.salida);
                movInv.setCantidad(aDeducirDeLote);
                movInv.setCostoUnitario(lote.getCostoUnitario());
                movInv.setMontoTotal(aDeducirDeLote.multiply(lote.getCostoUnitario()).setScale(2, RoundingMode.HALF_UP));
                movInv.setMotivoMovimiento("Venta POS");
                movInv.setReferenciaDocumento(referenciaVenta);
                movInv.setUsuario(usuario);
                movInv.setFechaMovimiento(LocalDateTime.now());
                movInventarioRepo.save(movInv);

                restante = restante.subtract(aDeducirDeLote);
            }
        }
    }

    /**
     * Restaura el stock cuando una venta es anulada.
     * Lee los detalles de la venta y revierte cada deducción.
     */
    private void restaurarStockVenta(Ventas venta) {
        List<DetalleVentas> detalles = detalleVentasRepo.findByVentaId(venta.getId());
        if (detalles.isEmpty()) return;

        Almacenes almacenDefault = almacenesRepo
                .findByNegocioIdAndEsPredeterminado(venta.getNegocio().getId(), true)
                .orElse(null);
        if (almacenDefault == null) return;

        Usuarios usuarioAnulacion = venta.getCanceladoPor() != null
                ? venta.getCanceladoPor() : venta.getUsuario();

        for (DetalleVentas detalle : detalles) {
            if (detalle.getProducto() == null) continue;

            BigDecimal cantidadARestaurar = detalle.getCantidad();

            // Restaurar stock consolidado
            Optional<StockInventario> stockOpt = stockRepo
                    .findByProductoIdAndAlmacenIdForUpdate(
                            detalle.getProducto().getId(), almacenDefault.getId());

            if (stockOpt.isPresent()) {
                StockInventario stock = stockOpt.get();
                stock.setCantidadActual(stock.getCantidadActual().add(cantidadARestaurar));
                stock.setCantidadDisponible(stock.getCantidadDisponible().add(cantidadARestaurar));
                stockRepo.save(stock);
            }

            // Registrar movimiento de inventario (devolución)
            MovimientosInventario movInv = new MovimientosInventario();
            movInv.setNegocio(venta.getNegocio());
            movInv.setProducto(detalle.getProducto());
            movInv.setAlmacenDestino(almacenDefault);
            movInv.setTipoMovimiento(MovimientosInventario.TipoMovimiento.devolucion);
            movInv.setCantidad(cantidadARestaurar);
            movInv.setCostoUnitario(detalle.getPrecioUnitario());
            movInv.setMontoTotal(cantidadARestaurar.multiply(detalle.getPrecioUnitario())
                    .setScale(2, RoundingMode.HALF_UP));
            movInv.setMotivoMovimiento("Anulación de venta " + venta.getNumeroVenta());
            movInv.setReferenciaDocumento(venta.getNumeroVenta());
            movInv.setUsuario(usuarioAnulacion);
            movInv.setFechaMovimiento(LocalDateTime.now());
            movInventarioRepo.save(movInv);
        }
    }
}
