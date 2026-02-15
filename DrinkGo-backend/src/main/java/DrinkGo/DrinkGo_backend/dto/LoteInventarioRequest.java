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

    @NotNull(message = "La cantidad_inicial es obligatoria")
    @Min(value = 1, message = "La cantidad_inicial debe ser al menos 1")
    private Integer cantidadInicial;

    @NotNull(message = "El precio_compra es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio_compra debe ser mayor a 0")
    private BigDecimal precioCompra;

    private LocalDate fechaFabricacion;

    private LocalDate fechaVencimiento;

    @NotNull(message = "La fecha_recepcion es obligatoria")
    private LocalDate fechaRecepcion;

    private Long proveedorId;

    private Long ordenCompraId;

    @Size(max = 65535, message = "Las notas son demasiado largas")
    private String notas;

    public LoteInventarioRequest() {}

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public Integer getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(Integer cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public LocalDate getFechaFabricacion() { return fechaFabricacion; }
    public void setFechaFabricacion(LocalDate fechaFabricacion) { this.fechaFabricacion = fechaFabricacion; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDate getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDate fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public Long getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
