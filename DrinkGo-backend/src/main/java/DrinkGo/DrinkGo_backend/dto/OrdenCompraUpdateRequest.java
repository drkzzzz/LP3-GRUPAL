package DrinkGo.DrinkGo_backend.dto;

import java.util.List;

/**
 * DTO para actualizar una orden de compra (PUT /restful/compras/{id}).
 * Bloque 6. Solo se puede actualizar si está en estado 'pendiente'.
 */
public class OrdenCompraUpdateRequest {

    private String estado;
    private String notas;
    private List<DetalleOrdenCompraRequest> items;

    // --- Getters y Setters ---

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public List<DetalleOrdenCompraRequest> getItems() { return items; }
    public void setItems(List<DetalleOrdenCompraRequest> items) { this.items = items; }
}
