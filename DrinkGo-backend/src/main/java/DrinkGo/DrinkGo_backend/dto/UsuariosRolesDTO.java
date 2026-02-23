package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class UsuariosRolesDTO {

    private Long id;
    private Long usuarioId;
    private Long rolId;
    private LocalDateTime creadoEn;

    // Constructores
    public UsuariosRolesDTO() {
    }

    public UsuariosRolesDTO(Long id, Long usuarioId, Long rolId, LocalDateTime creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.rolId = rolId;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
