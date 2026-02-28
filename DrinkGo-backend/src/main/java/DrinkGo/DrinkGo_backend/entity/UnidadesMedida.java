package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;

@Entity
@Table(name = "unidades_medida")
@SQLDelete(sql = "UPDATE unidades_medida SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({ "id", "negocioId", "codigo", "nombre", "abreviatura", "tipo", "estaActivo" })
public class UnidadesMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String abreviatura;

    @Enumerated(EnumType.STRING)
    private TipoUnidad tipo = TipoUnidad.unidad;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    public enum TipoUnidad {
        volumen, peso, unidad, paquete, otro
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
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

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public TipoUnidad getTipo() {
        return tipo;
    }

    public void setTipo(TipoUnidad tipo) {
        this.tipo = tipo;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    @Override
    public String toString() {
        return "UnidadesMedida [id=" + id + ", negocio=" + (negocio != null ? negocio.getId() : null) + ", codigo="
                + codigo + ", nombre=" + nombre + ", abreviatura=" + abreviatura + ", tipo=" + tipo + ", estaActivo="
                + estaActivo + "]";
    }
}
