package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@SQLDelete(sql = "UPDATE movimientos_inventario SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "productoId", "almacenOrigenId", "almacenDestinoId", "loteId",
        "tipoMovimiento", "cantidad", "costoUnitario", "montoTotal", "motivoMovimiento", "referenciaDocumento",
        "usuarioId", "fechaMovimiento", "estaActivo", "creadoEn", "actualizadoEn", "eliminadoEn" })
public class MovimientosInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Productos producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_origen_id")
    private Almacenes almacenOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_destino_id")
    private Almacenes almacenDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LotesInventario lote;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad = BigDecimal.ZERO;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario = BigDecimal.ZERO;

    @Column(name = "monto_total", precision = 10, scale = 2)
    private BigDecimal montoTotal = BigDecimal.ZERO;

    @Column(name = "motivo_movimiento", columnDefinition = "TEXT")
    private String motivoMovimiento;

    @Column(name = "referencia_documento")
    private String referenciaDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum TipoMovimiento {
        entrada, salida, transferencia, ajuste_positivo, ajuste_negativo, devolucion, merma
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (fechaMovimiento == null) {
            fechaMovimiento = LocalDateTime.now();
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

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public Almacenes getAlmacenOrigen() {
        return almacenOrigen;
    }

    public void setAlmacenOrigen(Almacenes almacenOrigen) {
        this.almacenOrigen = almacenOrigen;
    }

    public Almacenes getAlmacenDestino() {
        return almacenDestino;
    }

    public void setAlmacenDestino(Almacenes almacenDestino) {
        this.almacenDestino = almacenDestino;
    }

    public LotesInventario getLote() {
        return lote;
    }

    public void setLote(LotesInventario lote) {
        this.lote = lote;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getMotivoMovimiento() {
        return motivoMovimiento;
    }

    public void setMotivoMovimiento(String motivoMovimiento) {
        this.motivoMovimiento = motivoMovimiento;
    }

    public String getReferenciaDocumento() {
        return referenciaDocumento;
    }

    public void setReferenciaDocumento(String referenciaDocumento) {
        this.referenciaDocumento = referenciaDocumento;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
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
        return "MovimientosInventario [id=" + id + ", tipoMovimiento=" + tipoMovimiento + ", cantidad=" + cantidad
                + ", fechaMovimiento=" + fechaMovimiento + ", estaActivo=" + estaActivo + "]";
    }
}
