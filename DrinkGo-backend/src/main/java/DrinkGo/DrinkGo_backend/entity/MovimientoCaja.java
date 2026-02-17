package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad MovimientoCaja - Movimientos de efectivo en caja.
 * Soporta ingresos (INGRESO), egresos (EGRESO) y ajustes (AJUSTE).
 * Compatible con MySQL (XAMPP) - Bloque 8.
 */
@Entity
@Table(name = "movimientos_caja")
@SQLDelete(sql = "UPDATE movimientos_caja SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sesion_id", nullable = false)
    private Long sesionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", insertable = false, updatable = false)
    private SesionCaja sesion;

    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento; // INGRESO, EGRESO, AJUSTE

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "concepto", nullable = false, length = 200)
    private String concepto;

    @Column(name = "realizado_por_usuario_id", nullable = false)
    private Long realizadoPorUsuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realizado_por_usuario_id", insertable = false, updatable = false)
    private Usuario realizadoPorUsuario;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
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

    public Long getSesionId() {
        return sesionId;
    }

    public void setSesionId(Long sesionId) {
        this.sesionId = sesionId;
    }

    public SesionCaja getSesion() {
        return sesion;
    }

    public void setSesion(SesionCaja sesion) {
        this.sesion = sesion;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Long getRealizadoPorUsuarioId() {
        return realizadoPorUsuarioId;
    }

    public void setRealizadoPorUsuarioId(Long realizadoPorUsuarioId) {
        this.realizadoPorUsuarioId = realizadoPorUsuarioId;
    }

    public Usuario getRealizadoPorUsuario() {
        return realizadoPorUsuario;
    }

    public void setRealizadoPorUsuario(Usuario realizadoPorUsuario) {
        this.realizadoPorUsuario = realizadoPorUsuario;
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
}
