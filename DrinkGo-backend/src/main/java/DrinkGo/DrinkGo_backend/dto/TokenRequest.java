package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para solicitar un token JWT.
 */
public class TokenRequest {

    private String clienteId;
    private String llaveSecreta;

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getLlaveSecreta() {
        return llaveSecreta;
    }

    public void setLlaveSecreta(String llaveSecreta) {
        this.llaveSecreta = llaveSecreta;
    }
}
