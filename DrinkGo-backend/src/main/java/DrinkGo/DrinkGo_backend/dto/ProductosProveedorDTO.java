package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductosProveedorDTO {

    private Long id;
    private Long proveedorId;
    private Long productoId;
    private String codigoProveedor;
    private BigDecimal precioCompra;
    private Integer tiempoEntregaDias;
    private Boolean esPredeterminado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public ProductosProveedorDTO() {
    }

    public ProductosProveedorDTO(Long id, Long proveedorId, Long productoId, String codigoProveedor,
            BigDecimal precioCompra, Integer tiempoEntregaDias, Boolean esPredeterminado, LocalDateTime creadoEn,
            LocalDateTime actualizadoEn) {
        this.id = id;
        this.proveedorId = proveedorId;
        this.productoId = productoId;
        this.codigoProveedor = codigoProveedor;
        this.precioCompra = precioCompra;
        this.tiempoEntregaDias = tiempoEntregaDias;
        this.esPredeterminado = esPredeterminado;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Integer getTiempoEntregaDias() {
        return tiempoEntregaDias;
    }

    public void setTiempoEntregaDias(Integer tiempoEntregaDias) {
        this.tiempoEntregaDias = tiempoEntregaDias;
    }

    public Boolean getEsPredeterminado() {
        return esPredeterminado;
    }

    public void setEsPredeterminado(Boolean esPredeterminado) {
        this.esPredeterminado = esPredeterminado;
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
}
