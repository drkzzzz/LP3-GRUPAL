package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para actualizar una orden de compra (PUT /restful/compras/{id}).
 * Bloque 6. Solo se puede actualizar si está en estado 'borrador'.
 */
public class OrdenCompraUpdateRequest {

    private String estado;
    private String moneda;
    private LocalDate fechaEntregaEsperada;
    private Integer plazoPagoDias;
    private String notas;
    private String razonCancelacion;
    private List<DetalleOrdenCompraRequest> items;

    // --- Getters y Setters ---

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public LocalDate getFechaEntregaEsperada() { return fechaEntregaEsperada; }
    public void setFechaEntregaEsperada(LocalDate fechaEntregaEsperada) { this.fechaEntregaEsperada = fechaEntregaEsperada; }

    public Integer getPlazoPagoDias() { return plazoPagoDias; }
    public void setPlazoPagoDias(Integer plazoPagoDias) { this.plazoPagoDias = plazoPagoDias; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getRazonCancelacion() { return razonCancelacion; }
    public void setRazonCancelacion(String razonCancelacion) { this.razonCancelacion = razonCancelacion; }

    public List<DetalleOrdenCompraRequest> getItems() { return items; }
    public void setItems(List<DetalleOrdenCompraRequest> items) { this.items = items; }
}