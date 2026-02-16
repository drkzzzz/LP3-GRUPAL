package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para stock de inventario.
 */
public class StockInventarioResponse {

    private Long id;
    private Long negocioId;
    private Long productoId;
    private String productoNombre;
    private Long almacenId;
    private String almacenNombre;
    private Integer cantidadEnMano;
    private Integer cantidadReservada;
    private Integer cantidadDisponible;
    private LocalDateTime ultimoConteoEn;
    private LocalDateTime ultimoMovimientoEn;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public StockInventarioResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public String getAlmacenNombre() { return almacenNombre; }
    public void setAlmacenNombre(String almacenNombre) { this.almacenNombre = almacenNombre; }

    public Integer getCantidadEnMano() { return cantidadEnMano; }
    public void setCantidadEnMano(Integer cantidadEnMano) { this.cantidadEnMano = cantidadEnMano; }

    public Integer getCantidadReservada() { return cantidadReservada; }
    public void setCantidadReservada(Integer cantidadReservada) { this.cantidadReservada = cantidadReservada; }

    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

    public LocalDateTime getUltimoConteoEn() { return ultimoConteoEn; }
    public void setUltimoConteoEn(LocalDateTime ultimoConteoEn) { this.ultimoConteoEn = ultimoConteoEn; }

    public LocalDateTime getUltimoMovimientoEn() { return ultimoMovimientoEn; }
    public void setUltimoMovimientoEn(LocalDateTime ultimoMovimientoEn) { this.ultimoMovimientoEn = ultimoMovimientoEn; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
