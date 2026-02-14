package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para crear un registro de stock manualmente (POST /restful/inventario/stock).
 */
public class StockCreateRequest {

    private Long productoId;
    private Long almacenId;
    private Integer cantidadEnMano;
    private Integer cantidadReservada;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    public Integer getCantidadEnMano() {
        return cantidadEnMano;
    }

    public void setCantidadEnMano(Integer cantidadEnMano) {
        this.cantidadEnMano = cantidadEnMano;
    }

    public Integer getCantidadReservada() {
        return cantidadReservada;
    }

    public void setCantidadReservada(Integer cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }
}
