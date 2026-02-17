package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Combo - Packs promocionales de productos
 * BLOQUE 3: Catálogo de Productos
 */
@Entity
@Table(name = "combos")
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_combo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCombo;

    @Column(name = "precio_total_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotalOriginal;

    @Column(name = "porcentaje_descuento", precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "max_usos")
    private Integer maxUsos;

    @Column(name = "usos_actuales", nullable = false)
    private Integer usosActuales = 0;

    @Column(name = "valido_desde")
    private LocalDateTime validoDesde;

    @Column(name = "valido_hasta")
    private LocalDateTime validoHasta;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    // Relación con DetalleCombo
    @OneToMany(mappedBy = "comboId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCombo> detalles = new ArrayList<>();

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

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
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

    public BigDecimal getPrecioCombo() {
        return precioCombo;
    }

    public void setPrecioCombo(BigDecimal precioCombo) {
        this.precioCombo = precioCombo;
    }

    public BigDecimal getPrecioTotalOriginal() {
        return precioTotalOriginal;
    }

    public void setPrecioTotalOriginal(BigDecimal precioTotalOriginal) {
        this.precioTotalOriginal = precioTotalOriginal;
    }

    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public Integer getMaxUsos() {
        return maxUsos;
    }

    public void setMaxUsos(Integer maxUsos) {
        this.maxUsos = maxUsos;
    }

    public Integer getUsosActuales() {
        return usosActuales;
    }

    public void setUsosActuales(Integer usosActuales) {
        this.usosActuales = usosActuales;
    }

    public LocalDateTime getValidoDesde() {
        return validoDesde;
    }

    public void setValidoDesde(LocalDateTime validoDesde) {
        this.validoDesde = validoDesde;
    }

    public LocalDateTime getValidoHasta() {
        return validoHasta;
    }

    public void setValidoHasta(LocalDateTime validoHasta) {
        this.validoHasta = validoHasta;
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

    public List<DetalleCombo> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCombo> detalles) {
        this.detalles = detalles;
    }
}
