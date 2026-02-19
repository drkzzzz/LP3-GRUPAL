package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO para actualizar un producto (PUT /restful/productos/{id}).
 * Bloque 4 - Catálogo de Productos. Todos los campos son opcionales.
 */
public class ProductoUpdateRequest {

    private String sku;
    private String codigoBarras;
    private String nombre;
    private String descripcionCorta;
    private String descripcion;
    private String urlImagen;
    private Long categoriaId;
    private Long marcaId;
    private Long unidadMedidaId;
    
    // Atributos específicos de licorería
    private String tipoProducto;
    private String tipoBebida;
    private BigDecimal gradoAlcoholico;
    private Integer volumenMl;
    private String paisOrigen;
    private Integer anioCosecha;
    private String aniejamiento;
    
    // Precios
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal precioVentaMinimo;
    private BigDecimal precioMayorista;
    private BigDecimal tasaImpuesto;
    private Boolean impuestoIncluido;
    
    // Stock
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer puntoReorden;
    private String tipoAlmacenamiento;
    private BigDecimal tempOptimaMin;
    private BigDecimal tempOptimaMax;
    private Boolean esPerecible;
    private Integer diasVidaUtil;
    
    // Dimensiones
    private BigDecimal pesoKg;
    private BigDecimal altoCm;
    private BigDecimal anchoCm;
    private BigDecimal profundidadCm;
    
    // Flags
    private Boolean visiblePos;
    private Boolean visibleTiendaOnline;
    private Boolean esDestacado;
    private Boolean requiereVerificacionEdad;
    private Boolean permiteDescuento;
    
    // SEO
    private String metaTitulo;
    private String metaDescripcion;
    private String metaPalabrasClave;

    // --- Getters y Setters ---

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

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

    public String getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(String tipoProducto) { this.tipoProducto = tipoProducto; }

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

    public String getTipoAlmacenamiento() { return tipoAlmacenamiento; }
    public void setTipoAlmacenamiento(String tipoAlmacenamiento) { this.tipoAlmacenamiento = tipoAlmacenamiento; }

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

    public String getMetaTitulo() { return metaTitulo; }
    public void setMetaTitulo(String metaTitulo) { this.metaTitulo = metaTitulo; }

    public String getMetaDescripcion() { return metaDescripcion; }
    public void setMetaDescripcion(String metaDescripcion) { this.metaDescripcion = metaDescripcion; }

    public String getMetaPalabrasClave() { return metaPalabrasClave; }
    public void setMetaPalabrasClave(String metaPalabrasClave) { this.metaPalabrasClave = metaPalabrasClave; }
}
