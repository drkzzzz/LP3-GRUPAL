package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@SQLDelete(sql = "UPDATE movimientos_inventario SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class MovimientoInventario {

    public enum TipoMovimiento {
        entrada_compra,
        salida_venta,
        entrada_devolucion,
        salida_devolucion,
        entrada_transferencia,
        salida_transferencia,
        ajuste_entrada,
        ajuste_salida,
        merma,
        rotura,
        vencimiento,
        stock_inicial,
        entrada_produccion,
        salida_produccion
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "almacen_id", nullable = false)
    private Long almacenId;

    @Column(name = "lote_id")
    private Long loteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "costo_unitario", precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(name = "tipo_referencia", length = 50)
    private String tipoReferencia;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "motivo", length = 300)
    private String motivo;

    @Column(name = "realizado_por")
    private Long realizadoPor;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

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

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public String getTipoReferencia() {
        return tipoReferencia;
    }

    public void setTipoReferencia(String tipoReferencia) {
        this.tipoReferencia = tipoReferencia;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getRealizadoPor() {
        return realizadoPor;
    }

    public void setRealizadoPor(Long realizadoPor) {
        this.realizadoPor = realizadoPor;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void setEliminadoEn(LocalDateTime eliminadoEn) {
        this.eliminadoEn = eliminadoEn;
    }
}
