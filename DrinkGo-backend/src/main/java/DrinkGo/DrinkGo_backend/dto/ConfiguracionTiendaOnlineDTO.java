package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class ConfiguracionTiendaOnlineDTO {

    private Long id;
    private Long negocioId;
    private String nombreTienda;
    private String descripcion;
    private String logo;
    private String colorPrimario;
    private String colorSecundario;
    private Boolean permitirPagos;
    private Boolean permitirDelivery;
    private Boolean estaActivo;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructores
    public ConfiguracionTiendaOnlineDTO() {
    }

    public ConfiguracionTiendaOnlineDTO(Long id, Long negocioId, String nombreTienda, String descripcion, String logo,
            String colorPrimario, String colorSecundario, Boolean permitirPagos, Boolean permitirDelivery,
            Boolean estaActivo, LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.nombreTienda = nombreTienda;
        this.descripcion = descripcion;
        this.logo = logo;
        this.colorPrimario = colorPrimario;
        this.colorSecundario = colorSecundario;
        this.permitirPagos = permitirPagos;
        this.permitirDelivery = permitirDelivery;
        this.estaActivo = estaActivo;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
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

    public String getNombreTienda() {
        return nombreTienda;
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getColorPrimario() {
        return colorPrimario;
    }

    public void setColorPrimario(String colorPrimario) {
        this.colorPrimario = colorPrimario;
    }

    public String getColorSecundario() {
        return colorSecundario;
    }

    public void setColorSecundario(String colorSecundario) {
        this.colorSecundario = colorSecundario;
    }

    public Boolean getPermitirPagos() {
        return permitirPagos;
    }

    public void setPermitirPagos(Boolean permitirPagos) {
        this.permitirPagos = permitirPagos;
    }

    public Boolean getPermitirDelivery() {
        return permitirDelivery;
    }

    public void setPermitirDelivery(Boolean permitirDelivery) {
        this.permitirDelivery = permitirDelivery;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
