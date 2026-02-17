package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad SesionCaja - Sesiones/turnos de caja (RF-VEN-001..002).
 * Gestiona apertura y cierre de cajas registradoras.
 * Compatible con MySQL (XAMPP) - Bloque 8.
 */
@Entity
@Table(name = "sesiones_caja")
@SQLDelete(sql = "UPDATE sesiones_caja SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class SesionCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "caja_id", nullable = false)
    private Long cajaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id", insertable = false, updatable = false)
    private CajaRegistradora caja;

    @Column(name = "usuario_apertura_id", nullable = false)
    private Long usuarioAperturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_apertura_id", insertable = false, updatable = false)
    private Usuario usuarioApertura;

    @Column(name = "usuario_cierre_id")
    private Long usuarioCierreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_cierre_id", insertable = false, updatable = false)
    private Usuario usuarioCierre;

    @Column(name = "monto_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoInicial = BigDecimal.ZERO;

    @Column(name = "monto_final", precision = 15, scale = 2)
    private BigDecimal montoFinal;

    @Column(name = "monto_esperado", precision = 15, scale = 2)
    private BigDecimal montoEsperado;

    @Column(name = "diferencia", precision = 15, scale = 2)
    private BigDecimal diferencia;

    @Column(name = "observaciones_apertura", columnDefinition = "TEXT")
    private String observacionesApertura;

    @Column(name = "observaciones_cierre", columnDefinition = "TEXT")
    private String observacionesCierre;

    @Column(name = "esta_abierta", nullable = false)
    private Boolean estaAbierta = true;

    @Column(name = "abierta_en", nullable = false)
    private LocalDateTime abiertaEn;

    @Column(name = "cerrada_en")
    private LocalDateTime cerradaEn;

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
        if (this.abiertaEn == null) {
            this.abiertaEn = LocalDateTime.now();
        }
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

    public Long getCajaId() {
        return cajaId;
    }

    public void setCajaId(Long cajaId) {
        this.cajaId = cajaId;
    }

    public CajaRegistradora getCaja() {
        return caja;
    }

    public void setCaja(CajaRegistradora caja) {
        this.caja = caja;
    }

    public Long getUsuarioAperturaId() {
        return usuarioAperturaId;
    }

    public void setUsuarioAperturaId(Long usuarioAperturaId) {
        this.usuarioAperturaId = usuarioAperturaId;
    }

    public Usuario getUsuarioApertura() {
        return usuarioApertura;
    }

    public void setUsuarioApertura(Usuario usuarioApertura) {
        this.usuarioApertura = usuarioApertura;
    }

    public Long getUsuarioCierreId() {
        return usuarioCierreId;
    }

    public void setUsuarioCierreId(Long usuarioCierreId) {
        this.usuarioCierreId = usuarioCierreId;
    }

    public Usuario getUsuarioCierre() {
        return usuarioCierre;
    }

    public void setUsuarioCierre(Usuario usuarioCierre) {
        this.usuarioCierre = usuarioCierre;
    }

    public BigDecimal getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(BigDecimal montoInicial) {
        this.montoInicial = montoInicial;
    }

    public BigDecimal getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(BigDecimal montoFinal) {
        this.montoFinal = montoFinal;
    }

    public BigDecimal getMontoEsperado() {
        return montoEsperado;
    }

    public void setMontoEsperado(BigDecimal montoEsperado) {
        this.montoEsperado = montoEsperado;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public String getObservacionesApertura() {
        return observacionesApertura;
    }

    public void setObservacionesApertura(String observacionesApertura) {
        this.observacionesApertura = observacionesApertura;
    }

    public String getObservacionesCierre() {
        return observacionesCierre;
    }

    public void setObservacionesCierre(String observacionesCierre) {
        this.observacionesCierre = observacionesCierre;
    }

    public Boolean getEstaAbierta() {
        return estaAbierta;
    }

    public void setEstaAbierta(Boolean estaAbierta) {
        this.estaAbierta = estaAbierta;
    }

    public LocalDateTime getAbiertaEn() {
        return abiertaEn;
    }

    public void setAbiertaEn(LocalDateTime abiertaEn) {
        this.abiertaEn = abiertaEn;
    }

    public LocalDateTime getCerradaEn() {
        return cerradaEn;
    }

    public void setCerradaEn(LocalDateTime cerradaEn) {
        this.cerradaEn = cerradaEn;
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
