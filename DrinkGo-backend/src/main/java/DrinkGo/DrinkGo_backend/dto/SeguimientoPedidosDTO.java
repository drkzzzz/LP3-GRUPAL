package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class SeguimientoPedidosDTO {

    private Long id;
    private Long pedidoId;
    private String estadoAnterior;
    private String estadoNuevo;
    private String observaciones;
    private Long usuarioId;
    private LocalDateTime creadoEn;

    // Constructores
    public SeguimientoPedidosDTO() {
    }

    public SeguimientoPedidosDTO(Long id, Long pedidoId, String estadoAnterior, String estadoNuevo,
            String observaciones, Long usuarioId, LocalDateTime creadoEn) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.observaciones = observaciones;
        this.usuarioId = usuarioId;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
