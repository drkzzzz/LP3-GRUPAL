package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad RolPermiso - Permisos asignados a roles
 * RF-ADM-017
 */
@Entity
@Table(name = "roles_permisos")
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rol_id", nullable = false)
    private Long rolId;

    @Column(name = "permiso_id", nullable = false)
    private Long permisoId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }

    public Long getPermisoId() { return permisoId; }
    public void setPermisoId(Long permisoId) { this.permisoId = permisoId; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
