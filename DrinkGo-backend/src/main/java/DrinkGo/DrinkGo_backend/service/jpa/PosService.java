package DrinkGo.DrinkGo_backend.service.jpa;

import DrinkGo.DrinkGo_backend.dto.pos.*;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import DrinkGo.DrinkGo_backend.service.IPosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementación del servicio POS con reglas de negocio para licorería.
 */
@Service
public class PosService implements IPosService {

    @Autowired
    private CajasRegistradorasRepository cajasRepo;

    @Autowired
    private SesionesCajaRepository sesionesRepo;

    @Autowired
    private MovimientosCajaRepository movimientosRepo;

    @Autowired
    private VentasRepository ventasRepo;

    @Autowired
    private DetalleVentasRepository detalleVentasRepo;

    @Autowired
    private PagosVentaRepository pagosRepo;

    @Autowired
    private MetodosPagoRepository metodosPagoRepo;

    @Autowired
    private ProductosRepository productosRepo;

    @Autowired
    private NegociosRepository negociosRepo;

    @Autowired
    private SedesRepository sedesRepo;

    // ════════════════════════════════════════════════════════════
    //  CAJAS REGISTRADORAS
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<CajasRegistradoras> listarCajasPorNegocio(Long negocioId) {
        return cajasRepo.findByNegocioId(negocioId);
    }

    @Override
    @Transactional(readOnly = true)
    public CajasRegistradoras obtenerCaja(Long cajaId, Long negocioId) {
        return cajasRepo.findByIdAndNegocioId(cajaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Caja registradora no encontrada"));
    }

    @Override
    @Transactional
    public CajasRegistradoras crearCaja(CrearCajaRequest request) {
        Negocios negocio = negociosRepo.findById(request.getNegocioId())
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

        Sedes sede;
        if (request.getSedeId() != null) {
            sede = sedesRepo.findById(request.getSedeId())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        } else {
            List<Sedes> sedes = sedesRepo.findByNegocioId(request.getNegocioId());
            if (sedes.isEmpty()) {
                throw new RuntimeException("El negocio no tiene sedes registradas");
            }
            sede = sedes.get(0);
        }

        CajasRegistradoras caja = new CajasRegistradoras();
        caja.setNegocio(negocio);
        caja.setSede(sede);
        caja.setNombreCaja(request.getNombreCaja());
        caja.setCodigo(request.getCodigo());
        caja.setMontoAperturaDefecto(
                request.getMontoAperturaDefecto() != null ? request.getMontoAperturaDefecto() : java.math.BigDecimal.ZERO);
        caja.setEstaActivo(true);
        return cajasRepo.save(caja);
    }

    @Override
    @Transactional
    public CajasRegistradoras actualizarCaja(CrearCajaRequest request) {
        CajasRegistradoras caja = cajasRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

        if (request.getNombreCaja() != null) caja.setNombreCaja(request.getNombreCaja());
        if (request.getCodigo() != null) caja.setCodigo(request.getCodigo());
        if (request.getMontoAperturaDefecto() != null) caja.setMontoAperturaDefecto(request.getMontoAperturaDefecto());
        if (request.getSedeId() != null) {
            Sedes sede = sedesRepo.findById(request.getSedeId())
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
            caja.setSede(sede);
        }
        return cajasRepo.save(caja);
    }

    // ════════════════════════════════════════════════════════════
    //  SESIONES DE CAJA (TURNOS)
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public SesionesCaja abrirCaja(AbrirCajaRequest request) {
        // Validar que la caja existe
        CajasRegistradoras caja = cajasRepo.findById(request.getCajaId())
                .orElseThrow(() -> new RuntimeException("Caja registradora no encontrada"));

        // Validar que la caja está activa
        if (!Boolean.TRUE.equals(caja.getEstaActivo())) {
            throw new RuntimeException("La caja registradora no está activa");
        }

        // Validar que no haya sesión abierta en esta caja
        Optional<SesionesCaja> sesionExistente = sesionesRepo
                .findByCajaIdAndEstadoSesion(request.getCajaId(), SesionesCaja.EstadoSesion.abierta);
        if (sesionExistente.isPresent()) {
            throw new RuntimeException("Esta caja ya tiene una sesión abierta");
        }

        // Validar que el usuario no tenga otra sesión abierta
        Optional<SesionesCaja> sesionUsuario = sesionesRepo
                .findByUsuarioIdAndEstadoSesion(request.getUsuarioId(), SesionesCaja.EstadoSesion.abierta);
        if (sesionUsuario.isPresent()) {
            throw new RuntimeException("Ya tiene una sesión de caja abierta en otra caja");
        }

        // Crear sesión
        SesionesCaja sesion = new SesionesCaja();
        sesion.setCaja(caja);

        Usuarios usuario = new Usuarios();
        usuario.setId(request.getUsuarioId());
        sesion.setUsuario(usuario);

        BigDecimal montoApertura = request.getMontoApertura() != null
                ? request.getMontoApertura()
                : caja.getMontoAperturaDefecto();
        sesion.setMontoApertura(montoApertura);
        sesion.setFechaApertura(LocalDateTime.now());
        sesion.setEstadoSesion(SesionesCaja.EstadoSesion.abierta);

        SesionesCaja sesionGuardada = sesionesRepo.save(sesion);

        // Registrar movimiento de apertura
        MovimientosCaja movApertura = new MovimientosCaja();
        movApertura.setSesionCaja(sesionGuardada);
        movApertura.setTipoMovimiento(MovimientosCaja.TipoMovimiento.apertura);
        movApertura.setMonto(montoApertura);
        movApertura.setDescripcion("Apertura de caja con monto inicial: S/ " + montoApertura);
        movApertura.setFechaMovimiento(LocalDateTime.now());
        movimientosRepo.save(movApertura);

        return sesionGuardada;
    }

    @Override
    @Transactional
    public SesionesCaja cerrarCaja(CerrarCajaRequest request) {
        SesionesCaja sesion = sesionesRepo.findById(request.getSesionCajaId())
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada"));

        if (sesion.getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
            throw new RuntimeException("La sesión de caja ya está cerrada");
        }

        // Calcular totales del turno
        List<Ventas> ventasTurno = ventasRepo
                .findBySesionCajaIdAndEstado(request.getSesionCajaId(), Ventas.Estado.completada);

        BigDecimal totalVentas = BigDecimal.ZERO;
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalYape = BigDecimal.ZERO;
        BigDecimal totalPlin = BigDecimal.ZERO;
        BigDecimal totalOtros = BigDecimal.ZERO;

        for (Ventas venta : ventasTurno) {
            totalVentas = totalVentas.add(venta.getTotal());
            List<PagosVenta> pagos = pagosRepo.findByVentaId(venta.getId());
            for (PagosVenta pago : pagos) {
                MetodosPago metodo = pago.getMetodoPago();
                if (metodo != null && metodo.getTipo() != null) {
                    switch (metodo.getTipo()) {
                        case efectivo:
                            totalEfectivo = totalEfectivo.add(pago.getMonto());
                            break;
                        case tarjeta_credito:
                        case tarjeta_debito:
                            totalTarjeta = totalTarjeta.add(pago.getMonto());
                            break;
                        case yape:
                            totalYape = totalYape.add(pago.getMonto());
                            break;
                        case plin:
                            totalPlin = totalPlin.add(pago.getMonto());
                            break;
                        default:
                            totalOtros = totalOtros.add(pago.getMonto());
                            break;
                    }
                }
            }
        }

        // Sumar movimientos manuales (ingresos/egresos)
        List<MovimientosCaja> movimientos = movimientosRepo
                .findBySesionCajaIdOrderByFechaMovimientoDesc(request.getSesionCajaId());
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalEgresos = BigDecimal.ZERO;
        for (MovimientosCaja mov : movimientos) {
            if (mov.getTipoMovimiento() == MovimientosCaja.TipoMovimiento.ingreso_otro) {
                totalIngresos = totalIngresos.add(mov.getMonto());
            } else if (mov.getTipoMovimiento() == MovimientosCaja.TipoMovimiento.egreso_gasto
                    || mov.getTipoMovimiento() == MovimientosCaja.TipoMovimiento.egreso_otro) {
                totalEgresos = totalEgresos.add(mov.getMonto());
            }
        }

        // Efectivo esperado = apertura + efectivo ventas + ingresos_otro - egresos
        BigDecimal efectivoEsperado = sesion.getMontoApertura()
                .add(totalEfectivo)
                .add(totalIngresos)
                .subtract(totalEgresos);

        BigDecimal montoContado = request.getMontoContado() != null
                ? request.getMontoContado()
                : efectivoEsperado;

        BigDecimal diferencia = montoContado.subtract(efectivoEsperado);

        // Actualizar sesión
        sesion.setFechaCierre(LocalDateTime.now());
        sesion.setMontoCierre(montoContado);
        sesion.setTotalEfectivo(totalEfectivo);
        sesion.setTotalTarjeta(totalTarjeta);
        sesion.setTotalYape(totalYape);
        sesion.setTotalPlin(totalPlin);
        sesion.setTotalOtros(totalOtros);
        sesion.setTotalIngresos(totalIngresos.add(totalVentas));
        sesion.setTotalEgresos(totalEgresos);
        sesion.setDiferenciaEsperadoReal(diferencia);
        sesion.setObservaciones(request.getObservaciones());

        if (diferencia.abs().compareTo(BigDecimal.ZERO) > 0) {
            sesion.setEstadoSesion(SesionesCaja.EstadoSesion.con_diferencia);
        } else {
            sesion.setEstadoSesion(SesionesCaja.EstadoSesion.cerrada);
        }

        // Registrar movimiento de cierre
        MovimientosCaja movCierre = new MovimientosCaja();
        movCierre.setSesionCaja(sesion);
        movCierre.setTipoMovimiento(MovimientosCaja.TipoMovimiento.cierre);
        movCierre.setMonto(montoContado);
        movCierre.setDescripcion("Cierre de caja. Esperado: S/ " + efectivoEsperado
                + " | Contado: S/ " + montoContado + " | Diferencia: S/ " + diferencia);
        movCierre.setFechaMovimiento(LocalDateTime.now());
        movimientosRepo.save(movCierre);

        return sesionesRepo.save(sesion);
    }

    @Override
    @Transactional(readOnly = true)
    public SesionesCaja obtenerSesionActiva(Long usuarioId) {
        return sesionesRepo.findByUsuarioIdAndEstadoSesion(usuarioId, SesionesCaja.EstadoSesion.abierta)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SesionesCaja> listarSesionesPorNegocio(Long negocioId) {
        return sesionesRepo.findByCajaNegocioIdOrderByFechaAperturaDesc(negocioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SesionesCaja> listarSesionesPorCaja(Long cajaId) {
        return sesionesRepo.findByCajaIdOrderByFechaAperturaDesc(cajaId);
    }

    // ════════════════════════════════════════════════════════════
    //  MOVIMIENTOS DE CAJA
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public MovimientosCaja registrarMovimiento(MovimientoCajaRequest request) {
        SesionesCaja sesion = sesionesRepo.findById(request.getSesionCajaId())
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada"));

        if (sesion.getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
            throw new RuntimeException("No se pueden registrar movimientos en una caja cerrada");
        }

        MovimientosCaja movimiento = new MovimientosCaja();
        movimiento.setSesionCaja(sesion);
        movimiento.setTipoMovimiento(MovimientosCaja.TipoMovimiento.valueOf(request.getTipoMovimiento()));
        movimiento.setMonto(request.getMonto());
        movimiento.setDescripcion(request.getDescripcion());
        movimiento.setFechaMovimiento(LocalDateTime.now());

        return movimientosRepo.save(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientosCaja> listarMovimientosSesion(Long sesionCajaId) {
        return movimientosRepo.findBySesionCajaIdOrderByFechaMovimientoDesc(sesionCajaId);
    }

    // ════════════════════════════════════════════════════════════
    //  VENTAS POS
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public Ventas crearVentaPos(CrearVentaPosRequest request) {
        // Validar sesión de caja activa
        SesionesCaja sesion = sesionesRepo.findById(request.getSesionCajaId())
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada"));

        if (sesion.getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
            throw new RuntimeException("No se puede vender: la caja no está abierta");
        }

        // Validar items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un producto");
        }

        // Validar pagos
        if (request.getPagos() == null || request.getPagos().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un pago");
        }

        // Calcular subtotal y total de items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemVentaPosDTO item : request.getItems()) {
            BigDecimal lineaTotal = item.getPrecioUnitario()
                    .multiply(item.getCantidad())
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal descuentoItem = item.getDescuento() != null ? item.getDescuento() : BigDecimal.ZERO;
            subtotal = subtotal.add(lineaTotal.subtract(descuentoItem));
        }

        BigDecimal descuentoGlobal = request.getDescuentoGlobal() != null
                ? request.getDescuentoGlobal()
                : BigDecimal.ZERO;
        BigDecimal baseImponible = subtotal.subtract(descuentoGlobal).setScale(2, RoundingMode.HALF_UP);

        // Calcular IGV (18%) sobre la base imponible
        BigDecimal IGV_RATE = new BigDecimal("0.18");
        BigDecimal montoImpuesto = baseImponible.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = baseImponible.add(montoImpuesto).setScale(2, RoundingMode.HALF_UP);

        // Validar suma de pagos >= total
        BigDecimal sumaPagos = BigDecimal.ZERO;
        for (PagoVentaPosDTO pago : request.getPagos()) {
            sumaPagos = sumaPagos.add(pago.getMonto());
        }
        if (sumaPagos.compareTo(total) < 0) {
            throw new RuntimeException("El monto total de pagos (S/ " + sumaPagos
                    + ") es menor al total de la venta (S/ " + total + ")");
        }

        // Validar stock de productos
        for (ItemVentaPosDTO item : request.getItems()) {
            Optional<Productos> productoOpt = productosRepo.findById(item.getProductoId());
            if (productoOpt.isPresent()) {
                Productos producto = productoOpt.get();
                if (producto.getStock() != null && producto.getStock() < item.getCantidad().intValue()) {
                    throw new RuntimeException("Stock insuficiente para: " + producto.getNombre()
                            + " (disponible: " + producto.getStock() + ", solicitado: " + item.getCantidad() + ")");
                }
            }
        }

        // Generar número de venta
        int maxNum = ventasRepo.findMaxNumeroVentaByNegocioId(request.getNegocioId());
        String numeroVenta = String.format("V-%06d", maxNum + 1);

        // Crear venta
        Ventas venta = new Ventas();
        Negocios negocio = new Negocios();
        negocio.setId(request.getNegocioId());
        venta.setNegocio(negocio);

        Sedes sede = new Sedes();
        sede.setId(request.getSedeId());
        venta.setSede(sede);

        venta.setSesionCaja(sesion);

        Usuarios usuarioVendedor = new Usuarios();
        usuarioVendedor.setId(request.getUsuarioId());
        venta.setUsuario(usuarioVendedor);
        venta.setVendedor(usuarioVendedor);

        if (request.getClienteId() != null) {
            Clientes cliente = new Clientes();
            cliente.setId(request.getClienteId());
            venta.setCliente(cliente);
        }

        venta.setNumeroVenta(numeroVenta);
        venta.setTipoVenta(Ventas.TipoVenta.pos);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setSubtotal(subtotal.add(descuentoGlobal));
        venta.setMontoDescuento(descuentoGlobal);
        venta.setRazonDescuento(request.getRazonDescuento());
        venta.setMontoImpuesto(montoImpuesto);
        venta.setTotal(total);
        venta.setEstado(Ventas.Estado.completada);
        venta.setCompletadoEn(LocalDateTime.now());

        if (request.getTipoComprobante() != null) {
            venta.setTipoComprobante(Ventas.TipoComprobante.valueOf(request.getTipoComprobante()));
        }
        venta.setDocClienteNumero(request.getDocClienteNumero());
        venta.setDocClienteNombre(request.getDocClienteNombre());

        Ventas ventaGuardada = ventasRepo.save(venta);

        // Crear detalle de venta y descontar stock
        for (ItemVentaPosDTO item : request.getItems()) {
            DetalleVentas detalle = new DetalleVentas();
            detalle.setVenta(ventaGuardada);

            Productos productoRef = new Productos();
            productoRef.setId(item.getProductoId());
            detalle.setProducto(productoRef);

            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            BigDecimal descItem = item.getDescuento() != null ? item.getDescuento() : BigDecimal.ZERO;
            detalle.setDescuento(descItem);
            BigDecimal subtotalLinea = item.getPrecioUnitario()
                    .multiply(item.getCantidad())
                    .subtract(descItem)
                    .setScale(2, RoundingMode.HALF_UP);
            detalle.setSubtotal(subtotalLinea);
            detalle.setTotal(subtotalLinea);

            detalleVentasRepo.save(detalle);

            // Descontar stock
            Optional<Productos> productoOpt = productosRepo.findById(item.getProductoId());
            if (productoOpt.isPresent()) {
                Productos producto = productoOpt.get();
                if (producto.getStock() != null) {
                    producto.setStock(producto.getStock() - item.getCantidad().intValue());
                    productosRepo.save(producto);
                }
            }
        }

        // Crear pagos
        BigDecimal efectivoTotal = BigDecimal.ZERO;
        for (PagoVentaPosDTO pagoDto : request.getPagos()) {
            PagosVenta pago = new PagosVenta();
            pago.setVenta(ventaGuardada);

            MetodosPago metodo = new MetodosPago();
            metodo.setId(pagoDto.getMetodoPagoId());
            pago.setMetodoPago(metodo);

            pago.setMonto(pagoDto.getMonto());
            pago.setNumeroReferencia(pagoDto.getNumeroReferencia());
            pago.setFechaPago(LocalDateTime.now());
            pagosRepo.save(pago);

            // Solo efectivo afecta el monto de caja
            if (pagoDto.getTipoReferencia() != null
                    && pagoDto.getTipoReferencia().equalsIgnoreCase("efectivo")) {
                efectivoTotal = efectivoTotal.add(pagoDto.getMonto());
            }
        }

        // Registrar movimiento en caja (solo efectivo)
        if (efectivoTotal.compareTo(BigDecimal.ZERO) > 0) {
            MovimientosCaja movVenta = new MovimientosCaja();
            movVenta.setSesionCaja(sesion);
            movVenta.setTipoMovimiento(MovimientosCaja.TipoMovimiento.ingreso_venta);
            // Solo registrar hasta el total en efectivo (no el excedente/vuelto)
            BigDecimal efectivoRegistrado = efectivoTotal.min(total);
            movVenta.setMonto(efectivoRegistrado);
            movVenta.setVenta(ventaGuardada);
            movVenta.setDescripcion("Venta POS " + numeroVenta);
            movVenta.setFechaMovimiento(LocalDateTime.now());
            movimientosRepo.save(movVenta);
        }

        return ventaGuardada;
    }

    @Override
    @Transactional
    public Ventas anularVenta(AnularVentaRequest request) {
        Ventas venta = ventasRepo.findById(request.getVentaId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getEstado() == Ventas.Estado.anulada) {
            throw new RuntimeException("La venta ya está anulada");
        }

        if (venta.getEstado() != Ventas.Estado.completada) {
            throw new RuntimeException("Solo se pueden anular ventas completadas");
        }

        // Verificar que la caja siga abierta
        if (venta.getSesionCaja() != null
                && venta.getSesionCaja().getEstadoSesion() != SesionesCaja.EstadoSesion.abierta) {
            throw new RuntimeException("No se puede anular: la caja ya fue cerrada");
        }

        // Anular venta
        venta.setEstado(Ventas.Estado.anulada);
        venta.setCanceladoEn(LocalDateTime.now());
        venta.setRazonCancelacion(request.getRazonCancelacion());

        Usuarios cancelador = new Usuarios();
        cancelador.setId(request.getUsuarioId());
        venta.setCanceladoPor(cancelador);

        // Revertir stock
        List<DetalleVentas> detalles = detalleVentasRepo.findByVentaId(venta.getId());
        for (DetalleVentas detalle : detalles) {
            if (detalle.getProducto() != null) {
                Optional<Productos> productoOpt = productosRepo.findById(detalle.getProducto().getId());
                if (productoOpt.isPresent()) {
                    Productos producto = productoOpt.get();
                    if (producto.getStock() != null) {
                        producto.setStock(producto.getStock() + detalle.getCantidad().intValue());
                        productosRepo.save(producto);
                    }
                }
            }
        }

        // Revertir movimiento de efectivo en caja
        if (venta.getSesionCaja() != null) {
            List<PagosVenta> pagos = pagosRepo.findByVentaId(venta.getId());
            BigDecimal efectivoARevertir = BigDecimal.ZERO;
            for (PagosVenta pago : pagos) {
                if (pago.getMetodoPago() != null && pago.getMetodoPago().getTipo() != null
                        && pago.getMetodoPago().getTipo() == MetodosPago.TipoMetodoPago.efectivo) {
                    efectivoARevertir = efectivoARevertir.add(pago.getMonto());
                }
            }

            if (efectivoARevertir.compareTo(BigDecimal.ZERO) > 0) {
                MovimientosCaja movAnulacion = new MovimientosCaja();
                movAnulacion.setSesionCaja(venta.getSesionCaja());
                movAnulacion.setTipoMovimiento(MovimientosCaja.TipoMovimiento.egreso_otro);
                movAnulacion.setMonto(efectivoARevertir);
                movAnulacion.setVenta(venta);
                movAnulacion.setDescripcion("Anulación de venta " + venta.getNumeroVenta());
                movAnulacion.setFechaMovimiento(LocalDateTime.now());
                movimientosRepo.save(movAnulacion);
            }
        }

        return ventasRepo.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Ventas obtenerVenta(Long ventaId, Long negocioId) {
        return ventasRepo.findByIdAndNegocioId(ventaId, negocioId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ventas> listarVentasPorNegocio(Long negocioId) {
        return ventasRepo.findByNegocioIdOrderByCreadoEnDesc(negocioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ventas> listarVentasPorSesion(Long sesionCajaId) {
        return ventasRepo.findBySesionCajaIdOrderByCreadoEnDesc(sesionCajaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleVentas> obtenerDetalleVenta(Long ventaId) {
        return detalleVentasRepo.findByVentaId(ventaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagosVenta> obtenerPagosVenta(Long ventaId) {
        return pagosRepo.findByVentaId(ventaId);
    }

    // ════════════════════════════════════════════════════════════
    //  MÉTODOS DE PAGO
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<MetodosPago> listarMetodosPagoPOS(Long negocioId) {
        return metodosPagoRepo.findByNegocioIdAndDisponiblePosTrue(negocioId);
    }

    // ════════════════════════════════════════════════════════════
    //  RESUMEN DE TURNO (para arqueo)
    // ════════════════════════════════════════════════════════════

    @Override
    public Map<String, Object> obtenerResumenTurno(Long sesionCajaId) {
        SesionesCaja sesion = sesionesRepo.findById(sesionCajaId)
                .orElseThrow(() -> new RuntimeException("Sesión de caja no encontrada"));

        List<Ventas> ventasTurno = ventasRepo
                .findBySesionCajaIdAndEstado(sesionCajaId, Ventas.Estado.completada);
        List<Ventas> ventasAnuladas = ventasRepo
                .findBySesionCajaIdAndEstado(sesionCajaId, Ventas.Estado.anulada);
        List<MovimientosCaja> movimientos = movimientosRepo
                .findBySesionCajaIdOrderByFechaMovimientoDesc(sesionCajaId);

        BigDecimal totalVentas = ventasTurno.stream()
                .map(Ventas::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalDigital = BigDecimal.ZERO;

        for (Ventas venta : ventasTurno) {
            List<PagosVenta> pagos = pagosRepo.findByVentaId(venta.getId());
            for (PagosVenta pago : pagos) {
                if (pago.getMetodoPago() != null && pago.getMetodoPago().getTipo() != null) {
                    switch (pago.getMetodoPago().getTipo()) {
                        case efectivo:
                            totalEfectivo = totalEfectivo.add(pago.getMonto());
                            break;
                        case tarjeta_credito:
                        case tarjeta_debito:
                            totalTarjeta = totalTarjeta.add(pago.getMonto());
                            break;
                        default:
                            totalDigital = totalDigital.add(pago.getMonto());
                            break;
                    }
                }
            }
        }

        BigDecimal ingresosExtra = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientosCaja.TipoMovimiento.ingreso_otro)
                .map(MovimientosCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresos = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == MovimientosCaja.TipoMovimiento.egreso_gasto
                        || m.getTipoMovimiento() == MovimientosCaja.TipoMovimiento.egreso_otro)
                .map(MovimientosCaja::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal efectivoEsperado = sesion.getMontoApertura()
                .add(totalEfectivo)
                .add(ingresosExtra)
                .subtract(egresos);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("sesionId", sesion.getId());
        resumen.put("cajaId", sesion.getCaja() != null ? sesion.getCaja().getId() : null);
        resumen.put("cajaNombre", sesion.getCaja() != null ? sesion.getCaja().getNombreCaja() : null);
        resumen.put("fechaApertura", sesion.getFechaApertura());
        resumen.put("montoApertura", sesion.getMontoApertura());
        resumen.put("estadoSesion", sesion.getEstadoSesion().name());
        resumen.put("cantidadVentas", ventasTurno.size());
        resumen.put("cantidadAnuladas", ventasAnuladas.size());
        resumen.put("totalVentas", totalVentas);
        resumen.put("totalEfectivo", totalEfectivo);
        resumen.put("totalTarjeta", totalTarjeta);
        resumen.put("totalDigital", totalDigital);
        resumen.put("ingresosExtra", ingresosExtra);
        resumen.put("egresos", egresos);
        resumen.put("efectivoEsperado", efectivoEsperado);
        resumen.put("movimientos", movimientos.size());

        return resumen;
    }
}
