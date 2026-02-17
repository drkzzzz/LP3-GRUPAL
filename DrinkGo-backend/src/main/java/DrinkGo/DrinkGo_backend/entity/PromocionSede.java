package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;

/**
 * Entidad PromocionSede - Mapea la tabla 'promociones_sedes'.
 * Bloque 13: Relación muchos-a-muchos entre promociones y sedes.
 */
@Entity
@Table(name = "promociones_sedes")
@IdClass(PromocionSedeId.class)
public class PromocionSede {

    @Id
    @Column(name = "promocion_id")
    private Long promocionId;

    @Id
    @Column(name = "sede_id")
    private Long sedeId;

    // --- Getters y Setters ---

    public Long getPromocionId() { return promocionId; }
    public void setPromocionId(Long promocionId) { this.promocionId = promocionId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }
}