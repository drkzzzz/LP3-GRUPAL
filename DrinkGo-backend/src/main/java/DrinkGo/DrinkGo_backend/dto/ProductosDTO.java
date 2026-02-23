package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductosDTO {

    private Long id;
    private Long negocioId;
    private String sku;
    private String codigoBarras;
    private String nombre;
    private String descripcion;
    private Long categoriaId;
    private Long marcaId;
    private Long unidadMedidaId;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal stockMinimo;
    private BigDecimal stockMaximo;
    private Boolean tieneIgv;
    private Boolean controlaStock;
    private Boolean permiteVentaSinStock;
    private String urlImagen;
    private Boolean visibleTiendaOnline;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private LocalDateTime eliminadoEn;

    // Constructores
    public ProductosDTO() {
    }

    public ProductosDTO(Long id, Long negocioId, String sku, String codigoBarras, String nombre, String descripcion,
            Long categoriaId, Long marcaId, Long unidadMedidaId, BigDecimal precioCompra, BigDecimal precioVenta,
            BigDecimal stockMinimo, BigDecimal stockMaximo, Boolean tieneIgv, Boolean controlaStock,
            Boolean permiteVentaSinStock, String urlImagen, Boolean visibleTiendaOnline, Boolean estaActivo,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn, LocalDateTime eliminadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.sku = sku;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.marcaId = marcaId;
        this.unidadMedidaId = unidadMedidaId;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.tieneIgv = tieneIgv;
        this.controlaStock = controlaStock;
        this.permiteVentaSinStock = permiteVentaSinStock;
        this.urlImagen = urlImagen;
        this.visibleTiendaOnline = visibleTiendaOnline;
        this.estaActivo = estaActivo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
        this.eliminadoEn = eliminadoEn;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Long marcaId) {
        this.marcaId = marcaId;
    }

    public Long getUnidadMedidaId() {
        return unidadMedidaId;
    }

    public void setUnidadMedidaId(Long unidadMedidaId) {
        this.unidadMedidaId = unidadMedidaId;
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

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(BigDecimal stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Boolean getTieneIgv() {
        return tieneIgv;
    }

    public void setTieneIgv(Boolean tieneIgv) {
        this.tieneIgv = tieneIgv;
    }

    public Boolean getControlaStock() {
        return controlaStock;
    }

    public void setControlaStock(Boolean controlaStock) {
        this.controlaStock = controlaStock;
    }

    public Boolean getPermiteVentaSinStock() {
        return permiteVentaSinStock;
    }

    public void setPermiteVentaSinStock(Boolean permiteVentaSinStock) {
        this.permiteVentaSinStock = permiteVentaSinStock;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Boolean getVisibleTiendaOnline() {
        return visibleTiendaOnline;
    }

    public void setVisibleTiendaOnline(Boolean visibleTiendaOnline) {
        this.visibleTiendaOnline = visibleTiendaOnline;
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
}
