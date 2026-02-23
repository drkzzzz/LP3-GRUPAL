package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "suscripciones")
@JsonPropertyOrder({ "id", "negocioId", "planId", "estado", "inicioPeriodoActual", "finPeriodoActual",
        "proximaFechaFacturacion", "canceladoEn", "razonCancelacion", "suspendidoEn", "razonSuspension",
        "autoRenovar", "tokenMetodoPago", "creadoEn", "actualizadoEn" })
public class Suscripciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanesSuscripcion plan;

    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estado = EstadoSuscripcion.activa;

    @Column(name = "inicio_periodo_actual")
    private LocalDate inicioPeriodoActual;

    @Column(name = "fin_periodo_actual")
    private LocalDate finPeriodoActual;

    @Column(name = "proxima_fecha_facturacion")
    private LocalDate proximaFechaFacturacion;

    @Column(name = "cancelado_en")
    private LocalDateTime canceladoEn;

    @Column(name = "razon_cancelacion", columnDefinition = "TEXT")
    private String razonCancelacion;

    @Column(name = "suspendido_en")
    private LocalDateTime suspendidoEn;

    @Column(name = "razon_suspension", columnDefinition = "TEXT")
    private String razonSuspension;

    @Column(name = "auto_renovar")
    private Boolean autoRenovar = true;

    @Column(name = "token_metodo_pago")
    private String tokenMetodoPago;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum EstadoSuscripcion {
        activa, vencida, suspendida, cancelada, expirada
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
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

    public PlanesSuscripcion getPlan() {
        return plan;
    }

    public void setPlan(PlanesSuscripcion plan) {
        this.plan = plan;
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

    public LocalDateTime getCanceladoEn() {
        return canceladoEn;
    }

    public void setCanceladoEn(LocalDateTime canceladoEn) {
        this.canceladoEn = canceladoEn;
    }

    public String getRazonCancelacion() {
        return razonCancelacion;
    }

    public void setRazonCancelacion(String razonCancelacion) {
        this.razonCancelacion = razonCancelacion;
    }

    public LocalDateTime getSuspendidoEn() {
        return suspendidoEn;
    }

    public void setSuspendidoEn(LocalDateTime suspendidoEn) {
        this.suspendidoEn = suspendidoEn;
    }

    public String getRazonSuspension() {
        return razonSuspension;
    }

    public void setRazonSuspension(String razonSuspension) {
        this.razonSuspension = razonSuspension;
    }

    public Boolean getAutoRenovar() {
        return autoRenovar;
    }

    public void setAutoRenovar(Boolean autoRenovar) {
        this.autoRenovar = autoRenovar;
    }

    public String getTokenMetodoPago() {
        return tokenMetodoPago;
    }

    public void setTokenMetodoPago(String tokenMetodoPago) {
        this.tokenMetodoPago = tokenMetodoPago;
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

    @Override
    public String toString() {
        return "Suscripciones [id=" + id + ", negocio=" + (negocio != null ? negocio.getId() : null) + ", plan="
                + (plan != null ? plan.getId() : null) + ", estado=" + estado + ", inicioPeriodoActual="
                + inicioPeriodoActual + ", finPeriodoActual=" + finPeriodoActual + ", proximaFechaFacturacion="
                + proximaFechaFacturacion + ", canceladoEn=" + canceladoEn + ", razonCancelacion=" + razonCancelacion
                + ", suspendidoEn=" + suspendidoEn + ", razonSuspension=" + razonSuspension + ", autoRenovar="
                + autoRenovar + ", tokenMetodoPago=" + tokenMetodoPago + ", creadoEn=" + creadoEn + ", actualizadoEn="
                + actualizadoEn + "]";
    }
}
