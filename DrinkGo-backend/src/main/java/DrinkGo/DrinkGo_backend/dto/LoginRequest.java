package DrinkGo.DrinkGo_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de login.
 * Se envía a POST /restful/token
 */
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /**
     * ID del negocio al que se quiere acceder.
     * Obligatorio cuando el mismo email existe en múltiples negocios.
     * Si se omite y solo hay un negocio, se usa ese automáticamente.
     */
    private Long negocioId;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginRequest(String email, String password, Long negocioId) {
        this.email = email;
        this.password = password;
        this.negocioId = negocioId;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }
}
