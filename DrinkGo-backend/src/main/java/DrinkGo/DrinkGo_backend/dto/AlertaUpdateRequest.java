package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para actualizar una alerta de inventario (PUT).
 */
public class AlertaUpdateRequest {

    private String mensaje;
    private Integer valorUmbral;
    private Integer valorActual;
    private Boolean estaResuelta;

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

    public Boolean getEstaResuelta() {
        return estaResuelta;
    }

    public void setEstaResuelta(Boolean estaResuelta) {
        this.estaResuelta = estaResuelta;
    }
}
