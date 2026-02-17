package DrinkGo.DrinkGo_backend.dto;

import DrinkGo.DrinkGo_backend.enums.OrderModality;
import DrinkGo.DrinkGo_backend.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

public class PedidoDTO {
    
    private Long id;
    private String numeroPedido;
    private Long clienteId;
    private String clienteNombre;
    private String clienteTelefono;
    private OrderModality modalidad;
    
    // Delivery
    private String direccionEntrega;
    private String direccionReferencia;
    private String distritoEntrega;
    private BigDecimal coordenadasLat;
    private BigDecimal coordenadasLng;
    private BigDecimal costoDelivery;
    
    // Pickup
    private LocalTime horaRecojo;
    
    // Mesa
    private Long mesaId;
    private String mesaNombre;
    
    // Fechas
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaEntregaEstimada;
    private OffsetDateTime fechaEntregaReal;
    
    // Estado
    private OrderStatus estado;
    private String origen;
    
    // Montos
    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    
    // Verificación
    private Boolean requiereVerificacionEdad;
    private Boolean verificacionRealizada;
    
    private String observaciones;
    private String notasPreparacion;
    
    // Items y pagos
    private List<PedidoItemDTO> items;
    private List<PagoPedidoDTO> pagos;
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }
    
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    
    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }
    
    public OrderModality getModalidad() { return modalidad; }
    public void setModalidad(OrderModality modalidad) { this.modalidad = modalidad; }
    
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    
    public String getDireccionReferencia() { return direccionReferencia; }
    public void setDireccionReferencia(String direccionReferencia) { this.direccionReferencia = direccionReferencia; }
    
    public String getDistritoEntrega() { return distritoEntrega; }
    public void setDistritoEntrega(String distritoEntrega) { this.distritoEntrega = distritoEntrega; }
    
    public BigDecimal getCoordenadasLat() { return coordenadasLat; }
    public void setCoordenadasLat(BigDecimal coordenadasLat) { this.coordenadasLat = coordenadasLat; }
    
    public BigDecimal getCoordenadasLng() { return coordenadasLng; }
    public void setCoordenadasLng(BigDecimal coordenadasLng) { this.coordenadasLng = coordenadasLng; }
    
    public BigDecimal getCostoDelivery() { return costoDelivery; }
    public void setCostoDelivery(BigDecimal costoDelivery) { this.costoDelivery = costoDelivery; }
    
    public LocalTime getHoraRecojo() { return horaRecojo; }
    public void setHoraRecojo(LocalTime horaRecojo) { this.horaRecojo = horaRecojo; }
    
    public Long getMesaId() { return mesaId; }
    public void setMesaId(Long mesaId) { this.mesaId = mesaId; }
    
    public String getMesaNombre() { return mesaNombre; }
    public void setMesaNombre(String mesaNombre) { this.mesaNombre = mesaNombre; }
    
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public OffsetDateTime getFechaEntregaEstimada() { return fechaEntregaEstimada; }
    public void setFechaEntregaEstimada(OffsetDateTime fechaEntregaEstimada) { this.fechaEntregaEstimada = fechaEntregaEstimada; }
    
    public OffsetDateTime getFechaEntregaReal() { return fechaEntregaReal; }
    public void setFechaEntregaReal(OffsetDateTime fechaEntregaReal) { this.fechaEntregaReal = fechaEntregaReal; }
    
    public OrderStatus getEstado() { return estado; }
    public void setEstado(OrderStatus estado) { this.estado = estado; }
    
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDescuentoTotal() { return descuentoTotal; }
    public void setDescuentoTotal(BigDecimal descuentoTotal) { this.descuentoTotal = descuentoTotal; }
    
    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public Boolean getRequiereVerificacionEdad() { return requiereVerificacionEdad; }
    public void setRequiereVerificacionEdad(Boolean requiereVerificacionEdad) { this.requiereVerificacionEdad = requiereVerificacionEdad; }
    
    public Boolean getVerificacionRealizada() { return verificacionRealizada; }
    public void setVerificacionRealizada(Boolean verificacionRealizada) { this.verificacionRealizada = verificacionRealizada; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getNotasPreparacion() { return notasPreparacion; }
    public void setNotasPreparacion(String notasPreparacion) { this.notasPreparacion = notasPreparacion; }
    
    public List<PedidoItemDTO> getItems() { return items; }
    public void setItems(List<PedidoItemDTO> items) { this.items = items; }
    
    public List<PagoPedidoDTO> getPagos() { return pagos; }
    public void setPagos(List<PagoPedidoDTO> pagos) { this.pagos = pagos; }
}
