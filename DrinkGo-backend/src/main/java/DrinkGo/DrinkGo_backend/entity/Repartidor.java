package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repartidores")
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", length = 20)
    private TipoVehiculo tipoVehiculo;

    @Column(name = "placa_vehiculo", length = 20)
    private String placaVehiculo;

    @Column(name = "numero_licencia", length = 30)
    private String numeroLicencia;

    @Column(name = "esta_disponible", nullable = false)
    private Boolean estaDisponible = true;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "latitud_actual", precision = 10, scale = 8)
    private BigDecimal latitudActual;

    @Column(name = "longitud_actual", precision = 11, scale = 8)
    private BigDecimal longitudActual;

    @Column(name = "ultima_ubicacion_en")
    private LocalDateTime ultimaUbicacionEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    public enum TipoVehiculo {
        motocicleta,
        auto,
        bicicleta,
        a_pie
    }

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // Getters y Setters

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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public Boolean getEstaDisponible() {
        return estaDisponible;
    }

    public void setEstaDisponible(Boolean estaDisponible) {
        this.estaDisponible = estaDisponible;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public BigDecimal getLatitudActual() {
        return latitudActual;
    }

    public void setLatitudActual(BigDecimal latitudActual) {
        this.latitudActual = latitudActual;
    }

    public BigDecimal getLongitudActual() {
        return longitudActual;
    }

    public void setLongitudActual(BigDecimal longitudActual) {
        this.longitudActual = longitudActual;
    }

    public LocalDateTime getUltimaUbicacionEn() {
        return ultimaUbicacionEn;
    }

    public void setUltimaUbicacionEn(LocalDateTime ultimaUbicacionEn) {
        this.ultimaUbicacionEn = ultimaUbicacionEn;
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
