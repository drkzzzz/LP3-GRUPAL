package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastos")
@SQLDelete(sql = "UPDATE gastos SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "sedeId", "numeroGasto", "categoriaId", "proveedorId", "descripcion", "monto",
        "montoImpuesto", "total", "moneda", "fechaGasto", "metodoPago", "referenciaPago", "urlComprobante", "estado",
        "esRecurrente", "periodoRecurrencia", "aprobadoPor", "registradoPor", "notas", "estaActivo", "creadoEn",
        "actualizadoEn", "eliminadoEn" })
public class Gastos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    private Sedes sede;

    @Column(name = "numero_gasto", nullable = false, length = 30)
    private String numeroGasto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriasGasto categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedores proveedor;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal monto = BigDecimal.ZERO;

    @Column(name = "monto_impuesto", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 3, nullable = false)
    private String moneda = "PEN";

    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fechaGasto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago = MetodoPago.efectivo;

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    @Column(name = "url_comprobante", length = 500)
    private String urlComprobante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGasto estado = EstadoGasto.pendiente;

    @Column(name = "es_recurrente", nullable = false)
    private Boolean esRecurrente = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_recurrencia")
    private PeriodoRecurrencia periodoRecurrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Usuarios aprobadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por")
    private Usuarios registradoPor;

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

    public enum MetodoPago {
        efectivo, transferencia_bancaria, tarjeta_credito, cheque, otro
    }

    public enum EstadoGasto {
        pendiente, aprobado, pagado, rechazado, anulado
    }

    public enum PeriodoRecurrencia {
        semanal, quincenal, mensual, trimestral, anual
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

    public Sedes getSede() {
        return sede;
    }

    public void setSede(Sedes sede) {
        this.sede = sede;
    }

    public String getNumeroGasto() {
        return numeroGasto;
    }

    public void setNumeroGasto(String numeroGasto) {
        this.numeroGasto = numeroGasto;
    }

    public CategoriasGasto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriasGasto categoria) {
        this.categoria = categoria;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
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

    public Usuarios getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Usuarios aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public Usuarios getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Usuarios registradoPor) {
        this.registradoPor = registradoPor;
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
        return "Gastos [id=" + id + ", numeroGasto=" + numeroGasto + ", descripcion=" + descripcion + ", total=" + total
                + ", estado=" + estado + ", estaActivo=" + estaActivo + "]";
    }
}
