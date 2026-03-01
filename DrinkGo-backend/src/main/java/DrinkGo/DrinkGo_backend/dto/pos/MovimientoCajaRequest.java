package DrinkGo.DrinkGo_backend.dto.pos;

import java.math.BigDecimal;

/**
 * DTO para registrar un movimiento de caja.
 */
public class MovimientoCajaRequest {

    private Long sesionCajaId;
    private Long usuarioId;
    private String tipo;        // ingreso | egreso
    private BigDecimal monto;
    private String concepto;
    private Long categoriaGastoId;

    public Long getSesionCajaId() { return sesionCajaId; }
    public void setSesionCajaId(Long sesionCajaId) { this.sesionCajaId = sesionCajaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public Long getCategoriaGastoId() { return categoriaGastoId; }
    public void setCategoriaGastoId(Long categoriaGastoId) { this.categoriaGastoId = categoriaGastoId; }
}
