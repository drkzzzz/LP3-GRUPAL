package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;

@Entity
@Table(name = "modulos_sistema")
@SQLDelete(sql = "UPDATE modulos_sistema SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "codigo", "nombre", "descripcion", "moduloPadreId", "icono", "orden", "estaActivo" })
public class ModulosSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_padre_id")
    private ModulosSistema moduloPadre;

    private String icono;

    private Integer orden = 0;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ModulosSistema getModuloPadre() {
        return moduloPadre;
    }

    public void setModuloPadre(ModulosSistema moduloPadre) {
        this.moduloPadre = moduloPadre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    @Override
    public String toString() {
        return "ModulosSistema [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", moduloPadre=" + (moduloPadre != null ? moduloPadre.getId() : null) + ", icono=" + icono
                + ", orden=" + orden + ", estaActivo=" + estaActivo + "]";
    }
}
