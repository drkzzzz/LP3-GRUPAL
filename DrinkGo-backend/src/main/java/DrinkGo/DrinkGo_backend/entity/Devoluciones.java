package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones")
@SQLDelete(sql = "UPDATE devoluciones SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "sedeId", "numeroDevolucion", "ventaId", "pedidoId", "clienteId",
        "tipoDevolucion", "categoriaMotivo", "detalleMotivo", "subtotal", "montoImpuesto", "total",
        "metodoReembolso", "estado", "solicitadoPor", "aprobadoPor", "procesadoPor", "solicitadoEn", "aprobadoEn",
        "completadoEn", "rechazadoEn", "razonRechazo", "notas", "estaActivo", "creadoEn", "actualizadoEn",
        "eliminadoEn" })
public class Devoluciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sedes sede;

    @Column(name = "numero_devolucion", nullable = false, length = 30)
    private String numeroDevolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Ventas venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Clientes cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_devolucion", nullable = false)
    private TipoDevolucion tipoDevolucion = TipoDevolucion.parcial;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_motivo", nullable = false)
    private CategoriaMotivo categoriaMotivo;

    @Column(name = "detalle_motivo", columnDefinition = "TEXT")
    private String detalleMotivo;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "monto_impuesto", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_reembolso", nullable = false)
    private MetodoReembolso metodoReembolso = MetodoReembolso.pago_original;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDevolucion estado = EstadoDevolucion.solicitada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitado_por")
    private Usuarios solicitadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Usuarios aprobadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procesado_por")
    private Usuarios procesadoPor;

    @Column(name = "solicitado_en", nullable = false)
    private LocalDateTime solicitadoEn;

    @Column(name = "aprobado_en")
    private LocalDateTime aprobadoEn;

    @Column(name = "completado_en")
    private LocalDateTime completadoEn;

    @Column(name = "rechazado_en")
    private LocalDateTime rechazadoEn;

    @Column(name = "razon_rechazo", length = 500)
    private String razonRechazo;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum TipoDevolucion {
        total, parcial
    }

    public enum CategoriaMotivo {
        defectuoso, articulo_incorrecto, cambio_cliente, vencido, danado, otro
    }

    public enum MetodoReembolso {
        efectivo, pago_original, credito_tienda, transferencia_bancaria
    }

    public enum EstadoDevolucion {
        solicitada, aprobada, procesando, completada, rechazada
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (solicitadoEn == null) {
            solicitadoEn = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    public Sedes getSede() {
        return sede;
    }

    public void setSede(Sedes sede) {
        this.sede = sede;
    }

    public String getNumeroDevolucion() {
        return numeroDevolucion;
    }

    public void setNumeroDevolucion(String numeroDevolucion) {
        this.numeroDevolucion = numeroDevolucion;
    }

    public Ventas getVenta() {
        return venta;
    }

    public void setVenta(Ventas venta) {
        this.venta = venta;
    }

    public Pedidos getPedido() {
        return pedido;
    }

    public void setPedido(Pedidos pedido) {
        this.pedido = pedido;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
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

    public Usuarios getSolicitadoPor() {
        return solicitadoPor;
    }

    public void setSolicitadoPor(Usuarios solicitadoPor) {
        this.solicitadoPor = solicitadoPor;
    }

    public Usuarios getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Usuarios aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public Usuarios getProcesadoPor() {
        return procesadoPor;
    }

    public void setProcesadoPor(Usuarios procesadoPor) {
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

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
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

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void setEliminadoEn(LocalDateTime eliminadoEn) {
        this.eliminadoEn = eliminadoEn;
    }

    @Override
    public String toString() {
        return "Devoluciones [id=" + id + ", numeroDevolucion=" + numeroDevolucion + ", tipoDevolucion="
                + tipoDevolucion + ", total=" + total + ", estado=" + estado + ", estaActivo=" + estaActivo + "]";
    }
}
