package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de ventas (RF-VEN-003..008).
 * Bloque 8 - Ventas, POS y Cajas.
 * Maneja ventas completas con detalles, pagos, y actualización de stock.
 */
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final SedeRepository sedeRepository;
    private final SesionCajaRepository sesionRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final StockInventarioRepository stockRepository;
    private final LoteInventarioRepository loteRepository;

    public VentaService(VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            PagoVentaRepository pagoVentaRepository,
            SedeRepository sedeRepository,
            SesionCajaRepository sesionRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            ProductoRepository productoRepository,
            MetodoPagoRepository metodoPagoRepository,
            StockInventarioRepository stockRepository,
            LoteInventarioRepository loteRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.pagoVentaRepository = pagoVentaRepository;
        this.sedeRepository = sedeRepository;
        this.sesionRepository = sesionRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.stockRepository = stockRepository;
        this.loteRepository = loteRepository;
    }

    /** Listar ventas del negocio */
    public List<VentaDTO> listar(Long negocioId) {
        return ventaRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar ventas por sede */
    public List<VentaDTO> listarPorSede(Long negocioId, Long sedeId) {
        return ventaRepository.findByNegocioIdAndSedeId(negocioId, sedeId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar ventas por sesión */
    public List<VentaDTO> listarPorSesion(Long negocioId, Long sesionId) {
        return ventaRepository.findByNegocioIdAndSesionId(negocioId, sesionId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar ventas por estado */
    public List<VentaDTO> listarPorEstado(Long negocioId, String estado) {
        return ventaRepository.findByNegocioIdAndEstado(negocioId, estado).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Obtener venta por ID */
    public VentaDTO obtenerPorId(Long negocioId, Long id) {
        Venta venta = ventaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
        return convertirADTOCompleto(venta, negocioId);
    }

    /** Crear venta completa */
    @Transactional
    public VentaDTO crear(Long negocioId, Long usuarioId, VentaCreateRequest request) {
        // Validar sede
        Sede sede = sedeRepository.findByIdAndTenantId(request.getSedeId(), negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sede", request.getSedeId()));

        // Validar sesión si se proporciona
        if (request.getSesionId() != null) {
            SesionCaja sesion = sesionRepository.findByIdAndNegocioId(request.getSesionId(), negocioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Sesión de caja", request.getSesionId()));
            if (!sesion.getEstaAbierta()) {
                throw new OperacionInvalidaException("La sesión de caja no está abierta");
            }
        }

        // Validar cliente si se proporciona
        if (request.getClienteId() != null) {
            clienteRepository.findByIdAndNegocioId(request.getClienteId(), negocioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", request.getClienteId()));
        }

        // Validar que tenga items
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new OperacionInvalidaException("La venta debe tener al menos un item");
        }

        // Crear venta
        Venta venta = new Venta();
        venta.setNegocioId(negocioId);
        venta.setSedeId(request.getSedeId());
        venta.setCodigoVenta(generarCodigoVenta(negocioId));
        venta.setSesionId(request.getSesionId());
        venta.setClienteId(request.getClienteId());
        venta.setVendedorId(usuarioId);
        venta.setTipoVenta(request.getTipoVenta() != null ? request.getTipoVenta() : "LOCAL");
        venta.setCanalVenta(request.getCanalVenta() != null ? request.getCanalVenta() : "POS");
        venta.setMesaId(request.getMesaId());
        venta.setDescuentoMonto(request.getDescuentoMonto() != null ? request.getDescuentoMonto() : BigDecimal.ZERO);
        venta.setCostoDelivery(request.getCostoDelivery() != null ? request.getCostoDelivery() : BigDecimal.ZERO);
        venta.setPropina(request.getPropina() != null ? request.getPropina() : BigDecimal.ZERO);
        venta.setTipoComprobante(request.getTipoComprobante());
        venta.setObservaciones(request.getObservaciones());
        venta.setDireccionEntrega(request.getDireccionEntrega());
        venta.setTelefonoEntrega(request.getTelefonoEntrega());
        venta.setNombreClienteManual(request.getNombreClienteManual());
        venta.setEstado("PENDIENTE");

        // Calcular subtotal de items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (VentaCreateRequest.DetalleVentaItemRequest item : request.getDetalles()) {
            // Validar producto
            Producto producto = productoRepository.findByIdAndNegocioId(item.getProductoId(), negocioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", item.getProductoId()));

            BigDecimal descuentoItem = item.getDescuentoPorItem() != null ? item.getDescuentoPorItem()
                    : BigDecimal.ZERO;
            BigDecimal subtotalItem = item.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()))
                    .subtract(descuentoItem);
            subtotal = subtotal.add(subtotalItem);
        }

        venta.setSubtotal(subtotal);

        // Calcular total
        BigDecimal total = subtotal
                .subtract(venta.getDescuentoMonto())
                .add(venta.getCostoDelivery())
                .add(venta.getPropina());
        venta.setTotal(total);

        // Guardar venta
        ventaRepository.save(venta);

        // Crear detalles
        for (VentaCreateRequest.DetalleVentaItemRequest item : request.getDetalles()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setNegocioId(negocioId);
            detalle.setVentaId(venta.getId());
            detalle.setProductoId(item.getProductoId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setDescuentoPorItem(
                    item.getDescuentoPorItem() != null ? item.getDescuentoPorItem() : BigDecimal.ZERO);
            detalle.setImpuestoPorItem(BigDecimal.ZERO);

            BigDecimal subtotalItem = item.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(item.getCantidad()))
                    .subtract(detalle.getDescuentoPorItem());
            detalle.setSubtotalItem(subtotalItem);
            detalle.setObservaciones(item.getObservaciones());

            detalleVentaRepository.save(detalle);
            venta.getDetalles().add(detalle);

            // TODO: Actualizar stock usando FIFO (descontar del inventario)
            // Se implementaría aquí la lógica de descuento de lotes
        }

        // Crear pagos
        if (request.getPagos() != null && !request.getPagos().isEmpty()) {
            BigDecimal totalPagos = BigDecimal.ZERO;
            for (VentaCreateRequest.PagoVentaItemRequest pagoItem : request.getPagos()) {
                // Validar método de pago
                metodoPagoRepository.findByIdAndNegocioId(pagoItem.getMetodoPagoId(), negocioId)
                        .orElseThrow(
                                () -> new RecursoNoEncontradoException("Método de pago", pagoItem.getMetodoPagoId()));

                PagoVenta pago = new PagoVenta();
                pago.setNegocioId(negocioId);
                pago.setVentaId(venta.getId());
                pago.setMetodoPagoId(pagoItem.getMetodoPagoId());
                pago.setMonto(pagoItem.getMonto());
                pago.setMoneda("PEN");
                pago.setReferenciaPago(pagoItem.getReferenciaPago());
                pago.setEstado("COMPLETADO");

                pagoVentaRepository.save(pago);
                venta.getPagos().add(pago);
                totalPagos = totalPagos.add(pagoItem.getMonto());
            }

            // Validar que el total de pagos coincida con el total de la venta
            if (totalPagos.compareTo(venta.getTotal()) != 0) {
                throw new OperacionInvalidaException(
                        "El total de pagos (" + totalPagos + ") no coincide con el total de la venta ("
                                + venta.getTotal() + ")");
            }

            // Marcar como confirmada si está completamente pagada
            venta.setEstado("CONFIRMADA");
            ventaRepository.save(venta);
        }

        return convertirADTOCompleto(venta, negocioId);
    }

    /** Anular venta */
    @Transactional
    public VentaDTO anular(Long negocioId, Long id) {
        Venta venta = ventaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));

        if (venta.getEstado().equals("ANULADA")) {
            throw new OperacionInvalidaException("La venta ya está anulada");
        }

        venta.setEstado("ANULADA");
        ventaRepository.save(venta);

        // TODO: Restaurar stock si la venta estaba confirmada

        return convertirADTO(venta);
    }
    
    /** Actualizar venta completa */
    @Transactional
    public VentaDTO actualizar(Long negocioId, Long id, VentaCreateRequest request) {
        Venta venta = ventaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
        
        // Actualizar campos permitidos
        if (request.getClienteId() != null) {
            venta.setClienteId(request.getClienteId());
        }
        if (request.getObservaciones() != null) {
            venta.setObservaciones(request.getObservaciones());
        }
        if (request.getTipoComprobante() != null) {
            venta.setTipoComprobante(request.getTipoComprobante());
        }
        if (request.getDireccionEntrega() != null) {
            venta.setDireccionEntrega(request.getDireccionEntrega());
        }
        if (request.getTelefonoEntrega() != null) {
            venta.setTelefonoEntrega(request.getTelefonoEntrega());
        }
        
        ventaRepository.save(venta);
        return convertirADTO(venta);
    }
    
    /** Eliminar venta (soft delete) */
    @Transactional
    public void eliminar(Long negocioId, Long id) {
        Venta venta = ventaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
        
        if (!"PENDIENTE".equals(venta.getEstado())) {
            throw new OperacionInvalidaException("Solo se pueden eliminar ventas en estado PENDIENTE");
        }
        
        venta.setEstado("ELIMINADO");
        ventaRepository.save(venta);
    }

    // ============================================================
    // WRAPPERS PARA CONTROLADORES
    // ============================================================

    /** Listar ventas por cliente */
    public List<VentaDTO> listarPorCliente(Long negocioId, Long clienteId) {
        return ventaRepository.findByNegocioIdAndClienteId(negocioId, clienteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar ventas por vendedor */
    public List<VentaDTO> listarPorVendedor(Long negocioId, Long vendedorId) {
        return ventaRepository.findByNegocioIdAndVendedorId(negocioId, vendedorId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar ventas por tipo */
    public List<VentaDTO> listarPorTipo(Long negocioId, String tipo) {
        return ventaRepository.findByNegocioIdAndTipoVenta(negocioId, tipo).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Listar ventas por rango de fechas */
    public List<VentaDTO> listarPorRango(Long negocioId, java.time.LocalDateTime fechaInicio,
            java.time.LocalDateTime fechaFin) {
        return ventaRepository.findByNegocioId(negocioId).stream()
                .filter(v -> !v.getCreadoEn().isBefore(fechaInicio) && !v.getCreadoEn().isAfter(fechaFin))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /** Wrapper para obtenerPorId */
    public VentaDTO obtener(Long negocioId, Long id) {
        return obtenerPorId(negocioId, id);
    }

    /** Wrapper para anular con motivo */
    public VentaDTO anular(Long negocioId, Long id, String motivoAnulacion) {
        VentaDTO dto = anular(negocioId, id);
        // Agregar motivo a observaciones si se proporciona
        if (motivoAnulacion != null && !motivoAnulacion.isEmpty()) {
            Venta venta = ventaRepository.findByIdAndNegocioId(id, negocioId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Venta", id));
            venta.setObservaciones((venta.getObservaciones() != null ? venta.getObservaciones() + " | " : "")
                    + "Motivo anulación: " + motivoAnulacion);
            ventaRepository.save(venta);
        }
        return dto;
    }

    /** Calcular total de ventas de una sede en un rango */
    public BigDecimal calcularTotalVentasSede(Long negocioId, Long sedeId, java.time.LocalDateTime fechaInicio,
            java.time.LocalDateTime fechaFin) {
        BigDecimal total = ventaRepository.calcularTotalVentasSedePorFecha(negocioId, sedeId, fechaInicio, fechaFin);
        return total != null ? total : BigDecimal.ZERO;
    }

    /** Calcular total de ventas de una sesión */
    public BigDecimal calcularTotalVentasSesion(Long negocioId, Long sesionId) {
        BigDecimal total = ventaRepository.calcularTotalVentasSesion(negocioId, sesionId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /** Calcular total de compras de un cliente */
    public BigDecimal calcularTotalComprasCliente(Long negocioId, Long clienteId) {
        BigDecimal total = ventaRepository.calcularTotalComprasCliente(negocioId, clienteId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /** Generar código único de venta */
    private String generarCodigoVenta(Long negocioId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "VEN-" + negocioId + "-" + timestamp;
    }

    /** Convertir entidad a DTO (sin detalles) */
    private VentaDTO convertirADTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setNegocioId(venta.getNegocioId());
        dto.setSedeId(venta.getSedeId());
        if (venta.getSede() != null) {
            dto.setSedeNombre(venta.getSede().getNombre());
        }
        dto.setCodigoVenta(venta.getCodigoVenta());
        dto.setSesionId(venta.getSesionId());
        dto.setClienteId(venta.getClienteId());
        if (venta.getCliente() != null) {
            dto.setClienteNombre(venta.getCliente().getNombres() +
                    (venta.getCliente().getApellidos() != null ? " " + venta.getCliente().getApellidos() : ""));
        }
        dto.setVendedorId(venta.getVendedorId());
        if (venta.getVendedor() != null) {
            dto.setVendedorNombre(venta.getVendedor().getNombres() + " " + venta.getVendedor().getApellidos());
        }
        dto.setTipoVenta(venta.getTipoVenta());
        dto.setCanalVenta(venta.getCanalVenta());
        dto.setMesaId(venta.getMesaId());
        if (venta.getMesa() != null) {
            dto.setMesaNumero(venta.getMesa().getNumeroMesa());
        }
        dto.setPedidoId(venta.getPedidoId());
        dto.setSubtotal(venta.getSubtotal());
        dto.setDescuentoMonto(venta.getDescuentoMonto());
        dto.setImpuestoMonto(venta.getImpuestoMonto());
        dto.setCostoDelivery(venta.getCostoDelivery());
        dto.setPropina(venta.getPropina());
        dto.setTotal(venta.getTotal());
        dto.setEstado(venta.getEstado());
        dto.setTipoComprobante(venta.getTipoComprobante());
        dto.setNumeroComprobante(venta.getNumeroComprobante());
        dto.setObservaciones(venta.getObservaciones());
        dto.setDireccionEntrega(venta.getDireccionEntrega());
        dto.setTelefonoEntrega(venta.getTelefonoEntrega());
        dto.setNombreClienteManual(venta.getNombreClienteManual());
        dto.setCreadoEn(venta.getCreadoEn());
        dto.setActualizadoEn(venta.getActualizadoEn());
        return dto;
    }

    /** Convertir entidad a DTO completo (con detalles y pagos) */
    private VentaDTO convertirADTOCompleto(Venta venta, Long negocioId) {
        VentaDTO dto = convertirADTO(venta);

        // Cargar detalles
        List<DetalleVenta> detalles = detalleVentaRepository.findByNegocioIdAndVentaId(negocioId, venta.getId());
        dto.setDetalles(detalles.stream()
                .map(this::convertirDetalleADTO)
                .collect(Collectors.toList()));

        // Cargar pagos
        List<PagoVenta> pagos = pagoVentaRepository.findByNegocioIdAndVentaId(negocioId, venta.getId());
        dto.setPagos(pagos.stream()
                .map(this::convertirPagoADTO)
                .collect(Collectors.toList()));

        return dto;
    }

    /** Convertir detalle a DTO */
    private DetalleVentaDTO convertirDetalleADTO(DetalleVenta detalle) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setId(detalle.getId());
        dto.setProductoId(detalle.getProductoId());
        if (detalle.getProducto() != null) {
            dto.setProductoNombre(detalle.getProducto().getNombre());
        }
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setDescuentoPorItem(detalle.getDescuentoPorItem());
        dto.setImpuestoPorItem(detalle.getImpuestoPorItem());
        dto.setSubtotalItem(detalle.getSubtotalItem());
        dto.setObservaciones(detalle.getObservaciones());
        return dto;
    }

    /** Convertir pago a DTO */
    private PagoVentaDTO convertirPagoADTO(PagoVenta pago) {
        PagoVentaDTO dto = new PagoVentaDTO();
        dto.setId(pago.getId());
        dto.setMetodoPagoId(pago.getMetodoPagoId());
        if (pago.getMetodoPago() != null) {
            dto.setMetodoPagoNombre(pago.getMetodoPago().getNombre());
        }
        dto.setMonto(pago.getMonto());
        dto.setMoneda(pago.getMoneda());
        dto.setReferenciaPago(pago.getReferenciaPago());
        dto.setEstado(pago.getEstado());
        dto.setNotas(pago.getNotas());
        return dto;
    }
}
