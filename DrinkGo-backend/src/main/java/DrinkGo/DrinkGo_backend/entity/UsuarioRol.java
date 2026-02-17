package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad UsuarioRol - Roles asignados a usuarios
 * RF-ADM-018
 */
@Entity
@Table(name = "usuarios_roles")
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "rol_id", nullable = false)
    private Long rolId;

    @Column(name = "asignado_por")
    private Long asignadoPor;

    @Column(name = "asignado_en", nullable = false, updatable = false)
    private LocalDateTime asignadoEn;

    @Column(name = "valido_hasta")
    private LocalDateTime validoHasta;

    // ── Lifecycle Callbacks ──

    @PrePersist
    protected void onCreate() {
        this.asignadoEn = LocalDateTime.now();
    }

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
