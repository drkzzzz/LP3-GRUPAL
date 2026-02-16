package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para crear una alerta de inventario manualmente (POST /restful/inventario/alertas).
 */
public class AlertaCreateRequest {

    private Long productoId;
    private Long almacenId;
    private String tipoAlerta;
    private String mensaje;
    private Integer valorUmbral;
    private Integer valorActual;

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

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getValorUmbral() {
        return valorUmbral;
    }

    public void setValorUmbral(Integer valorUmbral) {
        this.valorUmbral = valorUmbral;
    }

    public Integer getValorActual() {
        return valorActual;
    }

    public void setValorActual(Integer valorActual) {
        this.valorActual = valorActual;
    }
}
