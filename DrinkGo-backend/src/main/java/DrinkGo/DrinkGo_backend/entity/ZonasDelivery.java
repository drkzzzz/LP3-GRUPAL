package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "zonas_delivery")
@SQLDelete(sql = "UPDATE zonas_delivery SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "sedeId", "nombre", "descripcion", "tarifaDelivery", "montoMinimoPedido",
        "estaActivo", "creadoEn", "actualizadoEn" })
public class ZonasDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sedes sede;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "tarifa_delivery", precision = 10, scale = 2)
    private BigDecimal tarifaDelivery = BigDecimal.ZERO;

    @Column(name = "monto_minimo_pedido", precision = 10, scale = 2)
    private BigDecimal montoMinimoPedido;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getTarifaDelivery() {
        return tarifaDelivery;
    }

    public void setTarifaDelivery(BigDecimal tarifaDelivery) {
        this.tarifaDelivery = tarifaDelivery;
    }

    public BigDecimal getMontoMinimoPedido() {
        return montoMinimoPedido;
    }

    public void setMontoMinimoPedido(BigDecimal montoMinimoPedido) {
        this.montoMinimoPedido = montoMinimoPedido;
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

    @Override
    public String toString() {
        return "ZonasDelivery [id=" + id + ", negocio=" + (negocio != null ? negocio.getId() : null) + ", sede="
                + (sede != null ? sede.getId() : null) + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", tarifaDelivery=" + tarifaDelivery + ", montoMinimoPedido=" + montoMinimoPedido + ", estaActivo="
                + estaActivo + ", creadoEn=" + creadoEn + ", actualizadoEn=" + actualizadoEn + "]";
    }
}
