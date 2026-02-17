package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para transferencias de inventario.
 */
public class TransferenciaInventarioResponse {

    private Long id;
    private Long negocioId;
    private String numeroTransferencia;
    private Long almacenOrigenId;
    private String almacenOrigenNombre;
    private Long almacenDestinoId;
    private String almacenDestinoNombre;
    private String estado;
    private Long solicitadoPor;
    private Long aprobadoPor;
    private Long recibidoPor;
    private String notas;
    private LocalDateTime solicitadoEn;
    private LocalDateTime aprobadoEn;
    private LocalDateTime despachadoEn;
    private LocalDateTime recibidoEn;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    private List<DetalleTransferenciaResponse> detalles;

    public TransferenciaInventarioResponse() {}

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getNumeroTransferencia() { return numeroTransferencia; }
    public void setNumeroTransferencia(String numeroTransferencia) { this.numeroTransferencia = numeroTransferencia; }

    public Long getAlmacenOrigenId() { return almacenOrigenId; }
    public void setAlmacenOrigenId(Long almacenOrigenId) { this.almacenOrigenId = almacenOrigenId; }

    public String getAlmacenOrigenNombre() { return almacenOrigenNombre; }
    public void setAlmacenOrigenNombre(String almacenOrigenNombre) { this.almacenOrigenNombre = almacenOrigenNombre; }

    public Long getAlmacenDestinoId() { return almacenDestinoId; }
    public void setAlmacenDestinoId(Long almacenDestinoId) { this.almacenDestinoId = almacenDestinoId; }

    public String getAlmacenDestinoNombre() { return almacenDestinoNombre; }
    public void setAlmacenDestinoNombre(String almacenDestinoNombre) { this.almacenDestinoNombre = almacenDestinoNombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getSolicitadoPor() { return solicitadoPor; }
    public void setSolicitadoPor(Long solicitadoPor) { this.solicitadoPor = solicitadoPor; }

    public Long getAprobadoPor() { return aprobadoPor; }
    public void setAprobadoPor(Long aprobadoPor) { this.aprobadoPor = aprobadoPor; }

    public Long getRecibidoPor() { return recibidoPor; }
    public void setRecibidoPor(Long recibidoPor) { this.recibidoPor = recibidoPor; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public LocalDateTime getSolicitadoEn() { return solicitadoEn; }
    public void setSolicitadoEn(LocalDateTime solicitadoEn) { this.solicitadoEn = solicitadoEn; }

    public LocalDateTime getAprobadoEn() { return aprobadoEn; }
    public void setAprobadoEn(LocalDateTime aprobadoEn) { this.aprobadoEn = aprobadoEn; }

    public LocalDateTime getDespachadoEn() { return despachadoEn; }
    public void setDespachadoEn(LocalDateTime despachadoEn) { this.despachadoEn = despachadoEn; }

    public LocalDateTime getRecibidoEn() { return recibidoEn; }
    public void setRecibidoEn(LocalDateTime recibidoEn) { this.recibidoEn = recibidoEn; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }

    public List<DetalleTransferenciaResponse> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleTransferenciaResponse> detalles) { this.detalles = detalles; }
}
