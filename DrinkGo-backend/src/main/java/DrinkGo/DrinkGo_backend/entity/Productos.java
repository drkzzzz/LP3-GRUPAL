package DrinkGo.DrinkGo_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
@SQLDelete(sql = "UPDATE productos SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "sku", "nombre", "slug", "descripcion",
        "urlImagen", "categoriaId", "marcaId", "unidadMedidaId", "gradoAlcoholico",
        "precioCompra", "precioVenta", "precioVentaMinimo",
        "precioMayorista", "tasaImpuesto", "impuestoIncluido", "stock", "fechaVencimiento",
        "permiteDescuento", "estaActivo", "creadoEn", "actualizadoEn", "eliminadoEn" })
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false, length = 250)
    private String nombre;

    @Column(nullable = false, length = 250)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categorias categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id")
    private Marcas marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_id")
    private UnidadesMedida unidadMedida;

    @Column(name = "grado_alcoholico", precision = 5, scale = 2)
    private BigDecimal gradoAlcoholico;

    @Column(name = "precio_compra", precision = 10, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @Column(name = "precio_venta", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioVenta = BigDecimal.ZERO;

    @Column(name = "precio_venta_minimo", precision = 10, scale = 2)
    private BigDecimal precioVentaMinimo;

    @Column(name = "precio_mayorista", precision = 10, scale = 2)
    private BigDecimal precioMayorista;

    @Column(name = "tasa_impuesto", precision = 5, scale = 2)
    private BigDecimal tasaImpuesto = new BigDecimal("18.00");

    @Column(name = "impuesto_incluido")
    private Boolean impuestoIncluido = true;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "fecha_vencimiento")
    private java.time.LocalDate fechaVencimiento;

    @Column(name = "permite_descuento")
    private Boolean permiteDescuento = true;

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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public Categorias getCategoria() {
        return categoria;
    }

    public void setCategoria(Categorias categoria) {
        this.categoria = categoria;
    }

    public Marcas getMarca() {
        return marca;
    }

    public void setMarca(Marcas marca) {
        this.marca = marca;
    }

    public UnidadesMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadesMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getGradoAlcoholico() {
        return gradoAlcoholico;
    }

    public void setGradoAlcoholico(BigDecimal gradoAlcoholico) {
        this.gradoAlcoholico = gradoAlcoholico;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getPrecioVentaMinimo() {
        return precioVentaMinimo;
    }

    public void setPrecioVentaMinimo(BigDecimal precioVentaMinimo) {
        this.precioVentaMinimo = precioVentaMinimo;
    }

    public BigDecimal getPrecioMayorista() {
        return precioMayorista;
    }

    public void setPrecioMayorista(BigDecimal precioMayorista) {
        this.precioMayorista = precioMayorista;
    }

    public BigDecimal getTasaImpuesto() {
        return tasaImpuesto;
    }

    public void setTasaImpuesto(BigDecimal tasaImpuesto) {
        this.tasaImpuesto = tasaImpuesto;
    }

    public Boolean getImpuestoIncluido() {
        return impuestoIncluido;
    }

    public void setImpuestoIncluido(Boolean impuestoIncluido) {
        this.impuestoIncluido = impuestoIncluido;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public java.time.LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(java.time.LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getPermiteDescuento() {
        return permiteDescuento;
    }

    public void setPermiteDescuento(Boolean permiteDescuento) {
        this.permiteDescuento = permiteDescuento;
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
        return "Productos [id=" + id + ", sku=" + sku + ", nombre=" + nombre
                + ", precioVenta=" + precioVenta + ", estaActivo=" + estaActivo + "]";
    }
}
