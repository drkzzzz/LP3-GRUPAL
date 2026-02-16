package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para aprobar o rechazar una devolución.
 * RF-FIN-009: Gestionar Motivos y Aprobación de Devoluciones.
 */
public class AprobarDevolucionRequest {

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
