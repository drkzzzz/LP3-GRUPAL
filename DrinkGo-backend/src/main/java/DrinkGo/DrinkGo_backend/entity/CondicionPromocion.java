package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad CondicionPromocion - Mapea la tabla 'condiciones_promocion'.
 * Bloque 13: Condiciones de aplicación (producto, categoría o marca).
 */
@Entity
@Table(name = "condiciones_promocion")
public class CondicionPromocion {

    public enum TipoEntidad {
        producto, categoria, marca
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promocion_id", nullable = false)
    private Long promocionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entidad", nullable = false)
    private TipoEntidad tipoEntidad;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPromocionId() { return promocionId; }
    public void setPromocionId(Long promocionId) { this.promocionId = promocionId; }

    public TipoEntidad getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(TipoEntidad tipoEntidad) { this.tipoEntidad = tipoEntidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }
}