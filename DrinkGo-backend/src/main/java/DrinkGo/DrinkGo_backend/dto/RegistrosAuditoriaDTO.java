package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class RegistrosAuditoriaDTO {

    private Long id;
    private Long negocioId;
    private Long usuarioId;
    private String accion;
    private String tablaAfectada;
    private Long registroId;
    private String valoresAnteriores;
    private String valoresNuevos;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime creadoEn;

    // Constructores
    public RegistrosAuditoriaDTO() {
    }

    public RegistrosAuditoriaDTO(Long id, Long negocioId, Long usuarioId, String accion, String tablaAfectada,
            Long registroId, String valoresAnteriores, String valoresNuevos, String ipAddress, String userAgent,
            LocalDateTime creadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.usuarioId = usuarioId;
        this.accion = accion;
        this.tablaAfectada = tablaAfectada;
        this.registroId = registroId;
        this.valoresAnteriores = valoresAnteriores;
        this.valoresNuevos = valoresNuevos;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getTablaAfectada() {
        return tablaAfectada;
    }

    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }

    public Long getRegistroId() {
        return registroId;
    }

    public void setRegistroId(Long registroId) {
        this.registroId = registroId;
    }

    public String getValoresAnteriores() {
        return valoresAnteriores;
    }

    public void setValoresAnteriores(String valoresAnteriores) {
        this.valoresAnteriores = valoresAnteriores;
    }

    public String getValoresNuevos() {
        return valoresNuevos;
    }

    public void setValoresNuevos(String valoresNuevos) {
        this.valoresNuevos = valoresNuevos;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
