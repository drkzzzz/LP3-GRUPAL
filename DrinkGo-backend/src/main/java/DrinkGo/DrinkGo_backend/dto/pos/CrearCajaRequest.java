package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * DTO para crear o actualizar una caja registradora.
 */
public class CrearCajaRequest {

    private Long id; // null para crear, set para actualizar
    private Long negocioId;
    private Long sedeId; // opcional: si es null se usa la primera sede del negocio
    private String nombreCaja;
    private String codigo;
    private BigDecimal montoAperturaDefecto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getMontoAperturaDefecto() {
        return montoAperturaDefecto;
    }

    public void setMontoAperturaDefecto(BigDecimal montoAperturaDefecto) {
        this.montoAperturaDefecto = montoAperturaDefecto;
    }
}
