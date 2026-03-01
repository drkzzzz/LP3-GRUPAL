package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metodos_pago")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@SQLDelete(sql = "UPDATE metodos_pago SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "nombre", "codigo", "tipo", "configuracionJson", "estaActivo",
        "disponiblePos", "disponibleTiendaOnline", "orden", "creadoEn", "actualizadoEn" })
public class MetodosPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    private TipoMetodoPago tipo;

    @Column(name = "configuracion_json", columnDefinition = "JSON")
    private String configuracionJson;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "disponible_pos")
    private Boolean disponiblePos = true;

    @Column(name = "disponible_tienda_online")
    private Boolean disponibleTiendaOnline = false;

    private Integer orden = 0;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum TipoMetodoPago {
        efectivo, tarjeta_credito, tarjeta_debito, transferencia_bancaria, billetera_digital, yape, plin, qr, otro
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoMetodoPago getTipo() {
        return tipo;
    }

    public void setTipo(TipoMetodoPago tipo) {
        this.tipo = tipo;
    }

    public String getConfiguracionJson() {
        return configuracionJson;
    }

    public void setConfiguracionJson(String configuracionJson) {
        this.configuracionJson = configuracionJson;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public Boolean getDisponiblePos() {
        return disponiblePos;
    }

    public void setDisponiblePos(Boolean disponiblePos) {
        this.disponiblePos = disponiblePos;
    }

    public Boolean getDisponibleTiendaOnline() {
        return disponibleTiendaOnline;
    }

    public void setDisponibleTiendaOnline(Boolean disponibleTiendaOnline) {
        this.disponibleTiendaOnline = disponibleTiendaOnline;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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
        return "MetodosPago [id=" + id + ", negocio=" + (negocio != null ? negocio.getId() : null) + ", nombre="
                + nombre + ", codigo=" + codigo + ", tipo=" + tipo + ", estaActivo=" + estaActivo + ", disponiblePos="
                + disponiblePos + ", disponibleTiendaOnline=" + disponibleTiendaOnline + ", orden=" + orden
                + ", creadoEn=" + creadoEn + ", actualizadoEn=" + actualizadoEn + "]";
    }
}
