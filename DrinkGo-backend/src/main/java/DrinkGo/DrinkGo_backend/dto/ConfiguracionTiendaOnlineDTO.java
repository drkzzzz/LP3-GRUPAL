package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

public class ConfiguracionTiendaOnlineDTO {
    
    private Long id;
    private Long negocioId;
    private Boolean estaHabilitado;
    private String nombreTienda;
    private String slugTienda;
    private String dominioPersonalizado;
    private String mensajeBienvenida;
    private String imagenesBanner;
    private String categoriasDestacadas;
    private String tituloSeo;
    private String descripcionSeo;
    private String palabrasClaveSeo;
    private String idGoogleAnalytics;
    private String idPixelFacebook;
    private String enlacesSociales;
    private BigDecimal montoMinimoPedido;
    private BigDecimal montoMaximoPedido;
    private String terminosCondiciones;
    private String politicaPrivacidad;
    private String politicaDevoluciones;
    private Boolean mostrarPreciosConImpuesto;
    private Boolean permitirCompraInvitado;
    private Boolean requiereVerificacionEdad;
    
    public ConfiguracionTiendaOnlineDTO() {}
    
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
    
    public Boolean getEstaHabilitado() {
        return estaHabilitado;
    }
    
    public void setEstaHabilitado(Boolean estaHabilitado) {
        this.estaHabilitado = estaHabilitado;
    }
    
    public String getNombreTienda() {
        return nombreTienda;
    }
    
    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }
    
    public String getSlugTienda() {
        return slugTienda;
    }
    
    public void setSlugTienda(String slugTienda) {
        this.slugTienda = slugTienda;
    }
    
    public String getDominioPersonalizado() {
        return dominioPersonalizado;
    }
    
    public void setDominioPersonalizado(String dominioPersonalizado) {
        this.dominioPersonalizado = dominioPersonalizado;
    }
    
    public String getMensajeBienvenida() {
        return mensajeBienvenida;
    }
    
    public void setMensajeBienvenida(String mensajeBienvenida) {
        this.mensajeBienvenida = mensajeBienvenida;
    }
    
    public String getImagenesBanner() {
        return imagenesBanner;
    }
    
    public void setImagenesBanner(String imagenesBanner) {
        this.imagenesBanner = imagenesBanner;
    }
    
    public String getCategoriasDestacadas() {
        return categoriasDestacadas;
    }
    
    public void setCategoriasDestacadas(String categoriasDestacadas) {
        this.categoriasDestacadas = categoriasDestacadas;
    }
    
    public String getTituloSeo() {
        return tituloSeo;
    }
    
    public void setTituloSeo(String tituloSeo) {
        this.tituloSeo = tituloSeo;
    }
    
    public String getDescripcionSeo() {
        return descripcionSeo;
    }
    
    public void setDescripcionSeo(String descripcionSeo) {
        this.descripcionSeo = descripcionSeo;
    }
    
    public String getPalabrasClaveSeo() {
        return palabrasClaveSeo;
    }
    
    public void setPalabrasClaveSeo(String palabrasClaveSeo) {
        this.palabrasClaveSeo = palabrasClaveSeo;
    }
    
    public String getIdGoogleAnalytics() {
        return idGoogleAnalytics;
    }
    
    public void setIdGoogleAnalytics(String idGoogleAnalytics) {
        this.idGoogleAnalytics = idGoogleAnalytics;
    }
    
    public String getIdPixelFacebook() {
        return idPixelFacebook;
    }
    
    public void setIdPixelFacebook(String idPixelFacebook) {
        this.idPixelFacebook = idPixelFacebook;
    }
    
    public String getEnlacesSociales() {
        return enlacesSociales;
    }
    
    public void setEnlacesSociales(String enlacesSociales) {
        this.enlacesSociales = enlacesSociales;
    }
    
    public BigDecimal getMontoMinimoPedido() {
        return montoMinimoPedido;
    }
    
    public void setMontoMinimoPedido(BigDecimal montoMinimoPedido) {
        this.montoMinimoPedido = montoMinimoPedido;
    }
    
    public BigDecimal getMontoMaximoPedido() {
        return montoMaximoPedido;
    }
    
    public void setMontoMaximoPedido(BigDecimal montoMaximoPedido) {
        this.montoMaximoPedido = montoMaximoPedido;
    }
    
    public String getTerminosCondiciones() {
        return terminosCondiciones;
    }
    
    public void setTerminosCondiciones(String terminosCondiciones) {
        this.terminosCondiciones = terminosCondiciones;
    }
    
    public String getPoliticaPrivacidad() {
        return politicaPrivacidad;
    }
    
    public void setPoliticaPrivacidad(String politicaPrivacidad) {
        this.politicaPrivacidad = politicaPrivacidad;
    }
    
    public String getPoliticaDevoluciones() {
        return politicaDevoluciones;
    }
    
    public void setPoliticaDevoluciones(String politicaDevoluciones) {
        this.politicaDevoluciones = politicaDevoluciones;
    }
    
    public Boolean getMostrarPreciosConImpuesto() {
        return mostrarPreciosConImpuesto;
    }
    
    public void setMostrarPreciosConImpuesto(Boolean mostrarPreciosConImpuesto) {
        this.mostrarPreciosConImpuesto = mostrarPreciosConImpuesto;
    }
    
    public Boolean getPermitirCompraInvitado() {
        return permitirCompraInvitado;
    }
    
    public void setPermitirCompraInvitado(Boolean permitirCompraInvitado) {
        this.permitirCompraInvitado = permitirCompraInvitado;
    }
    
    public Boolean getRequiereVerificacionEdad() {
        return requiereVerificacionEdad;
    }
    
    public void setRequiereVerificacionEdad(Boolean requiereVerificacionEdad) {
        this.requiereVerificacionEdad = requiereVerificacionEdad;
    }
}
