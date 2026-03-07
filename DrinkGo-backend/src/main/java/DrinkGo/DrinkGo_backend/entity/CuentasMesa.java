package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas_mesa", uniqueConstraints = {
        @UniqueConstraint(name = "uq_cuenta_numero_negocio", columnNames = { "numero_cuenta", "negocio_id" })
})
@JsonPropertyOrder({ "id", "negocioId", "numeroCuenta", "mesa", "meseroId", "clienteId",
        "numComensales", "estado", "subtotal", "total", "abiertoEn", "cerradoEn",
        "creadoEn", "actualizadoEn" })
public class CuentasMesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Negocio — write-only, expone negocioId para lectura */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(name = "negocio_id", insertable = false, updatable = false)
    private Long negocioId;

    @Column(name = "numero_cuenta", nullable = false, length = 20)
    private String numeroCuenta;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "horarioConfig",
            "calendarioEspecial", "configuracion" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesas mesa;

    /* Mesero — write-only, expone meseroId para lectura */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesero_id", nullable = false)
    private Usuarios mesero;

    @Column(name = "mesero_id", insertable = false, updatable = false)
    private Long meseroId;

    /* Cliente — opcional, write-only, expone clienteId para lectura */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Clientes cliente;

    @Column(name = "cliente_id", insertable = false, updatable = false)
    private Long clienteId;

    @Column(name = "num_comensales", nullable = false)
    private Integer numComensales = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuenta estado = EstadoCuenta.abierta;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "abierto_en")
    private LocalDateTime abiertoEn;

    @Column(name = "cerrado_en")
    private LocalDateTime cerradoEn;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum EstadoCuenta {
        abierta, cerrada, transferida
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (abiertoEn == null)
            abiertoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

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

    public Long getNegocioId() {
        return negocioId;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Mesas getMesa() {
        return mesa;
    }

    public void setMesa(Mesas mesa) {
        this.mesa = mesa;
    }

    public Usuarios getMesero() {
        return mesero;
    }

    public void setMesero(Usuarios mesero) {
        this.mesero = mesero;
    }

    public Long getMeseroId() {
        return meseroId;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Integer getNumComensales() {
        return numComensales;
    }

    public void setNumComensales(Integer numComensales) {
        this.numComensales = numComensales;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoCuenta estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getAbiertoEn() {
        return abiertoEn;
    }

    public void setAbiertoEn(LocalDateTime abiertoEn) {
        this.abiertoEn = abiertoEn;
    }

    public LocalDateTime getCerradoEn() {
        return cerradoEn;
    }

    public void setCerradoEn(LocalDateTime cerradoEn) {
        this.cerradoEn = cerradoEn;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }
}
