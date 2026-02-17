package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO Response para UsuarioRol
 */
public class UsuarioRolResponse {

    private Long id;
    private Long usuarioId;
    private Long rolId;
    private Long asignadoPor;
    private LocalDateTime asignadoEn;
    private LocalDateTime validoHasta;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    public Long getAsignadoPor() { return asignadoPor; }
    public void setAsignadoPor(Long asignadoPor) { this.asignadoPor = asignadoPor; }

    public LocalDateTime getAsignadoEn() { return asignadoEn; }
    public void setAsignadoEn(LocalDateTime asignadoEn) { this.asignadoEn = asignadoEn; }

    public LocalDateTime getValidoHasta() { return validoHasta; }
    public void setValidoHasta(LocalDateTime validoHasta) { this.validoHasta = validoHasta; }
}
