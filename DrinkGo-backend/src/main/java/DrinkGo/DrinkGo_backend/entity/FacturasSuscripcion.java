package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "facturas_suscripcion")
@JsonPropertyOrder({ "id", "suscripcionId", "negocioId", "numeroFactura", "inicioPeriodo", "finPeriodo",
        "subtotal", "montoImpuesto", "montoDescuento", "total", "moneda", "estado", "metodoPago",
        "referenciaPago", "pagadoEn", "notas", "emitidoEn", "fechaVencimiento", "creadoEn", "actualizadoEn" })
public class FacturasSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suscripcion_id", nullable = false)
    private Suscripciones suscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(name = "numero_factura", unique = true, nullable = false)
    private String numeroFactura;

    @Column(name = "inicio_periodo")
    private LocalDate inicioPeriodo;

    @Column(name = "fin_periodo")
    private LocalDate finPeriodo;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "monto_impuesto", precision = 10, scale = 2)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    @Column(name = "monto_descuento", precision = 10, scale = 2)
    private BigDecimal montoDescuento = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    private String moneda = "PEN";

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado = EstadoFactura.pendiente;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "referencia_pago")
    private String referenciaPago;

    @Column(name = "pagado_en")
    private LocalDateTime pagadoEn;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "emitido_en")
    private LocalDateTime emitidoEn;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum EstadoFactura {
        borrador, pendiente, pagada, fallida, reembolsada, anulada
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (emitidoEn == null) {
            emitidoEn = LocalDateTime.now();
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

    public Suscripciones getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Suscripciones suscripcion) {
        this.suscripcion = suscripcion;
    }

    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDate getInicioPeriodo() {
        return inicioPeriodo;
    }

    public void setInicioPeriodo(LocalDate inicioPeriodo) {
        this.inicioPeriodo = inicioPeriodo;
    }

    public LocalDate getFinPeriodo() {
        return finPeriodo;
    }

    public void setFinPeriodo(LocalDate finPeriodo) {
        this.finPeriodo = finPeriodo;
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

    public BigDecimal getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(BigDecimal montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public LocalDateTime getPagadoEn() {
        return pagadoEn;
    }

    public void setPagadoEn(LocalDateTime pagadoEn) {
        this.pagadoEn = pagadoEn;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getEmitidoEn() {
        return emitidoEn;
    }

    public void setEmitidoEn(LocalDateTime emitidoEn) {
        this.emitidoEn = emitidoEn;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
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
        return "FacturasSuscripcion [id=" + id + ", suscripcion=" + (suscripcion != null ? suscripcion.getId() : null)
                + ", negocio=" + (negocio != null ? negocio.getId() : null) + ", numeroFactura=" + numeroFactura
                + ", inicioPeriodo=" + inicioPeriodo + ", finPeriodo=" + finPeriodo + ", subtotal=" + subtotal
                + ", montoImpuesto=" + montoImpuesto + ", montoDescuento=" + montoDescuento + ", total=" + total
                + ", moneda=" + moneda + ", estado=" + estado + ", metodoPago=" + metodoPago + ", referenciaPago="
                + referenciaPago + ", pagadoEn=" + pagadoEn + ", notas=" + notas + ", emitidoEn=" + emitidoEn
                + ", fechaVencimiento=" + fechaVencimiento + ", creadoEn=" + creadoEn + ", actualizadoEn="
                + actualizadoEn + "]";
    }
}
