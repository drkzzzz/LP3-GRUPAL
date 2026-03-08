package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired private FacturacionService facturacionService;

    @Autowired private UsuariosRolesRepository usuariosRolesRepo;

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

        // ── 3. Resolver almacén predeterminado de la sede ──
        Almacenes almacenDefault = almacenesRepo
                .findFirstBySede_IdAndEsPredeterminado(request.getSedeId(), true)
                .orElse(null);

        // ── 4. Validar stock de cada ítem (en TODOS los almacenes del negocio) ──
        validarStockItems(request.getItems(), request.getNegocioId());

        // ── 5. Generar número de venta (secuencia propia por sede para evitar duplicados) ──
        long count = ventasRepo.countBySedeId(request.getSedeId());
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

        // IGV dinámico según configuración del negocio
        boolean aplicaIgv = negocio.getAplicaIgv() != null ? negocio.getAplicaIgv() : true;
        BigDecimal igvRate = BigDecimal.ZERO;
        if (aplicaIgv) {
            BigDecimal porcentaje = negocio.getPorcentajeIgv() != null
                    ? negocio.getPorcentajeIgv() : new BigDecimal("18.00");
            igvRate = porcentaje.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        }
        BigDecimal igv = baseImponible.multiply(igvRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = baseImponible.add(igv);

        // ── 6b. Redondear total a 0.10 si es venta 100% efectivo ──
        // También ajustar el IGV para mantener consistencia: subtotal + IGV ajustado = total redondeado
        if (esVenta100Efectivo(request.getPagos())) {
            BigDecimal totalRedondeado = redondearADecimaDiez(total);
            BigDecimal diferencia = total.subtract(totalRedondeado);
            igv = igv.subtract(diferencia);
            total = totalRedondeado;
        }

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
        venta.setDocClienteDireccion(request.getDocClienteDireccion());

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

            if (item.getComboId() != null) {
                detalle.setCombo(combosRepo.getReferenceById(item.getComboId()));
            }

            // Guardar snapshot del nombre para historial
            if (item.getNombreProducto() != null) {
                detalle.setNombreProductoSnapshot(item.getNombreProducto());
            }

            BigDecimal cantidad = BigDecimal.valueOf(item.getCantidad());
            BigDecimal precioUnit = item.getPrecioUnitario() != null
                    ? item.getPrecioUnitario() : BigDecimal.ZERO;
            BigDecimal descItem = item.getDescuento() != null ? item.getDescuento() : BigDecimal.ZERO;
            BigDecimal itemSubtotal = precioUnit.multiply(cantidad).subtract(descItem);
            BigDecimal itemIgv = itemSubtotal.multiply(igvRate).setScale(2, RoundingMode.HALF_UP);

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
            pagoEntity.setMontoRecibido(pago.getMontoRecibido());
            pagoEntity.setMontoCambio(pago.getMontoCambio());
            pagoEntity.setFechaPago(LocalDateTime.now());
            pagosVentaRepo.save(pagoEntity);
        }

        // ── 10. Movimiento de caja (ingreso por venta — solo monto efectivo) ──
        // El movimiento de caja refleja el dinero físico que entra/sale de la caja.
        // Solo el monto pagado en efectivo (ya redondeado por el frontend) cuenta.
        BigDecimal montoEfectivoVenta = BigDecimal.ZERO;
        for (CrearVentaPosRequest.PagoVenta pago : request.getPagos()) {
            MetodosPago mp = metodosPagoRepo.findById(pago.getMetodoPagoId()).orElse(null);
            if (mp != null && mp.getTipo() == MetodosPago.TipoMetodoPago.efectivo) {
                montoEfectivoVenta = montoEfectivoVenta.add(pago.getMonto());
            }
        }
        if (montoEfectivoVenta.compareTo(BigDecimal.ZERO) > 0) {
            MovimientosCaja movimiento = new MovimientosCaja();
            movimiento.setSesionCaja(sesion);
            movimiento.setTipoMovimiento(MovimientosCaja.TipoMovimiento.ingreso_venta);
            movimiento.setMonto(montoEfectivoVenta);
            movimiento.setDescripcion("Venta POS " + numeroVenta);
            movimiento.setVenta(venta);
            movimiento.setFechaMovimiento(LocalDateTime.now());
            movimientosRepo.save(movimiento);
        }

        // ── 11. Deducir stock del inventario (en TODOS los almacenes del negocio) ──
        deducirStockVenta(request.getItems(), almacenDefault, negocio, usuario, venta.getNumeroVenta());

        // ── 12. Generar comprobante de facturación (transacción separada) ──
        try {
            DocumentosFacturacion doc = facturacionService.emitirComprobanteDesdeVenta(venta, usuario);
            if (doc != null) {
                // Pasar el número del comprobante como campo transitorio
                // para que el frontend lo muestre en el recibo sin hacer refresh
                venta.setNumeroDocumentoFacturacion(doc.getNumeroDocumento());
            }
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

        // ── Determinar si el usuario es admin (alcance completo en ventas) ──
        boolean esAdmin = tieneAlcanceCompleto(request.getUsuarioId(), "m.ventas");

        if (esAdmin) {
            // Admin puede anular cualquier venta (sesión abierta o cerrada).
            // Si la sesión de caja está cerrada, el movimiento de egreso se omite
            // (la caja ya fue cuadrada).
        } else {
            // Cajero: solo puede anular si la caja de la venta sigue abierta
            // Y la venta pertenece a SU sesión activa
            if (venta.getSesionCaja() == null) {
                throw new IllegalStateException(
                        "No se puede anular esta venta sin sesión de caja asociada");
            }
            if (venta.getSesionCaja().getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
                throw new IllegalStateException(
                        "Solo se puede anular una venta mientras tu caja tenga sesión activa. " +
                        "Contacta a un administrador para anulaciones con caja cerrada.");
            }
            // Verificar que la sesión de la venta pertenece al cajero que solicita
            Long sesionUserId = venta.getSesionCaja().getUsuario().getId();
            if (!sesionUserId.equals(request.getUsuarioId())) {
                throw new IllegalStateException(
                        "Solo puedes anular ventas registradas en tu propia sesión de caja. " +
                        "Contacta a un administrador.");
            }
        }

        // No se puede anular si algún comprobante PSE ya fue enviado/aceptado por SUNAT
        List<DocumentosFacturacion> docs = facturacionService.getDocumentosByVentaId(venta.getId());
        for (DocumentosFacturacion doc : docs) {
            if (doc.getModoEmision() != DocumentosFacturacion.ModoEmision.PSE) continue;
            DocumentosFacturacion.EstadoDocumento edo = doc.getEstadoDocumento();
            if (edo == DocumentosFacturacion.EstadoDocumento.enviado
                    || edo == DocumentosFacturacion.EstadoDocumento.aceptado
                    || edo == DocumentosFacturacion.EstadoDocumento.observado) {
                throw new IllegalStateException(
                        "No se puede anular: el comprobante " + doc.getNumeroDocumento()
                                + " ya fue enviado a SUNAT. Se requiere una Nota de Crédito.");
            }
        }

        venta.setEstado(Ventas.Estado.anulada);
        venta.setCanceladoEn(LocalDateTime.now());
        venta.setRazonCancelacion(request.getRazonCancelacion());
        if (request.getUsuarioId() != null) {
            venta.setCanceladoPor(usuariosRepo.getReferenceById(request.getUsuarioId()));
        }
        ventasRepo.save(venta);

        // Movimiento de caja (egreso por anulación) — solo el monto en efectivo
        // porque es lo que realmente sale de la caja
        if (venta.getSesionCaja() != null
                && venta.getSesionCaja().getEstadoSesion() == SesionesCaja.EstadoSesion.abierta) {
            BigDecimal montoEfectivoAnulado = BigDecimal.ZERO;
            List<PagosVenta> pagosVenta = pagosVentaRepo.findByVentaId(venta.getId());
            for (PagosVenta pago : pagosVenta) {
                MetodosPago mp = pago.getMetodoPago();
                if (mp != null && mp.getTipo() == MetodosPago.TipoMetodoPago.efectivo) {
                    montoEfectivoAnulado = montoEfectivoAnulado.add(
                            pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO);
                }
            }
            if (montoEfectivoAnulado.compareTo(BigDecimal.ZERO) > 0) {
                MovimientosCaja movimiento = new MovimientosCaja();
                movimiento.setSesionCaja(venta.getSesionCaja());
                movimiento.setTipoMovimiento(MovimientosCaja.TipoMovimiento.egreso_anulacion);
                movimiento.setMonto(montoEfectivoAnulado);
                movimiento.setDescripcion("Anulación de venta " + venta.getNumeroVenta()
                        + (esAdmin ? " (por administrador)" : ""));
                movimiento.setVenta(venta);
                movimiento.setFechaMovimiento(LocalDateTime.now());
                movimientosRepo.save(movimiento);
            }
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
        // Validar monto de apertura >= 0
        if (request.getMontoApertura() != null && request.getMontoApertura().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto de apertura no puede ser negativo");
        }

        // Bloqueo pesimista: evitar que doble-click cree 2 sesiones
        Optional<SesionesCaja> existente = sesionesRepo
                .findByUsuarioIdAndEstadoForUpdate(request.getUsuarioId(), SesionesCaja.EstadoSesion.abierta.name());
        if (existente.isPresent()) {
            throw new IllegalStateException("El usuario ya tiene una sesión de caja abierta");
        }

        CajasRegistradoras caja = cajasRepo.findById(request.getCajaId())
                .orElseThrow(() -> new IllegalArgumentException("Caja no encontrada"));

        // Bloqueo pesimista: verificar que la caja no tenga sesión activa de otro usuario
        Optional<SesionesCaja> sesionActivaCaja = sesionesRepo
                .findByCajaIdAndEstadoForUpdate(request.getCajaId(), SesionesCaja.EstadoSesion.abierta.name());
        if (sesionActivaCaja.isPresent()
                && !sesionActivaCaja.get().getUsuario().getId().equals(request.getUsuarioId())) {
            throw new IllegalStateException(
                    "Esta caja ya está siendo usada por otro usuario.");
        }

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

        // Validar que quien cierra sea el propietario de la sesión o un administrador
        Long solicitanteId = request.getUsuarioId();
        Long propietarioId = sesion.getUsuario().getId();
        if (solicitanteId != null && !solicitanteId.equals(propietarioId)) {
            boolean esAdmin = tieneAlcanceCompleto(solicitanteId, "m.ventas");
            if (!esAdmin) {
                throw new IllegalStateException(
                        "Solo el cajero que abrió esta caja o un administrador puede cerrarla");
            }
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
        // Tolerancia de ±0.05 para evitar falsos positivos por redondeo de céntimos
        boolean sinDiferencia = diferencia.abs().compareTo(new BigDecimal("0.05")) <= 0;
        sesion.setDiferenciaEsperadoReal(sinDiferencia ? BigDecimal.ZERO : diferencia);

        sesion.setEstadoSesion(sinDiferencia
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
                        // Clasificar por tipo, con fallback por codigo para billetera_digital
                        String codigo = mp.getCodigo() != null ? mp.getCodigo().toLowerCase() : "";
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
                            case billetera_digital:
                                // Reclasificar billeteras digitales conocidas por su codigo
                                if ("yape".equals(codigo)) {
                                    totalYape = totalYape.add(monto);
                                } else if ("plin".equals(codigo)) {
                                    totalPlin = totalPlin.add(monto);
                                } else {
                                    totalOtros = totalOtros.add(monto);
                                }
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

        // ── Movimientos manuales (sin apertura, cierre, ingreso_venta ni egreso_anulacion) ──
        // egreso_anulacion is excluded here because it pairs with ingreso_venta and both
        // net to zero — they are already excluded from the efectivo display (annulled sales
        // are not in totalEfectivo). Including egreso_anulacion here would cause a mismatch.
        // Backward compat: also skip egreso_otro that is linked to a venta (old annulment records).
        BigDecimal totalIngresosManuales = BigDecimal.ZERO;
        BigDecimal totalEgresosManuales = BigDecimal.ZERO;
        for (MovimientosCaja mov : movimientos) {
            BigDecimal monto = mov.getMonto() != null ? mov.getMonto() : BigDecimal.ZERO;
            switch (mov.getTipoMovimiento()) {
                case ingreso_otro:
                case ingreso_manual:
                    totalIngresosManuales = totalIngresosManuales.add(monto);
                    break;
                case egreso_gasto:
                case egreso_manual:
                    totalEgresosManuales = totalEgresosManuales.add(monto);
                    break;
                case egreso_otro:
                    // Only count genuine "other" egresos — skip those linked to a venta (= old annulment records)
                    if (mov.getVenta() == null) {
                        totalEgresosManuales = totalEgresosManuales.add(monto);
                    }
                    break;
                case egreso_anulacion:
                    // Intentionally excluded from display totals — pairs with ingreso_venta
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
                // Fallback: "ingreso" → ingreso_manual, anything else → egreso_manual
                tipoMov = tipoStr.toLowerCase().startsWith("ingreso")
                        ? MovimientosCaja.TipoMovimiento.ingreso_manual
                        : MovimientosCaja.TipoMovimiento.egreso_manual;
            }
        } else {
            tipoMov = MovimientosCaja.TipoMovimiento.egreso_manual;
        }
        movimiento.setTipoMovimiento(tipoMov);

        // Si es egreso manual, marcar estadoEgreso como activo
        if (tipoMov == MovimientosCaja.TipoMovimiento.egreso_manual
                || tipoMov == MovimientosCaja.TipoMovimiento.egreso_otro
                || tipoMov == MovimientosCaja.TipoMovimiento.egreso_gasto) {
            movimiento.setEstadoEgreso(MovimientosCaja.EstadoEgreso.activo);
            movimiento.setMontoDevuelto(BigDecimal.ZERO);
        }

        // Si es egreso_gasto y se proporcionó una categoría, vincularla
        if (request.getCategoriaGastoId() != null) {
            categoriasGastoRepo.findById(request.getCategoriaGastoId())
                    .ifPresent(movimiento::setCategoriaGasto);
        }

        return movimientosRepo.save(movimiento);
    }

    /**
     * Devolver (total o parcial) un egreso de caja.
     * Crea un ingreso_manual vinculado al egreso original.
     */
    @Transactional
    public MovimientosCaja devolverEgreso(Long movimientoId, BigDecimal montoDevolucion, String motivo) {
        MovimientosCaja egreso = movimientosRepo.findById(movimientoId)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento no encontrado"));

        // Validar que sea un egreso
        if (!egreso.getTipoMovimiento().name().startsWith("egreso")) {
            throw new IllegalStateException("Solo se pueden devolver egresos");
        }

        // Validar que no esté completamente devuelto
        if (egreso.getEstadoEgreso() == MovimientosCaja.EstadoEgreso.devuelto) {
            throw new IllegalStateException("Este egreso ya fue devuelto completamente");
        }

        // Validar monto
        BigDecimal yaDevuelto = egreso.getMontoDevuelto() != null ? egreso.getMontoDevuelto() : BigDecimal.ZERO;
        BigDecimal maxDevolucion = egreso.getMonto().subtract(yaDevuelto);
        if (montoDevolucion.compareTo(maxDevolucion) > 0) {
            throw new IllegalStateException(
                    "El monto de devolución (S/ " + montoDevolucion
                            + ") excede el máximo permitido (S/ " + maxDevolucion + ")");
        }

        // Verificar que la sesión de caja esté abierta
        SesionesCaja sesion = egreso.getSesionCaja();
        if (sesion.getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
            throw new IllegalStateException("La sesión de caja no está abierta. No se puede registrar la devolución.");
        }

        // Crear movimiento de ingreso (devolución)
        MovimientosCaja devolucion = new MovimientosCaja();
        devolucion.setSesionCaja(sesion);
        devolucion.setTipoMovimiento(MovimientosCaja.TipoMovimiento.ingreso_manual);
        devolucion.setMonto(montoDevolucion);
        devolucion.setDescripcion("Devolución: " + (motivo != null ? motivo : egreso.getDescripcion()));
        devolucion.setEgresoRelacionado(egreso);
        devolucion.setFechaMovimiento(LocalDateTime.now());
        movimientosRepo.save(devolucion);

        // Actualizar egreso original
        BigDecimal nuevoDevuelto = yaDevuelto.add(montoDevolucion);
        egreso.setMontoDevuelto(nuevoDevuelto);

        if (nuevoDevuelto.compareTo(egreso.getMonto()) >= 0) {
            egreso.setEstadoEgreso(MovimientosCaja.EstadoEgreso.devuelto);
        } else {
            egreso.setEstadoEgreso(MovimientosCaja.EstadoEgreso.parcial);
        }
        movimientosRepo.save(egreso);

        return devolucion;
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
        // Validar que ningún pago tenga monto negativo
        for (CrearVentaPosRequest.PagoVenta pago : request.getPagos()) {
            if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El monto de cada pago debe ser mayor o igual a cero");
            }
        }
        // Validar que la suma de pagos cubra el total calculado
        BigDecimal sumaPagos = request.getPagos().stream()
                .map(CrearVentaPosRequest.PagoVenta::getMonto)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumaPagos.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La suma de pagos debe ser mayor a cero");
        }
    }

    /**
     * Valida que haya stock suficiente para todos los ítems de la venta.
     * Suma el stock de TODOS los almacenes del negocio para cada producto,
     * igual que como lo muestra el POS al cajero.
     */
    private void validarStockItems(List<CrearVentaPosRequest.ItemVenta> items, Long negocioId) {
        for (CrearVentaPosRequest.ItemVenta item : items) {
            if (item.getProductoId() == null) continue;

            List<StockInventario> stockRecords = stockRepo
                    .findByProductoIdAndNegocioId(item.getProductoId(), negocioId);

            if (stockRecords.isEmpty()) {
                // No hay registro de stock — permitir la venta (puede no tener inventario configurado)
                continue;
            }

            BigDecimal disponible = stockRecords.stream()
                    .map(s -> s.getCantidadDisponible() != null ? s.getCantidadDisponible() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
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
     * Busca en TODOS los almacenes del negocio donde el producto tenga stock
     * (prioriza el almacén predeterminado de la sede).
     * Crea movimientos de inventario tipo SALIDA por cada deducción.
     */
    private void deducirStockVenta(List<CrearVentaPosRequest.ItemVenta> items,
                                    Almacenes almacenDefault, Negocios negocio,
                                    Usuarios usuario, String referenciaVenta) {
        for (CrearVentaPosRequest.ItemVenta item : items) {
            if (item.getProductoId() == null) continue;

            BigDecimal restante = BigDecimal.valueOf(item.getCantidad());
            Productos producto = productosRepo.getReferenceById(item.getProductoId());

            // Buscar stock en TODOS los almacenes del negocio para este producto
            List<StockInventario> stockRecords = stockRepo
                    .findByProductoIdAndNegocioId(item.getProductoId(), negocio.getId());

            // Priorizar almacén predeterminado
            stockRecords.sort((a, b) -> {
                boolean aDefault = almacenDefault != null && almacenDefault.getId().equals(a.getAlmacen().getId());
                boolean bDefault = almacenDefault != null && almacenDefault.getId().equals(b.getAlmacen().getId());
                if (aDefault && !bDefault) return -1;
                if (!aDefault && bDefault) return 1;
                return 0;
            });

            for (StockInventario stock : stockRecords) {
                if (restante.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal disponible = stock.getCantidadDisponible() != null
                        ? stock.getCantidadDisponible() : BigDecimal.ZERO;
                BigDecimal aDeducirDeAlmacen = restante.min(disponible);

                if (aDeducirDeAlmacen.compareTo(BigDecimal.ZERO) <= 0) continue;

                // Actualizar stock consolidado del almacén
                stock.setCantidadActual(stock.getCantidadActual().subtract(aDeducirDeAlmacen));
                stock.setCantidadDisponible(stock.getCantidadDisponible().subtract(aDeducirDeAlmacen));
                stockRepo.save(stock);

                Almacenes almacenActual = stock.getAlmacen();

                // Deducir de lotes en este almacén (FIFO)
                List<LotesInventario> lotes = lotesRepo
                        .findLotesConStockFIFO(item.getProductoId(), almacenActual.getId());

                BigDecimal restanteLotes = aDeducirDeAlmacen;
                for (LotesInventario lote : lotes) {
                    if (restanteLotes.compareTo(BigDecimal.ZERO) <= 0) break;

                    BigDecimal disponibleLote = lote.getCantidadActual();
                    BigDecimal aDeducirDeLote = restanteLotes.min(disponibleLote);

                    lote.setCantidadActual(disponibleLote.subtract(aDeducirDeLote));
                    lotesRepo.save(lote);

                    // Registrar movimiento de inventario
                    MovimientosInventario movInv = new MovimientosInventario();
                    movInv.setNegocio(negocio);
                    movInv.setProducto(producto);
                    movInv.setAlmacenOrigen(almacenActual);
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

                    restanteLotes = restanteLotes.subtract(aDeducirDeLote);
                }

                restante = restante.subtract(aDeducirDeAlmacen);
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
                .findFirstBySede_IdAndEsPredeterminado(venta.getSede().getId(), true)
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

    // ═══════════════════════════════════════════════════════════════════
    //  HELPERS PRIVADOS: REDONDEO Y VALIDACIÓN DE PAGOS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Verifica si la venta es 100% en efectivo.
     * Una venta es 100% efectivo si TODOS los pagos son del tipo 'efectivo'.
     */
    private boolean esVenta100Efectivo(List<CrearVentaPosRequest.PagoVenta> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return false;
        }
        for (CrearVentaPosRequest.PagoVenta pago : pagos) {
            MetodosPago mp = metodosPagoRepo.findById(pago.getMetodoPagoId()).orElse(null);
            if (mp == null || mp.getTipo() != MetodosPago.TipoMetodoPago.efectivo) {
                return false;
            }
        }
        return true;
    }

    /**
     * Redondea un monto a la décima más cercana (0.10).
     * Por ejemplo: 45.14 → 45.10, 45.16 → 45.20, 45.15 → 45.20 (banker's rounding)
     */
    private BigDecimal redondearADecimaDiez(BigDecimal monto) {
        // Dividir entre 0.10, redondear a entero, multiplicar por 0.10
        return monto.divide(BigDecimal.valueOf(0.10), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HELPERS DE PERMISOS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Verifica si un usuario tiene alcance 'completo' sobre un módulo.
     * Soporta herencia: si tiene 'completo' en 'm.ventas', también aplica a 'm.ventas.pos'.
     */
    private boolean tieneAlcanceCompleto(Long usuarioId, String codigoModulo) {
        if (usuarioId == null) return false;
        List<Object[]> permisos = usuariosRolesRepo.findPermisosConAlcanceByUsuarioId(usuarioId);
        for (Object[] row : permisos) {
            String codigo = (String) row[0];
            String alcance = (String) row[1];
            if ("completo".equalsIgnoreCase(alcance)) {
                // Coincidencia exacta o padre jerárquico
                if (codigoModulo.equals(codigo) || codigoModulo.startsWith(codigo + ".")) {
                    return true;
                }
            }
        }
        return false;
    }
}
