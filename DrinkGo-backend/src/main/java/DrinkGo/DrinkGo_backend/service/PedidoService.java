package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.PedidoDTO;
import DrinkGo.DrinkGo_backend.dto.PedidoItemDTO;
import DrinkGo.DrinkGo_backend.dto.PagoPedidoDTO;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.enums.OrderModality;
import DrinkGo.DrinkGo_backend.enums.OrderStatus;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PedidoItemRepository pedidoItemRepository;
    
    @Autowired
    private PagoPedidoRepository pagoPedidoRepository;
    
    @Autowired
    private SeguimientoPedidoRepository seguimientoPedidoRepository;
    
    @Autowired
    private ZonaDeliveryRepository zonaDeliveryRepository;
    
    // Crear pedido
    public PedidoDTO crearPedido(Long tenantId, Long sedeId, PedidoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setTenantId(tenantId);
        pedido.setSedeId(sedeId);
        pedido.setNumeroPedido(generarNumeroPedido(tenantId));
        
        mapearDatosBasicos(pedido, dto);
        validarModalidad(pedido);
        
        // Calcular costo de delivery si aplica
        if (pedido.getModalidad() == OrderModality.delivery && dto.getDistritoEntrega() != null) {
            calcularCostoDelivery(tenantId, sedeId, pedido, dto.getDistritoEntrega());
        }
        
        pedido = pedidoRepository.save(pedido);
        
        // Guardar items
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            guardarItems(pedido.getId(), dto.getItems());
            recalcularTotales(pedido);
            pedido = pedidoRepository.save(pedido);
        }
        
        // Registrar seguimiento inicial
        registrarSeguimiento(pedido.getId(), null, OrderStatus.pendiente, "Pedido creado", null);
        
        return convertirADTO(pedido);
    }
    
    // Obtener pedido por ID
    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedido(Long tenantId, Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return convertirADTO(pedido);
    }
    
    // Listar pedidos por sede
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPedidosPorSede(Long tenantId, Long sedeId) {
        return pedidoRepository.findByTenantIdAndSedeIdOrderByFechaCreacionDesc(tenantId, sedeId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // Listar pedidos por estado
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPedidosPorEstado(Long tenantId, OrderStatus estado) {
        return pedidoRepository.findByTenantIdAndEstadoOrderByFechaCreacionDesc(tenantId, estado)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // Obtener pedidos pendientes de preparación
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosPendientesPreparacion(Long tenantId, Long sedeId) {
        return pedidoRepository.findPedidosPendientesPreparacion(tenantId, sedeId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // Cambiar estado de pedido
    public PedidoDTO cambiarEstado(Long tenantId, Long pedidoId, OrderStatus nuevoEstado, String mensaje, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        OrderStatus estadoAnterior = pedido.getEstado();
        validarTransicionEstado(estadoAnterior, nuevoEstado);
        
        pedido.setEstado(nuevoEstado);
        
        if (nuevoEstado == OrderStatus.entregado) {
            pedido.setFechaEntregaReal(OffsetDateTime.now());
        }
        
        pedido = pedidoRepository.save(pedido);
        registrarSeguimiento(pedidoId, estadoAnterior, nuevoEstado, mensaje, usuarioId);
        
        return convertirADTO(pedido);
    }
    
    // Asignar repartidor
    public PedidoDTO asignarRepartidor(Long tenantId, Long pedidoId, Long repartidorId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .filter(p -> p.getModalidad() == OrderModality.delivery)
            .orElseThrow(() -> new RuntimeException("Pedido de delivery no encontrado"));
        
        pedido.setRepartidorId(repartidorId);
        pedido = pedidoRepository.save(pedido);
        
        registrarSeguimiento(pedidoId, pedido.getEstado(), pedido.getEstado(), 
            "Repartidor asignado", repartidorId);
        
        return convertirADTO(pedido);
    }
    
    // Registrar pago
    public PedidoDTO registrarPago(Long tenantId, Long pedidoId, PagoPedidoDTO pagoDTO) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        PagoPedido pago = new PagoPedido();
        pago.setPedidoId(pedidoId);
        pago.setMetodoPago(pagoDTO.getMetodoPago());
        pago.setMonto(pagoDTO.getMonto());
        pago.setReferencia(pagoDTO.getReferencia());
        pago.setGateway(pagoDTO.getGateway());
        pago.setGatewayId(pagoDTO.getGatewayId());
        
        pagoPedidoRepository.save(pago);
        
        return convertirADTO(pedido);
    }
    
    // Verificar edad del cliente
    public PedidoDTO verificarEdad(Long tenantId, Long pedidoId, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        pedido.setVerificacionRealizada(true);
        pedido = pedidoRepository.save(pedido);
        
        registrarSeguimiento(pedidoId, pedido.getEstado(), pedido.getEstado(), 
            "Verificación de edad realizada", usuarioId);
        
        return convertirADTO(pedido);
    }
    
    // Anular pedido
    public PedidoDTO anularPedido(Long tenantId, Long pedidoId, String motivo, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        if (pedido.getEstado() == OrderStatus.entregado) {
            throw new RuntimeException("No se puede anular un pedido entregado");
        }
        
        OrderStatus estadoAnterior = pedido.getEstado();
        pedido.setEstado(OrderStatus.anulado);
        pedido = pedidoRepository.save(pedido);
        
        registrarSeguimiento(pedidoId, estadoAnterior, OrderStatus.anulado, 
            "Pedido anulado: " + motivo, usuarioId);
        
        return convertirADTO(pedido);
    }
    
    // Obtener pedidos de delivery sin repartidor
    @Transactional(readOnly = true)
    public List<PedidoDTO> obtenerPedidosDeliverySinRepartidor(Long tenantId) {
        return pedidoRepository.findPedidosDeliverySinRepartidor(tenantId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // Listar todos los pedidos del negocio
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorNegocio(Long tenantId) {
        return pedidoRepository.findByTenantIdOrderByFechaCreacionDesc(tenantId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    // Actualizar pedido completo
    public PedidoDTO actualizarPedido(Long tenantId, Long id, PedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Solo actualizar si el pedido está en estado pendiente
        if (pedido.getEstado() != OrderStatus.pendiente) {
            throw new RuntimeException("Solo se pueden actualizar pedidos en estado pendiente");
        }
        
        // Actualizar datos básicos
        if (dto.getClienteNombre() != null) {
            pedido.setClienteNombre(dto.getClienteNombre());
        }
        if (dto.getClienteTelefono() != null) {
            pedido.setClienteTelefono(dto.getClienteTelefono());
        }
        if (dto.getDireccionEntrega() != null) {
            pedido.setDireccionEntrega(dto.getDireccionEntrega());
        }
        if (dto.getDireccionReferencia() != null) {
            pedido.setDireccionReferencia(dto.getDireccionReferencia());
        }
        if (dto.getObservaciones() != null) {
            pedido.setObservaciones(dto.getObservaciones());
        }
        if (dto.getNotasPreparacion() != null) {
            pedido.setNotasPreparacion(dto.getNotasPreparacion());
        }
        
        pedido = pedidoRepository.save(pedido);
        return convertirADTO(pedido);
    }
    
    // Eliminar pedido (soft delete)
    public void eliminar(Long tenantId, Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .filter(p -> p.getTenantId().equals(tenantId))
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Solo eliminar si el pedido está en estado pendiente o anulado
        if (pedido.getEstado() != OrderStatus.pendiente && pedido.getEstado() != OrderStatus.anulado) {
            throw new RuntimeException("Solo se pueden eliminar pedidos en estado pendiente o anulado");
        }
        
        pedido.setEstado(OrderStatus.anulado);
        pedidoRepository.save(pedido);
    }
    
    // Métodos privados
    private String generarNumeroPedido(Long tenantId) {
        String prefijo = "PED-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Integer siguiente = pedidoRepository.obtenerSiguienteNumeroPedido(tenantId, prefijo);
        return prefijo + "-" + String.format("%04d", siguiente);
    }
    
    private void mapearDatosBasicos(Pedido pedido, PedidoDTO dto) {
        pedido.setClienteId(dto.getClienteId());
        pedido.setClienteNombre(dto.getClienteNombre());
        pedido.setClienteTelefono(dto.getClienteTelefono());
        pedido.setModalidad(dto.getModalidad());
        pedido.setDireccionEntrega(dto.getDireccionEntrega());
        pedido.setDireccionReferencia(dto.getDireccionReferencia());
        pedido.setDistritoEntrega(dto.getDistritoEntrega());
        pedido.setCoordenadasLat(dto.getCoordenadasLat());
        pedido.setCoordenadasLng(dto.getCoordenadasLng());
        pedido.setHoraRecojo(dto.getHoraRecojo());
        pedido.setMesaId(dto.getMesaId());
        pedido.setEstado(OrderStatus.pendiente);
        pedido.setOrigen(dto.getOrigen() != null ? dto.getOrigen() : "pos");
        pedido.setObservaciones(dto.getObservaciones());
        pedido.setNotasPreparacion(dto.getNotasPreparacion());
        pedido.setRequiereVerificacionEdad(dto.getRequiereVerificacionEdad() != null ? dto.getRequiereVerificacionEdad() : true);
    }
    
    private void validarModalidad(Pedido pedido) {
        if (pedido.getModalidad() == OrderModality.delivery && pedido.getDireccionEntrega() == null) {
            throw new RuntimeException("La dirección de entrega es requerida para delivery");
        }
        if (pedido.getModalidad() == OrderModality.mesa && pedido.getMesaId() == null) {
            throw new RuntimeException("La mesa es requerida para modalidad mesa");
        }
    }
    
    private void calcularCostoDelivery(Long tenantId, Long sedeId, Pedido pedido, String distrito) {
        zonaDeliveryRepository.findByDistrito(tenantId, sedeId, distrito)
            .ifPresent(zona -> {
                pedido.setCostoDelivery(zona.getCostoDelivery());
                if (zona.getTiempoEstimadoMinutos() != null) {
                    pedido.setFechaEntregaEstimada(
                        OffsetDateTime.now().plusMinutes(zona.getTiempoEstimadoMinutos())
                    );
                }
            });
    }
    
    private void guardarItems(Long pedidoId, List<PedidoItemDTO> itemsDTO) {
        for (PedidoItemDTO itemDTO : itemsDTO) {
            PedidoItem item = new PedidoItem();
            item.setPedidoId(pedidoId);
            item.setProductoId(itemDTO.getProductoId());
            item.setComboId(itemDTO.getComboId());
            item.setCodigoProducto(itemDTO.getCodigoProducto());
            item.setNombreProducto(itemDTO.getNombreProducto());
            item.setCantidad(itemDTO.getCantidad());
            item.setPrecioUnitario(itemDTO.getPrecioUnitario());
            item.setDescuento(itemDTO.getDescuento() != null ? itemDTO.getDescuento() : BigDecimal.ZERO);
            item.calcularSubtotal();
            item.setPromocionId(itemDTO.getPromocionId());
            item.setNotas(itemDTO.getNotas());
            pedidoItemRepository.save(item);
        }
    }
    
    private void recalcularTotales(Pedido pedido) {
        List<PedidoItem> items = pedidoItemRepository.findByPedidoId(pedido.getId());
        
        BigDecimal subtotal = items.stream()
            .map(PedidoItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal descuentoTotal = items.stream()
            .map(PedidoItem::getDescuento)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        pedido.setSubtotal(subtotal);
        pedido.setDescuentoTotal(descuentoTotal);
        
        // IGV 18% en Perú
        BigDecimal impuesto = subtotal.multiply(new BigDecimal("0.18"));
        pedido.setImpuesto(impuesto);
        
        BigDecimal total = subtotal.add(impuesto).add(
            pedido.getCostoDelivery() != null ? pedido.getCostoDelivery() : BigDecimal.ZERO
        );
        pedido.setTotal(total);
    }
    
    private void validarTransicionEstado(OrderStatus actual, OrderStatus nuevo) {
        // Validar transiciones válidas
        if (actual == OrderStatus.anulado || actual == OrderStatus.entregado) {
            throw new RuntimeException("No se puede cambiar el estado de un pedido " + actual);
        }
    }
    
    private void registrarSeguimiento(Long pedidoId, OrderStatus anterior, OrderStatus nuevo, 
                                       String mensaje, Long usuarioId) {
        SeguimientoPedido seguimiento = new SeguimientoPedido();
        seguimiento.setPedidoId(pedidoId);
        seguimiento.setEstadoAnterior(anterior);
        seguimiento.setEstadoNuevo(nuevo);
        seguimiento.setMensaje(mensaje);
        seguimiento.setUsuarioId(usuarioId);
        seguimientoPedidoRepository.save(seguimiento);
    }
    
    private PedidoDTO convertirADTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setNumeroPedido(pedido.getNumeroPedido());
        dto.setClienteId(pedido.getClienteId());
        dto.setClienteNombre(pedido.getClienteNombre());
        dto.setClienteTelefono(pedido.getClienteTelefono());
        dto.setModalidad(pedido.getModalidad());
        dto.setDireccionEntrega(pedido.getDireccionEntrega());
        dto.setDireccionReferencia(pedido.getDireccionReferencia());
        dto.setDistritoEntrega(pedido.getDistritoEntrega());
        dto.setCoordenadasLat(pedido.getCoordenadasLat());
        dto.setCoordenadasLng(pedido.getCoordenadasLng());
        dto.setCostoDelivery(pedido.getCostoDelivery());
        dto.setHoraRecojo(pedido.getHoraRecojo());
        dto.setMesaId(pedido.getMesaId());
        dto.setFechaCreacion(pedido.getFechaCreacion());
        dto.setFechaEntregaEstimada(pedido.getFechaEntregaEstimada());
        dto.setFechaEntregaReal(pedido.getFechaEntregaReal());
        dto.setEstado(pedido.getEstado());
        dto.setOrigen(pedido.getOrigen());
        dto.setSubtotal(pedido.getSubtotal());
        dto.setDescuentoTotal(pedido.getDescuentoTotal());
        dto.setImpuesto(pedido.getImpuesto());
        dto.setTotal(pedido.getTotal());
        dto.setRequiereVerificacionEdad(pedido.getRequiereVerificacionEdad());
        dto.setVerificacionRealizada(pedido.getVerificacionRealizada());
        dto.setObservaciones(pedido.getObservaciones());
        dto.setNotasPreparacion(pedido.getNotasPreparacion());
        
        // Cargar items
        dto.setItems(pedidoItemRepository.findByPedidoId(pedido.getId())
            .stream()
            .map(this::convertirItemADTO)
            .collect(Collectors.toList()));
        
        // Cargar pagos
        dto.setPagos(pagoPedidoRepository.findByPedidoId(pedido.getId())
            .stream()
            .map(this::convertirPagoADTO)
            .collect(Collectors.toList()));
        
        return dto;
    }
    
    private PedidoItemDTO convertirItemADTO(PedidoItem item) {
        PedidoItemDTO dto = new PedidoItemDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProductoId());
        dto.setComboId(item.getComboId());
        dto.setCodigoProducto(item.getCodigoProducto());
        dto.setNombreProducto(item.getNombreProducto());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setDescuento(item.getDescuento());
        dto.setSubtotal(item.getSubtotal());
        dto.setPromocionId(item.getPromocionId());
        dto.setNotas(item.getNotas());
        return dto;
    }
    
    private PagoPedidoDTO convertirPagoADTO(PagoPedido pago) {
        PagoPedidoDTO dto = new PagoPedidoDTO();
        dto.setId(pago.getId());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setMonto(pago.getMonto());
        dto.setReferencia(pago.getReferencia());
        dto.setGateway(pago.getGateway());
        dto.setGatewayId(pago.getGatewayId());
        dto.setPagadoEn(pago.getPagadoEn());
        return dto;
    }
}
