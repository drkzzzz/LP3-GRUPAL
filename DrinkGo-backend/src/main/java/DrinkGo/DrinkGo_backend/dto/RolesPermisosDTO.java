package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class RolesPermisosDTO {

    private Long id;
    private Long rolId;
    private Long permisoSistemaId;
    private LocalDateTime creadoEn;

    // Constructores
    public RolesPermisosDTO() {
    }

    public RolesPermisosDTO(Long id, Long rolId, Long permisoSistemaId, LocalDateTime creadoEn) {
        this.id = id;
        this.rolId = rolId;
        this.permisoSistemaId = permisoSistemaId;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public Long getPermisoSistemaId() {
        return permisoSistemaId;
    }

    public void setPermisoSistemaId(Long permisoSistemaId) {
        this.permisoSistemaId = permisoSistemaId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
