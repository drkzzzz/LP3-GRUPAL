package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "roles_permisos")
@JsonPropertyOrder({ "id", "rolId", "permisoId", "alcance", "creadoEn" })
public class RolesPermisos {

    /** Alcance del permiso: completo = acceso total, caja_asignada = solo su caja */
    public enum Alcance {
        completo,
        caja_asignada
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Roles rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", nullable = false)
    private PermisosSistema permiso;

    @Enumerated(EnumType.STRING)
    @Column(name = "alcance", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'completo'")
    private Alcance alcance = Alcance.completo;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public PermisosSistema getPermiso() {
        return permiso;
    }

    public void setPermiso(PermisosSistema permiso) {
        this.permiso = permiso;
    }

    public Alcance getAlcance() {
        return alcance;
    }

    public void setAlcance(Alcance alcance) {
        this.alcance = alcance;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public String toString() {
        return "RolesPermisos [id=" + id + ", rol=" + (rol != null ? rol.getId() : null) + ", permiso="
                + (permiso != null ? permiso.getId() : null) + ", alcance=" + alcance + ", creadoEn=" + creadoEn + "]";
    }
}
