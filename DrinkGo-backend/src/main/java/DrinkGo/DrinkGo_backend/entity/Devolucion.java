package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Devolucion - Mapea la tabla 'devoluciones'.
 * Gestiona devoluciones y reembolsos (RF-FIN-008..012).
 */
@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "numero_devolucion", unique = true, nullable = false, length = 30)
    private String numeroDevolucion;

    @Column(name = "venta_id")
    private Long ventaId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "tipo_devolucion", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDevolucion tipoDevolucion = TipoDevolucion.parcial;

    @Column(name = "categoria_motivo", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoriaMotivo categoriaMotivo;

    @Column(name = "detalle_motivo", columnDefinition = "TEXT")
    private String detalleMotivo;

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "monto_impuesto", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "metodo_reembolso", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetodoReembolso metodoReembolso = MetodoReembolso.pago_original;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoDevolucion estado = EstadoDevolucion.solicitada;

    @Column(name = "solicitado_por")
    private Long solicitadoPor;

    @Column(name = "aprobado_por")
    private Long aprobadoPor;

    @Column(name = "procesado_por")
    private Long procesadoPor;

    @Column(name = "solicitado_en", insertable = false, updatable = false)
    private LocalDateTime solicitadoEn;

    @Column(name = "aprobado_en")
    private LocalDateTime aprobadoEn;

    @Column(name = "completado_en")
    private LocalDateTime completadoEn;

    @Column(name = "rechazado_en")
    private LocalDateTime rechazadoEn;

    @Column(name = "razon_rechazo", length = 500)
    private String razonRechazo;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDevolucion> detalles = new ArrayList<>();

    public enum EstadoDevolucion {
        solicitada, aprobada, procesando, completada, rechazada
    }

    public enum TipoDevolucion {
        total, parcial
    }

    public enum CategoriaMotivo {
        defectuoso, articulo_incorrecto, cambio_cliente, vencido, danado, otro
    }

    public enum MetodoReembolso {
        efectivo, pago_original, credito_tienda, transferencia_bancaria
    }

    // --- Métodos de ayuda para gestionar detalles ---

    public void addDetalle(DetalleDevolucion detalle) {
        detalles.add(detalle);
        detalle.setDevolucion(this);
    }

    public void removeDetalle(DetalleDevolucion detalle) {
        detalles.remove(detalle);
        detalle.setDevolucion(null);
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public String getNumeroDevolucion() {
        return numeroDevolucion;
    }

    public void setNumeroDevolucion(String numeroDevolucion) {
        this.numeroDevolucion = numeroDevolucion;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public TipoDevolucion getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(TipoDevolucion tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

    public CategoriaMotivo getCategoriaMotivo() {
        return categoriaMotivo;
    }

    public void setCategoriaMotivo(CategoriaMotivo categoriaMotivo) {
        this.categoriaMotivo = categoriaMotivo;
    }

    public String getDetalleMotivo() {
        return detalleMotivo;
    }

    public void setDetalleMotivo(String detalleMotivo) {
        this.detalleMotivo = detalleMotivo;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(BigDecimal montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public MetodoReembolso getMetodoReembolso() {
        return metodoReembolso;
    }

    public void setMetodoReembolso(MetodoReembolso metodoReembolso) {
        this.metodoReembolso = metodoReembolso;
    }

    public EstadoDevolucion getEstado() {
        return estado;
    }

    public void setEstado(EstadoDevolucion estado) {
        this.estado = estado;
    }

    public Long getSolicitadoPor() {
        return solicitadoPor;
    }

    public void setSolicitadoPor(Long solicitadoPor) {
        this.solicitadoPor = solicitadoPor;
    }

    public Long getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Long aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public Long getProcesadoPor() {
        return procesadoPor;
    }

    public void setProcesadoPor(Long procesadoPor) {
        this.procesadoPor = procesadoPor;
    }

    public LocalDateTime getSolicitadoEn() {
        return solicitadoEn;
    }

    public void setSolicitadoEn(LocalDateTime solicitadoEn) {
        this.solicitadoEn = solicitadoEn;
    }

    public LocalDateTime getAprobadoEn() {
        return aprobadoEn;
    }

    public void setAprobadoEn(LocalDateTime aprobadoEn) {
        this.aprobadoEn = aprobadoEn;
    }

    public LocalDateTime getCompletadoEn() {
        return completadoEn;
    }

    public void setCompletadoEn(LocalDateTime completadoEn) {
        this.completadoEn = completadoEn;
    }

    public LocalDateTime getRechazadoEn() {
        return rechazadoEn;
    }

    public void setRechazadoEn(LocalDateTime rechazadoEn) {
        this.rechazadoEn = rechazadoEn;
    }

    public String getRazonRechazo() {
        return razonRechazo;
    }

    public void setRazonRechazo(String razonRechazo) {
        this.razonRechazo = razonRechazo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public List<DetalleDevolucion> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleDevolucion> detalles) {
        this.detalles = detalles;
    }
}
