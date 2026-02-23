package DrinkGo.DrinkGo_backend.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;

@Entity
@Table(name = "condiciones_promocion")
@JsonPropertyOrder({ "id", "promocionId", "tipoEntidad", "entidadId" })
public class CondicionesPromocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promocion_id", nullable = false)
    private Promociones promocion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entidad", nullable = false)
    private TipoEntidad tipoEntidad;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    public enum TipoEntidad {
        producto, categoria, marca
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Promociones getPromocion() {
        return promocion;
    }

    public void setPromocion(Promociones promocion) {
        this.promocion = promocion;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidad tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    @Override
    public String toString() {
        return "CondicionesPromocion [id=" + id + ", tipoEntidad=" + tipoEntidad + ", entidadId=" + entidadId + "]";
    }
}
