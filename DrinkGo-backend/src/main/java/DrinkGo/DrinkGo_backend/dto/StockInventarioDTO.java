package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockInventarioDTO {

    private Long id;
    private Long almacenId;
    private Long productoId;
    private BigDecimal cantidad;
    private BigDecimal cantidadReservada;
    private BigDecimal cantidadDisponible;
    private LocalDateTime ultimaActualizacion;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public StockInventarioDTO() {
    }

    public StockInventarioDTO(Long id, Long almacenId, Long productoId, BigDecimal cantidad,
            BigDecimal cantidadReservada, BigDecimal cantidadDisponible, LocalDateTime ultimaActualizacion,
            LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.almacenId = almacenId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.cantidadReservada = cantidadReservada;
        this.cantidadDisponible = cantidadDisponible;
        this.ultimaActualizacion = ultimaActualizacion;
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

    public Long getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCantidadReservada() {
        return cantidadReservada;
    }

    public void setCantidadReservada(BigDecimal cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(BigDecimal cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
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
