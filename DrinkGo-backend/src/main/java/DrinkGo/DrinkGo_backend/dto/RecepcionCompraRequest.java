package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDate;

/**
 * DTO Request para RecepcionCompra
 */
public class RecepcionCompraRequest {

    private Long negocioId;
    private Long ordenCompraId;
    private String numeroRecepcion;
    private Long recibidoPor;
    private LocalDate fechaRecepcion;
    private String notas;
    private String estado;

    // ── Getters y Setters ──

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }

    public String getNumeroRecepcion() { return numeroRecepcion; }
    public void setNumeroRecepcion(String numeroRecepcion) { this.numeroRecepcion = numeroRecepcion; }

    public Long getRecibidoPor() { return recibidoPor; }
    public void setRecibidoPor(Long recibidoPor) { this.recibidoPor = recibidoPor; }

    public LocalDate getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDate fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
