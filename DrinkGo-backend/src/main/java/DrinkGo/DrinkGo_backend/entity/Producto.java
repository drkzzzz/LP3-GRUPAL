package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Producto - Bloque 4 (Catálogo de Productos).
 * Implementa RF-PRO-001 a RF-PRO-004.
 * Borrado lógico mediante eliminado_en.
 */
@Entity
@Table(name = "productos")
@SQLDelete(sql = "UPDATE productos SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @Column(name = "nombre", nullable = false, length = 250)
    private String nombre;

    @Column(name = "slug", nullable = false, length = 250)
    private String slug;

    @Column(name = "descripcion_corta", length = 500)
    private String descripcionCorta;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "marca_id")
    private Long marcaId;

    @Column(name = "unidad_medida_id")
    private Long unidadMedidaId;

    // ============================================================
    // Atributos específicos de licorería
    // ============================================================

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_producto", nullable = false)
    private TipoProducto tipoProducto = TipoProducto.alcoholica;

    @Column(name = "tipo_bebida", length = 100)
    private String tipoBebida;

    @Column(name = "grado_alcoholico", precision = 5, scale = 2)
    private BigDecimal gradoAlcoholico;

    @Column(name = "volumen_ml")
    private Integer volumenMl;

    @Column(name = "pais_origen", length = 100)
    private String paisOrigen;

    @Column(name = "anio_cosecha")
    private Integer anioCosecha;

    @Column(name = "aniejamiento", length = 100)
    private String aniejamiento;

    // ============================================================
    // Precios
    // ============================================================

    @Column(name = "precio_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta = BigDecimal.ZERO;

    @Column(name = "precio_venta_minimo", precision = 10, scale = 2)
    private BigDecimal precioVentaMinimo;

    @Column(name = "precio_mayorista", precision = 10, scale = 2)
    private BigDecimal precioMayorista;

    @Column(name = "tasa_impuesto", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaImpuesto = new BigDecimal("18.00");

    @Column(name = "impuesto_incluido", nullable = false)
    private Boolean impuestoIncluido = true;

    // ============================================================
    // Stock y almacenamiento
    // ============================================================

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "punto_reorden")
    private Integer puntoReorden;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_almacenamiento", nullable = false)
    private TipoAlmacenamiento tipoAlmacenamiento = TipoAlmacenamiento.ambiente;

    @Column(name = "temp_optima_min", precision = 5, scale = 2)
    private BigDecimal tempOptimaMin;

    @Column(name = "temp_optima_max", precision = 5, scale = 2)
    private BigDecimal tempOptimaMax;

    @Column(name = "es_perecible", nullable = false)
    private Boolean esPerecible = false;

    @Column(name = "dias_vida_util")
    private Integer diasVidaUtil;

    // ============================================================
    // Dimensiones y peso
    // ============================================================

    @Column(name = "peso_kg", precision = 8, scale = 3)
    private BigDecimal pesoKg;

    @Column(name = "alto_cm", precision = 6, scale = 2)
    private BigDecimal altoCm;

    @Column(name = "ancho_cm", precision = 6, scale = 2)
    private BigDecimal anchoCm;

    @Column(name = "profundidad_cm", precision = 6, scale = 2)
    private BigDecimal profundidadCm;

    // ============================================================
    // Flags de visibilidad
    // ============================================================

    @Column(name = "visible_pos", nullable = false)
    private Boolean visiblePos = true;

    @Column(name = "visible_tienda_online", nullable = false)
    private Boolean visibleTiendaOnline = false;

    @Column(name = "es_destacado", nullable = false)
    private Boolean esDestacado = false;

    @Column(name = "requiere_verificacion_edad", nullable = false)
    private Boolean requiereVerificacionEdad = false;

    @Column(name = "permite_descuento", nullable = false)
    private Boolean permiteDescuento = true;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    // ============================================================
    // SEO para tienda online
    // ============================================================

    @Column(name = "meta_titulo", length = 200)
    private String metaTitulo;

    @Column(name = "meta_descripcion", length = 500)
    private String metaDescripcion;

    @Column(name = "meta_palabras_clave", length = 300)
    private String metaPalabrasClave;

    // ============================================================
    // Timestamps
    // ============================================================

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (slug == null || slug.isBlank()) {
            slug = generarSlug(nombre);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    private String generarSlug(String texto) {
        if (texto == null || texto.isBlank()) return "";
        return texto.toLowerCase()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("ñ", "n")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    // ============================================================
    // ENUMS
    // ============================================================

    public enum TipoProducto {
        alcoholica, no_alcoholica, comida, accesorio, combo, otro
    }

    public enum TipoAlmacenamiento {
        ambiente, frio, congelado
    }

    // ============================================================
    // GETTERS Y SETTERS
    // ============================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescripcionCorta() { return descripcionCorta; }
    public void setDescripcionCorta(String descripcionCorta) { this.descripcionCorta = descripcionCorta; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public Long getMarcaId() { return marcaId; }
    public void setMarcaId(Long marcaId) { this.marcaId = marcaId; }

    public Long getUnidadMedidaId() { return unidadMedidaId; }
    public void setUnidadMedidaId(Long unidadMedidaId) { this.unidadMedidaId = unidadMedidaId; }

    public TipoProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(TipoProducto tipoProducto) { this.tipoProducto = tipoProducto; }

    public String getTipoBebida() { return tipoBebida; }
    public void setTipoBebida(String tipoBebida) { this.tipoBebida = tipoBebida; }

    public BigDecimal getGradoAlcoholico() { return gradoAlcoholico; }
    public void setGradoAlcoholico(BigDecimal gradoAlcoholico) { this.gradoAlcoholico = gradoAlcoholico; }

    public Integer getVolumenMl() { return volumenMl; }
    public void setVolumenMl(Integer volumenMl) { this.volumenMl = volumenMl; }

    public String getPaisOrigen() { return paisOrigen; }
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    public Integer getAnioCosecha() { return anioCosecha; }
    public void setAnioCosecha(Integer anioCosecha) { this.anioCosecha = anioCosecha; }

    public String getAniejamiento() { return aniejamiento; }
    public void setAniejamiento(String aniejamiento) { this.aniejamiento = aniejamiento; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public BigDecimal getPrecioVentaMinimo() { return precioVentaMinimo; }
    public void setPrecioVentaMinimo(BigDecimal precioVentaMinimo) { this.precioVentaMinimo = precioVentaMinimo; }

    public BigDecimal getPrecioMayorista() { return precioMayorista; }
    public void setPrecioMayorista(BigDecimal precioMayorista) { this.precioMayorista = precioMayorista; }

    public BigDecimal getTasaImpuesto() { return tasaImpuesto; }
    public void setTasaImpuesto(BigDecimal tasaImpuesto) { this.tasaImpuesto = tasaImpuesto; }

    public Boolean getImpuestoIncluido() { return impuestoIncluido; }
    public void setImpuestoIncluido(Boolean impuestoIncluido) { this.impuestoIncluido = impuestoIncluido; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }

    public Integer getPuntoReorden() { return puntoReorden; }
    public void setPuntoReorden(Integer puntoReorden) { this.puntoReorden = puntoReorden; }

    public TipoAlmacenamiento getTipoAlmacenamiento() { return tipoAlmacenamiento; }
    public void setTipoAlmacenamiento(TipoAlmacenamiento tipoAlmacenamiento) { this.tipoAlmacenamiento = tipoAlmacenamiento; }

    public BigDecimal getTempOptimaMin() { return tempOptimaMin; }
    public void setTempOptimaMin(BigDecimal tempOptimaMin) { this.tempOptimaMin = tempOptimaMin; }

    public BigDecimal getTempOptimaMax() { return tempOptimaMax; }
    public void setTempOptimaMax(BigDecimal tempOptimaMax) { this.tempOptimaMax = tempOptimaMax; }

    public Boolean getEsPerecible() { return esPerecible; }
    public void setEsPerecible(Boolean esPerecible) { this.esPerecible = esPerecible; }

    public Integer getDiasVidaUtil() { return diasVidaUtil; }
    public void setDiasVidaUtil(Integer diasVidaUtil) { this.diasVidaUtil = diasVidaUtil; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public BigDecimal getAltoCm() { return altoCm; }
    public void setAltoCm(BigDecimal altoCm) { this.altoCm = altoCm; }

    public BigDecimal getAnchoCm() { return anchoCm; }
    public void setAnchoCm(BigDecimal anchoCm) { this.anchoCm = anchoCm; }

    public BigDecimal getProfundidadCm() { return profundidadCm; }
    public void setProfundidadCm(BigDecimal profundidadCm) { this.profundidadCm = profundidadCm; }

    public Boolean getVisiblePos() { return visiblePos; }
    public void setVisiblePos(Boolean visiblePos) { this.visiblePos = visiblePos; }

    public Boolean getVisibleTiendaOnline() { return visibleTiendaOnline; }
    public void setVisibleTiendaOnline(Boolean visibleTiendaOnline) { this.visibleTiendaOnline = visibleTiendaOnline; }

    public Boolean getEsDestacado() { return esDestacado; }
    public void setEsDestacado(Boolean esDestacado) { this.esDestacado = esDestacado; }

    public Boolean getRequiereVerificacionEdad() { return requiereVerificacionEdad; }
    public void setRequiereVerificacionEdad(Boolean requiereVerificacionEdad) { this.requiereVerificacionEdad = requiereVerificacionEdad; }

    public Boolean getPermiteDescuento() { return permiteDescuento; }
    public void setPermiteDescuento(Boolean permiteDescuento) { this.permiteDescuento = permiteDescuento; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public String getMetaTitulo() { return metaTitulo; }
    public void setMetaTitulo(String metaTitulo) { this.metaTitulo = metaTitulo; }

    public String getMetaDescripcion() { return metaDescripcion; }
    public void setMetaDescripcion(String metaDescripcion) { this.metaDescripcion = metaDescripcion; }

    public String getMetaPalabrasClave() { return metaPalabrasClave; }
    public void setMetaPalabrasClave(String metaPalabrasClave) { this.metaPalabrasClave = metaPalabrasClave; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}
