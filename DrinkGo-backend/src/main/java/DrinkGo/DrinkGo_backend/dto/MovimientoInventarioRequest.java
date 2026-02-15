package DrinkGo.DrinkGo_backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para registrar un movimiento de inventario (ajuste manual).
 * negocio_id y realizado_por se obtienen del token JWT.
 */
public class MovimientoInventarioRequest {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    @NotNull(message = "El almacen_id es obligatorio")
    private Long almacenId;

    private Long loteId;

    @NotBlank(message = "El tipo_movimiento es obligatorio")
    private String tipoMovimiento;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @DecimalMin(value = "0.00", message = "El costo_unitario no puede ser negativo")
    private BigDecimal costoUnitario;

    @Size(max = 50, message = "El tipo_referencia no puede exceder 50 caracteres")
    private String tipoReferencia;

    private Long referenciaId;

    @Size(max = 300, message = "El motivo no puede exceder 300 caracteres")
    private String motivo;

    public MovimientoInventarioRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }

    public String getTipoReferencia() { return tipoReferencia; }
    public void setTipoReferencia(String tipoReferencia) { this.tipoReferencia = tipoReferencia; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
