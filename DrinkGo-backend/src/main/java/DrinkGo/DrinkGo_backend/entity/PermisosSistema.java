package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;

@Entity
@Table(name = "permisos_sistema")
@JsonPropertyOrder({ "id", "moduloId", "codigo", "nombre", "descripcion", "tipoAccion" })
public class PermisosSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private ModulosSistema modulo;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion")
    private TipoAccion tipoAccion = TipoAccion.ver;

    public enum TipoAccion {
        ver, crear, editar, eliminar, exportar, aprobar, configurar, completo
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModulosSistema getModulo() {
        return modulo;
    }

    public void setModulo(ModulosSistema modulo) {
        this.modulo = modulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoAccion getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(TipoAccion tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    @Override
    public String toString() {
        return "PermisosSistema [id=" + id + ", modulo=" + (modulo != null ? modulo.getId() : null) + ", codigo="
                + codigo + ", nombre=" + nombre + ", descripcion=" + descripcion + ", tipoAccion=" + tipoAccion + "]";
    }
}
