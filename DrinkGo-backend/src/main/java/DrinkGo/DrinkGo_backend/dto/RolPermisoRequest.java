package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO Request para RolPermiso
 */
public class RolPermisoRequest {

    private Long rolId;
    private Long permisoId;

    // ── Getters y Setters ──

    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    public Long getPermisoId() { return permisoId; }
    public void setPermisoId(Long permisoId) { this.permisoId = permisoId; }
}
