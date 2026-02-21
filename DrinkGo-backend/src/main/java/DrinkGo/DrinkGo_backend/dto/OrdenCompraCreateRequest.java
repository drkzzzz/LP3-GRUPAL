package DrinkGo.DrinkGo_backend.dto;

import java.util.List;

/**
 * DTO para crear una orden de compra (POST /restful/compras).
 * Bloque 6. Incluye la lista de items (detalle).
 */
public class OrdenCompraCreateRequest {

    private String numeroOrden;
    private Long proveedorId;
    private Long sedeId;
    private Long almacenId;
    private String notas;
    private List<DetalleOrdenCompraRequest> items;

    // --- Getters y Setters ---

    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public List<DetalleOrdenCompraRequest> getItems() { return items; }
    public void setItems(List<DetalleOrdenCompraRequest> items) { this.items = items; }
}
