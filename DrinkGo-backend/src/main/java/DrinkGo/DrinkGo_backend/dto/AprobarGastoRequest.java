package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para aprobar o rechazar un gasto.
 */
public class AprobarGastoRequest {

    private Long aprobadoPor;
    private String notas;

    // --- Getters y Setters ---

    public Long getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Long aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
