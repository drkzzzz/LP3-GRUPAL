package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Suscripcion - Mapea la tabla 'suscripciones'.
 * Suscripciones activas de cada negocio (RF-PLT-006, RF-FAC-001..002).
 */
@Entity
@Table(name = "suscripciones")
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estado = EstadoSuscripcion.activa;

    @Column(name = "inicio_periodo_actual", nullable = false)
    private LocalDate inicioPeriodoActual;

    @Column(name = "fin_periodo_actual", nullable = false)
    private LocalDate finPeriodoActual;

    @Column(name = "proxima_fecha_facturacion")
    private LocalDate proximaFechaFacturacion;

    @Column(name = "precio_periodo_actual", precision = 10, scale = 2)
    private BigDecimal precioPeriodoActual;

    @Column(name = "cancelada_en")
    private LocalDateTime canceladaEn;

    @Column(name = "razon_cancelacion", length = 500)
    private String razonCancelacion;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public enum EstadoSuscripcion {
        activa, suspendida, cancelada, pendiente_pago
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

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public EstadoSuscripcion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSuscripcion estado) {
        this.estado = estado;
    }

    public LocalDate getInicioPeriodoActual() {
        return inicioPeriodoActual;
    }

    public void setInicioPeriodoActual(LocalDate inicioPeriodoActual) {
        this.inicioPeriodoActual = inicioPeriodoActual;
    }

    public LocalDate getFinPeriodoActual() {
        return finPeriodoActual;
    }

    public void setFinPeriodoActual(LocalDate finPeriodoActual) {
        this.finPeriodoActual = finPeriodoActual;
    }

    public LocalDate getProximaFechaFacturacion() {
        return proximaFechaFacturacion;
    }

    public void setProximaFechaFacturacion(LocalDate proximaFechaFacturacion) {
        this.proximaFechaFacturacion = proximaFechaFacturacion;
    }

    public BigDecimal getPrecioPeriodoActual() {
        return precioPeriodoActual;
    }

    public void setPrecioPeriodoActual(BigDecimal precioPeriodoActual) {
        this.precioPeriodoActual = precioPeriodoActual;
    }

    public LocalDateTime getCanceladaEn() {
        return canceladaEn;
    }

    public void setCanceladaEn(LocalDateTime canceladaEn) {
        this.canceladaEn = canceladaEn;
    }

    public String getRazonCancelacion() {
        return razonCancelacion;
    }

    public void setRazonCancelacion(String razonCancelacion) {
        this.razonCancelacion = razonCancelacion;
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
}
