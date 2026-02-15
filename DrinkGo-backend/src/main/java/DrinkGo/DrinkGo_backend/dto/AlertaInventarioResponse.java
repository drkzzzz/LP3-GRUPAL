package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para alertas de inventario.
 */
public class AlertaInventarioResponse {

    private Long id;
    private Long negocioId;
    private Long productoId;
    private String productoNombre;
    private Long almacenId;
    private String almacenNombre;
    private String tipoAlerta;
    private String mensaje;
    private Integer valorUmbral;
    private Integer valorActual;
    private Boolean estaResuelta;
    private LocalDateTime resueltaEn;
    private Long resueltaPor;
    private LocalDateTime creadoEn;

    public AlertaInventarioResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public Long getAlmacenId() { return almacenId; }
    public void setAlmacenId(Long almacenId) { this.almacenId = almacenId; }

    public String getAlmacenNombre() { return almacenNombre; }
    public void setAlmacenNombre(String almacenNombre) { this.almacenNombre = almacenNombre; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Integer getValorUmbral() { return valorUmbral; }
    public void setValorUmbral(Integer valorUmbral) { this.valorUmbral = valorUmbral; }

    public Integer getValorActual() { return valorActual; }
    public void setValorActual(Integer valorActual) { this.valorActual = valorActual; }

    public Boolean getEstaResuelta() { return estaResuelta; }
    public void setEstaResuelta(Boolean estaResuelta) { this.estaResuelta = estaResuelta; }

    public LocalDateTime getResueltaEn() { return resueltaEn; }
    public void setResueltaEn(LocalDateTime resueltaEn) { this.resueltaEn = resueltaEn; }

    public Long getResueltaPor() { return resueltaPor; }
    public void setResueltaPor(Long resueltaPor) { this.resueltaPor = resueltaPor; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
