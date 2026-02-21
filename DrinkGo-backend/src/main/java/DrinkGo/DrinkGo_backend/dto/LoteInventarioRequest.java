package DrinkGo.DrinkGo_backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para registrar un nuevo lote de inventario.
 * negocio_id se obtiene del token JWT (nunca del JSON).
 */
public class LoteInventarioRequest {

    @NotNull(message = "El producto_id es obligatorio")
    private Long productoId;

    @NotNull(message = "El almacen_id es obligatorio")
    private Long almacenId;

    @NotBlank(message = "El numero_lote es obligatorio")
    @Size(max = 50, message = "El numero_lote no puede exceder 50 caracteres")
    private String numeroLote;

    @NotNull(message = "La cantidad_restante es obligatoria")
    @Min(value = 1, message = "La cantidad_restante debe ser al menos 1")
    private Integer cantidadRestante;

    @NotNull(message = "El costo_unitario_compra es obligatorio")
    @DecimalMin(value = "0.00", message = "El costo_unitario_compra no puede ser negativo")
    private BigDecimal costoUnitarioCompra;

    private LocalDate fechaVencimiento;

    public LoteInventarioRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Integer getCantidadRestante() { return cantidadRestante; }
    public void setCantidadRestante(Integer cantidadRestante) { this.cantidadRestante = cantidadRestante; }

    public BigDecimal getCostoUnitarioCompra() { return costoUnitarioCompra; }
    public void setCostoUnitarioCompra(BigDecimal costoUnitarioCompra) { this.costoUnitarioCompra = costoUnitarioCompra; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
