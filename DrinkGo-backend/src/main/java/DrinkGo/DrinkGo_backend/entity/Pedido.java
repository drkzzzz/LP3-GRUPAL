package DrinkGo.DrinkGo_backend.entity;

import DrinkGo.DrinkGo_backend.enums.OrderModality;
import DrinkGo.DrinkGo_backend.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Pedido - Pedidos polimórficos (delivery, pickup, mesa, barra)
 * Tabla: drinkgo.pedido
 */
@Entity
@Table(name = "pedido", schema = "drinkgo",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "numero_pedido"}))
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "sede_id", nullable = false)
    private Long sedeId;
    
    @Column(name = "numero_pedido", nullable = false, length = 30)
    private String numeroPedido;
    
    // Cliente
    @Column(name = "cliente_id")
    private Long clienteId;
    
    @Column(name = "cliente_nombre", length = 150)
    private String clienteNombre;
    
    @Column(name = "cliente_telefono", length = 20)
    private String clienteTelefono;
    
    // Modalidad polimórfica
    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad", nullable = false)
    private OrderModality modalidad;
    
    // Campos para Delivery
    @Column(name = "direccion_entrega", length = 300)
    private String direccionEntrega;
    
    @Column(name = "direccion_referencia", length = 250)
    private String direccionReferencia;
    
    @Column(name = "distrito_entrega", length = 80)
    private String distritoEntrega;
    
    @Column(name = "coordenadas_lat", precision = 10, scale = 7)
    private BigDecimal coordenadasLat;
    
    @Column(name = "coordenadas_lng", precision = 10, scale = 7)
    private BigDecimal coordenadasLng;
    
    @Column(name = "costo_delivery", precision = 12, scale = 2)
    private BigDecimal costoDelivery = BigDecimal.ZERO;
    
    // Campos para Pickup
    @Column(name = "hora_recojo")
    private LocalTime horaRecojo;
    
    // Campos para Mesa
    @Column(name = "mesa_id")
    private Long mesaId;
    
    @Column(name = "cuenta_mesa_id")
    private Long cuentaMesaId;
    
    // Fechas
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
    
    @Column(name = "fecha_entrega_estimada")
    private OffsetDateTime fechaEntregaEstimada;
    
    @Column(name = "fecha_entrega_real")
    private OffsetDateTime fechaEntregaReal;
    
    // Estado
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private OrderStatus estado = OrderStatus.pendiente;
    
    // Origen
    @Column(name = "origen", nullable = false, length = 20)
    private String origen = "pos";
    
    // Pago online
    @Column(name = "pago_online_estado", length = 20)
    private String pagoOnlineEstado;
    
    @Column(name = "pago_online_referencia", length = 120)
    private String pagoOnlineReferencia;
    
    @Column(name = "pago_online_metodo", length = 50)
    private String pagoOnlineMetodo;
    
    // Montos
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(name = "descuento_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuentoTotal = BigDecimal.ZERO;
    
    @Column(name = "impuesto", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;
    
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    // Asignaciones
    @Column(name = "preparado_por_id")
    private Long preparadoPorId;
    
    @Column(name = "repartidor_id")
    private Long repartidorId;
    
    // Verificación de edad
    @Column(name = "requiere_verificacion_edad", nullable = false)
    private Boolean requiereVerificacionEdad = true;
    
    @Column(name = "verificacion_realizada", nullable = false)
    private Boolean verificacionRealizada = false;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "notas_preparacion", columnDefinition = "TEXT")
    private String notasPreparacion;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
    
    // Relaciones
    @OneToMany(mappedBy = "pedidoId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PedidoItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "pedidoId", fetch = FetchType.LAZY)
    private List<PagoPedido> pagos = new ArrayList<>();
    
    @OneToMany(mappedBy = "pedidoId", fetch = FetchType.LAZY)
    private List<SeguimientoPedido> seguimientos = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        creadoEn = OffsetDateTime.now();
        actualizadoEn = OffsetDateTime.now();
        fechaCreacion = OffsetDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    
    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
    
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
    
    public Long getCuentaMesaId() { return cuentaMesaId; }
    public void setCuentaMesaId(Long cuentaMesaId) { this.cuentaMesaId = cuentaMesaId; }
    
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
    
    public String getPagoOnlineEstado() { return pagoOnlineEstado; }
    public void setPagoOnlineEstado(String pagoOnlineEstado) { this.pagoOnlineEstado = pagoOnlineEstado; }
    
    public String getPagoOnlineReferencia() { return pagoOnlineReferencia; }
    public void setPagoOnlineReferencia(String pagoOnlineReferencia) { this.pagoOnlineReferencia = pagoOnlineReferencia; }
    
    public String getPagoOnlineMetodo() { return pagoOnlineMetodo; }
    public void setPagoOnlineMetodo(String pagoOnlineMetodo) { this.pagoOnlineMetodo = pagoOnlineMetodo; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDescuentoTotal() { return descuentoTotal; }
    public void setDescuentoTotal(BigDecimal descuentoTotal) { this.descuentoTotal = descuentoTotal; }
    
    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public Long getPreparadoPorId() { return preparadoPorId; }
    public void setPreparadoPorId(Long preparadoPorId) { this.preparadoPorId = preparadoPorId; }
    
    public Long getRepartidorId() { return repartidorId; }
    public void setRepartidorId(Long repartidorId) { this.repartidorId = repartidorId; }
    
    public Boolean getRequiereVerificacionEdad() { return requiereVerificacionEdad; }
    public void setRequiereVerificacionEdad(Boolean requiereVerificacionEdad) { this.requiereVerificacionEdad = requiereVerificacionEdad; }
    
    public Boolean getVerificacionRealizada() { return verificacionRealizada; }
    public void setVerificacionRealizada(Boolean verificacionRealizada) { this.verificacionRealizada = verificacionRealizada; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getNotasPreparacion() { return notasPreparacion; }
    public void setNotasPreparacion(String notasPreparacion) { this.notasPreparacion = notasPreparacion; }
    
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
    
    public List<PedidoItem> getItems() { return items; }
    public void setItems(List<PedidoItem> items) { this.items = items; }
    
    public List<PagoPedido> getPagos() { return pagos; }
    public List<SeguimientoPedido> getSeguimientos() { return seguimientos; }
    
    // Métodos de utilidad
    public boolean esDelivery() { return modalidad == OrderModality.delivery; }
    public boolean esPickup() { return modalidad == OrderModality.pickup; }
    public boolean esMesa() { return modalidad == OrderModality.mesa; }
    public boolean esBarra() { return modalidad == OrderModality.barra; }
}
