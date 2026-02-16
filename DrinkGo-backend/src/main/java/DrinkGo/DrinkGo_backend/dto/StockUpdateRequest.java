package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para actualizar stock manualmente (PUT).
 */
public class StockUpdateRequest {

    private Integer cantidadEnMano;
    private Integer cantidadReservada;

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
