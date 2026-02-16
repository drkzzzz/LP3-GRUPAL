package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity para registro de gastos y egresos del negocio.
 * RF-FIN-013: Registrar Gastos y Egresos
 * RF-FIN-014: Categorizar Gastos
 * RF-FIN-015: Gestionar Comprobantes de Gasto
 */
@Entity
@Table(name = "gastos")
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "numero_gasto", nullable = false, length = 30)
    private String numeroGasto;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "monto", precision = 12, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "monto_impuesto", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "moneda", length = 3, nullable = false)
    private String moneda = "PEN";

    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fechaGasto;

    @Column(name = "metodo_pago", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago = MetodoPago.efectivo;

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    @Column(name = "url_comprobante", length = 500)
    private String urlComprobante;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoGasto estado = EstadoGasto.pendiente;

    @Column(name = "es_recurrente", nullable = false)
    private Boolean esRecurrente = false;

    @Column(name = "periodo_recurrencia")
    @Enumerated(EnumType.STRING)
    private PeriodoRecurrencia periodoRecurrencia;

    @Column(name = "aprobado_por")
    private Long aprobadoPor;

    @Column(name = "registrado_por", nullable = false)
    private Long registradoPor;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    // --- ENUMS ---

    public enum MetodoPago {
        efectivo,
        transferencia_bancaria,
        tarjeta_credito,
        cheque,
        otro
    }

    public enum EstadoGasto {
        pendiente,
        aprobado,
        pagado,
        rechazado,
        anulado
    }

    public enum PeriodoRecurrencia {
        semanal,
        quincenal,
        mensual,
        trimestral,
        anual
    }

    // --- Constructores ---

    public Gasto() {
    }

    // --- PrePersist & PreUpdate ---

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

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public String getNumeroGasto() {
        return numeroGasto;
    }

    public void setNumeroGasto(String numeroGasto) {
        this.numeroGasto = numeroGasto;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
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

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(LocalDate fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }

    public String getUrlComprobante() {
        return urlComprobante;
    }

    public void setUrlComprobante(String urlComprobante) {
        this.urlComprobante = urlComprobante;
    }

    public EstadoGasto getEstado() {
        return estado;
    }

    public void setEstado(EstadoGasto estado) {
        this.estado = estado;
    }

    public Boolean getEsRecurrente() {
        return esRecurrente;
    }

    public void setEsRecurrente(Boolean esRecurrente) {
        this.esRecurrente = esRecurrente;
    }

    public PeriodoRecurrencia getPeriodoRecurrencia() {
        return periodoRecurrencia;
    }

    public void setPeriodoRecurrencia(PeriodoRecurrencia periodoRecurrencia) {
        this.periodoRecurrencia = periodoRecurrencia;
    }

    public Long getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Long aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public Long getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Long registradoPor) {
        this.registradoPor = registradoPor;
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
}
