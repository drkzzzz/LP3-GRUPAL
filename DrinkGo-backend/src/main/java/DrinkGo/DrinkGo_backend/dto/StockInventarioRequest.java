package DrinkGo.DrinkGo_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear o actualizar stock de inventario.
 * negocio_id se obtiene del token JWT (nunca del JSON).
 */
public class StockInventarioRequest {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    @NotNull(message = "El almacen_id es obligatorio")
    private Long almacenId;

    @NotNull(message = "La cantidad_en_mano es obligatoria")
    @Min(value = 0, message = "La cantidad_en_mano no puede ser negativa")
    private Integer cantidadEnMano;

    @Min(value = 0, message = "La cantidad_reservada no puede ser negativa")
    private Integer cantidadReservada = 0;

    public StockInventarioRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public Integer getCantidadEnMano() { return cantidadEnMano; }
    public void setCantidadEnMano(Integer cantidadEnMano) { this.cantidadEnMano = cantidadEnMano; }

    public Integer getCantidadReservada() { return cantidadReservada; }
    public void setCantidadReservada(Integer cantidadReservada) { this.cantidadReservada = cantidadReservada; }
}
