package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO Response para RegistroAuditoria
 */
public class RegistroAuditoriaResponse {

    private Long id;
    private Long negocioId;
    private Long usuarioId;
    private String accion;
    private String tipoEntidad;
    private Long entidadId;
    private Map<String, Object> valoresAnteriores;
    private Map<String, Object> valoresNuevos;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime fechaAccion;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String tipoEntidad) { this.tipoEntidad = tipoEntidad; }

    public Long getEntidadId() { return entidadId; }
    public void setEntidadId(Long entidadId) { this.entidadId = entidadId; }

    public Map<String, Object> getValoresAnteriores() { return valoresAnteriores; }
    public void setValoresAnteriores(Map<String, Object> valoresAnteriores) { this.valoresAnteriores = valoresAnteriores; }

    public Map<String, Object> getValoresNuevos() { return valoresNuevos; }
    public void setValoresNuevos(Map<String, Object> valoresNuevos) { this.valoresNuevos = valoresNuevos; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getFechaAccion() { return fechaAccion; }
    public void setFechaAccion(LocalDateTime fechaAccion) { this.fechaAccion = fechaAccion; }
}
