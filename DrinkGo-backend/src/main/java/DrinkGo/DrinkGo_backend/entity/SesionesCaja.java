package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_caja")
@SQLDelete(sql = "UPDATE sesiones_caja SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "cajaId", "usuarioId", "fechaApertura", "fechaCierre", "montoApertura", "montoCierre",
        "totalEfectivo", "totalTarjeta", "totalYape", "totalPlin", "totalOtros", "totalIngresos", "totalEgresos",
        "diferenciaEsperadoReal", "observaciones", "estadoSesion", "estaActivo", "creadoEn", "actualizadoEn",
        "eliminadoEn" })
public class SesionesCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", nullable = false)
    private CajasRegistradoras caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto_apertura", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoApertura = BigDecimal.ZERO;

    @Column(name = "monto_cierre", precision = 10, scale = 2)
    private BigDecimal montoCierre = BigDecimal.ZERO;

    @Column(name = "total_efectivo", precision = 10, scale = 2)
    private BigDecimal totalEfectivo = BigDecimal.ZERO;

    @Column(name = "total_tarjeta", precision = 10, scale = 2)
    private BigDecimal totalTarjeta = BigDecimal.ZERO;

    @Column(name = "total_yape", precision = 10, scale = 2)
    private BigDecimal totalYape = BigDecimal.ZERO;

    @Column(name = "total_plin", precision = 10, scale = 2)
    private BigDecimal totalPlin = BigDecimal.ZERO;

    @Column(name = "total_otros", precision = 10, scale = 2)
    private BigDecimal totalOtros = BigDecimal.ZERO;

    @Column(name = "total_ingresos", precision = 10, scale = 2)
    private BigDecimal totalIngresos = BigDecimal.ZERO;

    @Column(name = "total_egresos", precision = 10, scale = 2)
    private BigDecimal totalEgresos = BigDecimal.ZERO;

    @Column(name = "diferencia_esperado_real", precision = 10, scale = 2)
    private BigDecimal diferenciaEsperadoReal = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sesion", nullable = false)
    private EstadoSesion estadoSesion = EstadoSesion.abierta;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum EstadoSesion {
        abierta, cerrada, con_diferencia
    }

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
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

    public CajasRegistradoras getCaja() {
        return caja;
    }

    public void setCaja(CajasRegistradoras caja) {
        this.caja = caja;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getMontoApertura() {
        return montoApertura;
    }

    public void setMontoApertura(BigDecimal montoApertura) {
        this.montoApertura = montoApertura;
    }

    public BigDecimal getMontoCierre() {
        return montoCierre;
    }

    public void setMontoCierre(BigDecimal montoCierre) {
        this.montoCierre = montoCierre;
    }

    public BigDecimal getTotalEfectivo() {
        return totalEfectivo;
    }

    public void setTotalEfectivo(BigDecimal totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    public BigDecimal getTotalTarjeta() {
        return totalTarjeta;
    }

    public void setTotalTarjeta(BigDecimal totalTarjeta) {
        this.totalTarjeta = totalTarjeta;
    }

    public BigDecimal getTotalYape() {
        return totalYape;
    }

    public void setTotalYape(BigDecimal totalYape) {
        this.totalYape = totalYape;
    }

    public BigDecimal getTotalPlin() {
        return totalPlin;
    }

    public void setTotalPlin(BigDecimal totalPlin) {
        this.totalPlin = totalPlin;
    }

    public BigDecimal getTotalOtros() {
        return totalOtros;
    }

    public void setTotalOtros(BigDecimal totalOtros) {
        this.totalOtros = totalOtros;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getTotalEgresos() {
        return totalEgresos;
    }

    public void setTotalEgresos(BigDecimal totalEgresos) {
        this.totalEgresos = totalEgresos;
    }

    public BigDecimal getDiferenciaEsperadoReal() {
        return diferenciaEsperadoReal;
    }

    public void setDiferenciaEsperadoReal(BigDecimal diferenciaEsperadoReal) {
        this.diferenciaEsperadoReal = diferenciaEsperadoReal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public EstadoSesion getEstadoSesion() {
        return estadoSesion;
    }

    public void setEstadoSesion(EstadoSesion estadoSesion) {
        this.estadoSesion = estadoSesion;
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
        return "SesionesCaja [id=" + id + ", fechaApertura=" + fechaApertura + ", estadoSesion=" + estadoSesion
                + ", totalIngresos=" + totalIngresos + ", estaActivo=" + estaActivo + "]";
    }
}
