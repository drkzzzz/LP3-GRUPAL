package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO de respuesta con el token JWT generado.
 */
public class TokenResponse {

    private String accessToken;
    private String clienteId;

    public TokenResponse() {}

    public TokenResponse(String accessToken, String clienteId) {
        this.accessToken = accessToken;
        this.clienteId = clienteId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
}
