package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_sedes")
@JsonPropertyOrder({ "id", "usuarioId", "sedeId", "esPredeterminado", "asignadoEn", "asignadoPor" })
public class UsuariosSedes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    private Sedes sede;

    @Column(name = "es_predeterminado")
    private Boolean esPredeterminado = false;

    @Column(name = "asignado_en")
    private LocalDateTime asignadoEn;

    @Column(name = "asignado_por")
    private Long asignadoPor;

    @PrePersist
    protected void onCreate() {
        asignadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Sedes getSede() {
        return sede;
    }

    public void setSede(Sedes sede) {
        this.sede = sede;
    }

    public Boolean getEsPredeterminado() {
        return esPredeterminado;
    }

    public void setEsPredeterminado(Boolean esPredeterminado) {
        this.esPredeterminado = esPredeterminado;
    }

    public LocalDateTime getAsignadoEn() {
        return asignadoEn;
    }

    public void setAsignadoEn(LocalDateTime asignadoEn) {
        this.asignadoEn = asignadoEn;
    }

    public Long getAsignadoPor() {
        return asignadoPor;
    }

    public void setAsignadoPor(Long asignadoPor) {
        this.asignadoPor = asignadoPor;
    }

    @Override
    public String toString() {
        return "UsuariosSedes [id=" + id + ", usuario=" + (usuario != null ? usuario.getId() : null) + ", sede="
                + (sede != null ? sede.getId() : null) + ", esPredeterminado=" + esPredeterminado + ", asignadoEn="
                + asignadoEn + ", asignadoPor=" + asignadoPor + "]";
    }
}
