package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta estándar para errores.
 * Utilizado por el @ControllerAdvice para manejo global de excepciones.
 */
public class ErrorResponse {

    private int status;
    private String error;
    private String mensaje;
    private String timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public ErrorResponse(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now().toString();
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
