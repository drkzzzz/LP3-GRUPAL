package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@SQLDelete(sql = "UPDATE productos SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "sku", "codigoBarras", "nombre", "slug", "descripcionCorta", "descripcion",
        "urlImagen", "categoriaId", "marcaId", "unidadMedidaId", "tipoProducto", "tipoBebida", "gradoAlcoholico",
        "volumenMl", "paisOrigen", "anioCosecha", "aniejamiento", "precioCompra", "precioVenta", "precioVentaMinimo",
        "precioMayorista", "tasaImpuesto", "impuestoIncluido", "stockMinimo", "stockMaximo", "puntoReorden",
        "tipoAlmacenamiento", "tempOptimaMin", "tempOptimaMax", "esPerecedero", "diasVidaUtil", "pesoKg", "altoCm",
        "anchoCm", "visiblePos", "visibleTiendaOnline", "esDestacado", "requiereVerificacionEdad", "permiteDescuento",
        "estaActivo", "creadoEn", "actualizadoEn", "eliminadoEn" })
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false)
    private String sku;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    @Column(nullable = false, length = 250)
    private String nombre;

    @Column(nullable = false, length = 250)
    private String slug;

    @Column(name = "descripcion_corta", length = 500)
    private String descripcionCorta;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_producto")
    private TipoProducto tipoProducto = TipoProducto.alcoholica;

    @Column(name = "tipo_bebida")
    private String tipoBebida;

    @Column(name = "grado_alcoholico", precision = 5, scale = 2)
    private BigDecimal gradoAlcoholico;

    @Column(name = "volumen_ml")
    private Integer volumenMl;

    @Column(name = "pais_origen")
    private String paisOrigen;

    @Column(name = "anio_cosecha")
    private Integer anioCosecha;

    private String aniejamiento;

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

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "punto_reorden")
    private Integer puntoReorden;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_almacenamiento")
    private TipoAlmacenamiento tipoAlmacenamiento = TipoAlmacenamiento.ambiente;

    @Column(name = "temp_optima_min", precision = 5, scale = 2)
    private BigDecimal tempOptimaMin;

    @Column(name = "temp_optima_max", precision = 5, scale = 2)
    private BigDecimal tempOptimaMax;

    @Column(name = "es_perecedero")
    private Boolean esPerecedero = false;

    @Column(name = "dias_vida_util")
    private Integer diasVidaUtil;

    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    @Column(name = "alto_cm", precision = 6, scale = 2)
    private BigDecimal altoCm;

    @Column(name = "ancho_cm", precision = 6, scale = 2)
    private BigDecimal anchoCm;

    @Column(name = "visible_pos")
    private Boolean visiblePos = true;

    @Column(name = "visible_tienda_online")
    private Boolean visibleTiendaOnline = false;

    @Column(name = "es_destacado")
    private Boolean esDestacado = false;

    @Column(name = "requiere_verificacion_edad")
    private Boolean requiereVerificacionEdad = false;

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

    public enum TipoProducto {
        alcoholica, no_alcoholica, comida, accesorio, combo, otro
    }

    public enum TipoAlmacenamiento {
        ambiente, frio, congelado
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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

    public String getDescripcionCorta() {
        return descripcionCorta;
    }

    public void setDescripcionCorta(String descripcionCorta) {
        this.descripcionCorta = descripcionCorta;
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

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public String getTipoBebida() {
        return tipoBebida;
    }

    public void setTipoBebida(String tipoBebida) {
        this.tipoBebida = tipoBebida;
    }

    public BigDecimal getGradoAlcoholico() {
        return gradoAlcoholico;
    }

    public void setGradoAlcoholico(BigDecimal gradoAlcoholico) {
        this.gradoAlcoholico = gradoAlcoholico;
    }

    public Integer getVolumenMl() {
        return volumenMl;
    }

    public void setVolumenMl(Integer volumenMl) {
        this.volumenMl = volumenMl;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public Integer getAnioCosecha() {
        return anioCosecha;
    }

    public void setAnioCosecha(Integer anioCosecha) {
        this.anioCosecha = anioCosecha;
    }

    public String getAniejamiento() {
        return aniejamiento;
    }

    public void setAniejamiento(String aniejamiento) {
        this.aniejamiento = aniejamiento;
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

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Integer getPuntoReorden() {
        return puntoReorden;
    }

    public void setPuntoReorden(Integer puntoReorden) {
        this.puntoReorden = puntoReorden;
    }

    public TipoAlmacenamiento getTipoAlmacenamiento() {
        return tipoAlmacenamiento;
    }

    public void setTipoAlmacenamiento(TipoAlmacenamiento tipoAlmacenamiento) {
        this.tipoAlmacenamiento = tipoAlmacenamiento;
    }

    public BigDecimal getTempOptimaMin() {
        return tempOptimaMin;
    }

    public void setTempOptimaMin(BigDecimal tempOptimaMin) {
        this.tempOptimaMin = tempOptimaMin;
    }

    public BigDecimal getTempOptimaMax() {
        return tempOptimaMax;
    }

    public void setTempOptimaMax(BigDecimal tempOptimaMax) {
        this.tempOptimaMax = tempOptimaMax;
    }

    public Boolean getEsPerecedero() {
        return esPerecedero;
    }

    public void setEsPerecedero(Boolean esPerecedero) {
        this.esPerecedero = esPerecedero;
    }

    public Integer getDiasVidaUtil() {
        return diasVidaUtil;
    }

    public void setDiasVidaUtil(Integer diasVidaUtil) {
        this.diasVidaUtil = diasVidaUtil;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getAltoCm() {
        return altoCm;
    }

    public void setAltoCm(BigDecimal altoCm) {
        this.altoCm = altoCm;
    }

    public BigDecimal getAnchoCm() {
        return anchoCm;
    }

    public void setAnchoCm(BigDecimal anchoCm) {
        this.anchoCm = anchoCm;
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

    public Boolean getRequiereVerificacionEdad() {
        return requiereVerificacionEdad;
    }

    public void setRequiereVerificacionEdad(Boolean requiereVerificacionEdad) {
        this.requiereVerificacionEdad = requiereVerificacionEdad;
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
        return "Productos [id=" + id + ", sku=" + sku + ", nombre=" + nombre + ", tipoProducto=" + tipoProducto
                + ", precioVenta=" + precioVenta + ", estaActivo=" + estaActivo + "]";
    }
}
