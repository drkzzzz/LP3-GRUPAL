package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "combos")
@SQLDelete(sql = "UPDATE combos SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "nombre", "slug", "descripcion", "urlImagen", "precioRegular",
        "precioCombo", "porcentajeDescuento", "fechaInicio", "fechaFin", "visiblePos", "visibleTiendaOnline",
        "esDestacado", "estaActivo", "creadoEn", "actualizadoEn", "eliminadoEn" })
public class Combos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false, length = 250)
    private String nombre;

    @Column(nullable = false, length = 250, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @Column(name = "precio_regular", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioRegular = BigDecimal.ZERO;

    @Column(name = "precio_combo", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioCombo = BigDecimal.ZERO;

    @Column(name = "porcentaje_descuento", precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "visible_pos")
    private Boolean visiblePos = true;

    @Column(name = "visible_tienda_online")
    private Boolean visibleTiendaOnline = false;

    @Column(name = "es_destacado")
    private Boolean esDestacado = false;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public BigDecimal getPrecioRegular() {
        return precioRegular;
    }

    public void setPrecioRegular(BigDecimal precioRegular) {
        this.precioRegular = precioRegular;
    }

    public BigDecimal getPrecioCombo() {
        return precioCombo;
    }

    public void setPrecioCombo(BigDecimal precioCombo) {
        this.precioCombo = precioCombo;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getVisiblePos() {
        return visiblePos;
    }

    public void setVisiblePos(Boolean visiblePos) {
        this.visiblePos = visiblePos;
    }

    public Boolean getVisibleTiendaOnline() {
        return visibleTiendaOnline;
    }

    public void setVisibleTiendaOnline(Boolean visibleTiendaOnline) {
        this.visibleTiendaOnline = visibleTiendaOnline;
    }

    public Boolean getEsDestacado() {
        return esDestacado;
    }

    public void setEsDestacado(Boolean esDestacado) {
        this.esDestacado = esDestacado;
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
        return "Combos [id=" + id + ", nombre=" + nombre + ", precioCombo=" + precioCombo + ", estaActivo="
                + estaActivo + "]";
    }
}
