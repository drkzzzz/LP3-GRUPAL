package DrinkGo.DrinkGo_backend.dto;

import java.util.List;

/**
 * DTO para recibir una transferencia (registra cantidades recibidas).
 */
public class TransferenciaRecepcionRequest {

    private List<DetalleRecepcionItem> detalles;

    public List<DetalleRecepcionItem> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleRecepcionItem> detalles) {
        this.detalles = detalles;
    }

    public static class DetalleRecepcionItem {
        private Long detalleId;
        private Integer cantidadRecibida;

        public Long getDetalleId() {
            return detalleId;
        }

        public void setDetalleId(Long detalleId) {
            this.detalleId = detalleId;
        }

        public Integer getCantidadRecibida() {
            return cantidadRecibida;
        }

        public void setCantidadRecibida(Integer cantidadRecibida) {
            this.cantidadRecibida = cantidadRecibida;
        }
    }
}
