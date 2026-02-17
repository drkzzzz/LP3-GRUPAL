package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO Request para UsuarioRol
 */
public class UsuarioRolRequest {

    private Long usuarioId;
    private Long rolId;
    private Long asignadoPor;
    private LocalDateTime validoHasta;

    // ── Getters y Setters ──

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    public Long getAsignadoPor() { return asignadoPor; }
    public void setAsignadoPor(Long asignadoPor) { this.asignadoPor = asignadoPor; }

    public LocalDateTime getValidoHasta() { return validoHasta; }
    public void setValidoHasta(LocalDateTime validoHasta) { this.validoHasta = validoHasta; }
}
