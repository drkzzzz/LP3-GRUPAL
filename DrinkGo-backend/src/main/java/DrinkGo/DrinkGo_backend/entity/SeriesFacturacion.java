package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "series_facturacion")
@SQLDelete(sql = "UPDATE series_facturacion SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "sedeId", "tipoDocumento", "serie", "numeroActual", "esPredeterminada",
        "estaActivo", "creadoEn", "actualizadoEn", "eliminadoEn" })
public class SeriesFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sedes sede;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false)
    private String serie;

    @Column(name = "numero_actual", nullable = false)
    private Integer numeroActual = 1;

    @Column(name = "es_predeterminada")
    private Boolean esPredeterminada = false;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum TipoDocumento {
        boleta, factura, nota_credito, nota_debito
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

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getNumeroActual() {
        return numeroActual;
    }

    public void setNumeroActual(Integer numeroActual) {
        this.numeroActual = numeroActual;
    }

    public Boolean getEsPredeterminada() {
        return esPredeterminada;
    }

    public void setEsPredeterminada(Boolean esPredeterminada) {
        this.esPredeterminada = esPredeterminada;
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
        return "SeriesFacturacion [id=" + id + ", tipoDocumento=" + tipoDocumento + ", serie=" + serie
                + ", numeroActual=" + numeroActual + ", estaActivo=" + estaActivo + "]";
    }
}
