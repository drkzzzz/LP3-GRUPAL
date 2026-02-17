package DrinkGo.DrinkGo_backend.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clave compuesta para la tabla 'promociones_sedes'.
 * Bloque 13.
 */
public class PromocionSedeId implements Serializable {

    private Long promocionId;
    private Long sedeId;

    public PromocionSedeId() {}

    public PromocionSedeId(Long promocionId, Long sedeId) {
        this.promocionId = promocionId;
        this.sedeId = sedeId;
    }

    public Long getPromocionId() { return promocionId; }
    public void setPromocionId(Long promocionId) { this.promocionId = promocionId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PromocionSedeId that = (PromocionSedeId) o;
        return Objects.equals(promocionId, that.promocionId) &&
               Objects.equals(sedeId, that.sedeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promocionId, sedeId);
    }
}