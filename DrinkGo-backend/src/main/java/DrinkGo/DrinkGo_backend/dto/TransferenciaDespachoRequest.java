package DrinkGo.DrinkGo_backend.dto;

import java.util.List;

/**
 * DTO para despachar una transferencia (registra cantidades enviadas).
 */
public class TransferenciaDespachoRequest {

    private List<DetalleDespachoItem> detalles;

    public List<DetalleDespachoItem> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleDespachoItem> detalles) {
        this.detalles = detalles;
    }

    public static class DetalleDespachoItem {
        private Long detalleId;
        private Integer cantidadEnviada;

        public Long getDetalleId() {
            return detalleId;
        }

        public void setDetalleId(Long detalleId) {
            this.detalleId = detalleId;
        }

        public Integer getCantidadEnviada() {
            return cantidadEnviada;
        }

        public void setCantidadEnviada(Integer cantidadEnviada) {
            this.cantidadEnviada = cantidadEnviada;
        }
    }
}
