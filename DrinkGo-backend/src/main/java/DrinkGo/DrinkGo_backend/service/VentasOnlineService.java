package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.entity.DetallePedidos;
import DrinkGo.DrinkGo_backend.entity.DetalleVentas;
import DrinkGo.DrinkGo_backend.entity.PagosPedido;
import DrinkGo.DrinkGo_backend.entity.PagosVenta;
import DrinkGo.DrinkGo_backend.entity.Pedidos;
import DrinkGo.DrinkGo_backend.entity.Ventas;
import DrinkGo.DrinkGo_backend.repository.DetallePedidosRepository;
import DrinkGo.DrinkGo_backend.repository.DetalleVentasRepository;
import DrinkGo.DrinkGo_backend.repository.PagosPedidoRepository;
import DrinkGo.DrinkGo_backend.repository.PagosVentaRepository;
import DrinkGo.DrinkGo_backend.repository.PedidosRepository;
import DrinkGo.DrinkGo_backend.repository.VentasRepository;
import DrinkGo.DrinkGo_backend.service.FacturacionService;

/**
 * Servicio que crea registros de Ventas a partir de pedidos entregados.
 *
 * Usa Propagation.REQUIRES_NEW para que cualquier fallo al crear la venta
 * no contamine la transacción principal del cambio de estado del pedido.
 */
@Service
public class VentasOnlineService {

    @Autowired
    private VentasRepository ventasRepo;

    @Autowired
    private PedidosRepository pedidosRepo;

    @Autowired
    private PagosPedidoRepository pagosPedidoRepo;

    @Autowired
    private PagosVentaRepository pagosVentaRepo;

    @Autowired
    private DetallePedidosRepository detallePedidosRepo;

    @Autowired
    private DetalleVentasRepository detalleVentasRepo;

    @Autowired
    private FacturacionService facturacionService;

    /**
     * Crea una Ventas desde un pedido con estado entregado.
     * Es idempotente: si el pedido ya está vinculado a una venta, devuelve null.
     * Corre en su propia transacción (REQUIRES_NEW) para garantizar que un fallo
     * aquí no revierta el cambio de estado del pedido.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Ventas crearVentaDesdePedido(Long pedidoId) {
        Pedidos pedido = pedidosRepo.findById(pedidoId).orElse(null);
        if (pedido == null) return null;
        if (pedido.getVenta() != null) return null; // ya vinculado

        Long sedeId = pedido.getSede() != null ? pedido.getSede().getId() : null;
        long count = sedeId != null ? ventasRepo.countBySedeId(sedeId) : ventasRepo.count();
        String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String numeroVenta = "VTA-" + fechaStr + "-" + String.format("%04d", count + 1);

        Ventas.TipoComprobante tipoComp = Ventas.TipoComprobante.boleta;
        if ("factura".equalsIgnoreCase(pedido.getTipoComprobante())) {
            tipoComp = Ventas.TipoComprobante.factura;
        }

        Ventas.TipoVenta tipoVenta = Ventas.TipoVenta.tienda_online;
        if (pedido.getOrigenPedido() != null) {
            switch (pedido.getOrigenPedido()) {
                case pos:      tipoVenta = Ventas.TipoVenta.pos;      break;
                case telefono: tipoVenta = Ventas.TipoVenta.telefono; break;
                default:       tipoVenta = Ventas.TipoVenta.tienda_online; break;
            }
        }

        Ventas venta = new Ventas();
        venta.setNegocio(pedido.getNegocio());
        venta.setSede(pedido.getSede());
        venta.setNumeroVenta(numeroVenta);
        venta.setTipoVenta(tipoVenta);
        venta.setCliente(pedido.getCliente());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setSubtotal(pedido.getSubtotal()     != null ? pedido.getSubtotal()     : BigDecimal.ZERO);
        venta.setMontoDescuento(pedido.getDescuento()   != null ? pedido.getDescuento()   : BigDecimal.ZERO);
        venta.setMontoImpuesto(pedido.getImpuestos()   != null ? pedido.getImpuestos()   : BigDecimal.ZERO);
        venta.setCostoEnvio(pedido.getCostoDelivery() != null ? pedido.getCostoDelivery() : BigDecimal.ZERO);
        venta.setTotal(pedido.getTotal()         != null ? pedido.getTotal()         : BigDecimal.ZERO);
        venta.setEstado(Ventas.Estado.completada);
        venta.setEstadoEntrega(Ventas.EstadoEntrega.entregado);
        venta.setCompletadoEn(LocalDateTime.now());
        venta.setTipoComprobante(tipoComp);
        venta.setUsuario(pedido.getUsuario()); // nullable — puede ser null en pedidos online

        ventasRepo.save(venta);
        System.out.println("✅ Venta " + numeroVenta + " generada desde pedido " + pedido.getNumeroPedido());

        // Copiar DetallePedidos → DetalleVentas para que el comprobante muestre los productos
        try {
            List<DetallePedidos> detallesPedido = detallePedidosRepo.findByPedido_Id(pedido.getId());
            for (DetallePedidos dp : detallesPedido) {
                DetalleVentas dv = new DetalleVentas();
                dv.setVenta(venta);
                dv.setProducto(dp.getProducto());
                dv.setCombo(dp.getCombo());
                String nombreSnap = dp.getProducto() != null ? dp.getProducto().getNombre()
                        : (dp.getCombo() != null ? dp.getCombo().getNombre() : null);
                dv.setNombreProductoSnapshot(nombreSnap);
                dv.setCantidad(dp.getCantidad() != null ? dp.getCantidad() : BigDecimal.ZERO);
                dv.setPrecioUnitario(dp.getPrecioUnitario() != null ? dp.getPrecioUnitario() : BigDecimal.ZERO);
                dv.setDescuento(dp.getDescuento() != null ? dp.getDescuento() : BigDecimal.ZERO);
                dv.setSubtotal(dp.getSubtotal() != null ? dp.getSubtotal() : BigDecimal.ZERO);
                dv.setImpuesto(dp.getImpuesto() != null ? dp.getImpuesto() : BigDecimal.ZERO);
                dv.setTotal(dp.getTotal() != null ? dp.getTotal() : BigDecimal.ZERO);
                detalleVentasRepo.save(dv);
            }
            System.out.println("📦 " + detallesPedido.size() + " ítem(s) copiados para venta " + numeroVenta);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo copiar detalles para venta " + numeroVenta + ": " + e.getMessage());
        }

        // Copiar PagosPedido → PagosVenta para que el comprobante muestre el método de pago
        try {
            List<PagosPedido> pagosPedido = pagosPedidoRepo.findByPedidoId(pedido.getId());
            for (PagosPedido pp : pagosPedido) {
                if (pp.getMetodoPago() == null) continue;
                PagosVenta pv = new PagosVenta();
                pv.setVenta(venta);
                pv.setMetodoPago(pp.getMetodoPago());
                pv.setMonto(pp.getMonto() != null ? pp.getMonto() : BigDecimal.ZERO);
                pv.setNumeroReferencia(pp.getNumeroReferencia());
                pv.setBanco(pp.getBanco());
                pv.setUltimosCuatroDigitos(pp.getUltimosCuatroDigitos());
                pv.setNombreTitular(pp.getNombreTitular());
                pv.setFechaPago(pp.getFechaPago() != null ? pp.getFechaPago() : LocalDateTime.now());
                pagosVentaRepo.save(pv);
            }
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo copiar pagos para venta " + numeroVenta + ": " + e.getMessage());
        }

        // Emitir comprobante de facturación (boleta/factura según lo elegido en el pedido)
        try {
            facturacionService.emitirComprobanteDesdeVenta(venta, null);
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo emitir comprobante para venta " + numeroVenta + ": " + e.getMessage());
        }

        return venta;
    }
}
