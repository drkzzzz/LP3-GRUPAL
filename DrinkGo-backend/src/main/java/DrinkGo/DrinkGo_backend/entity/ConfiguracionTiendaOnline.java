package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad ConfiguracionTiendaOnline - Configuración de la tienda online por negocio
 * Tabla: configuracion_tienda_online
 * BLOQUE 14: TIENDA ONLINE (STOREFRONT)
 */
@Entity
@Table(name = "configuracion_tienda_online", schema = "drinkgo_db")
public class ConfiguracionTiendaOnline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "negocio_id", nullable = false, unique = true)
    private Long negocioId;
    
    @Column(name = "esta_habilitado", nullable = false)
    private Boolean estaHabilitado = false;
    
    @Column(name = "nombre_tienda", length = 200)
    private String nombreTienda;
    
    @Column(name = "slug_tienda", length = 100, unique = true)
    private String slugTienda;
    
    @Column(name = "dominio_personalizado", length = 200)
    private String dominioPersonalizado;
    
    @Column(name = "mensaje_bienvenida", columnDefinition = "TEXT")
    private String mensajeBienvenida;
    
    @Column(name = "imagenes_banner", columnDefinition = "JSON")
    private String imagenesBanner;
    
    @Column(name = "categorias_destacadas", columnDefinition = "JSON")
    private String categoriasDestacadas;
    
    @Column(name = "titulo_seo", length = 200)
    private String tituloSeo;
    
    @Column(name = "descripcion_seo", length = 500)
    private String descripcionSeo;
    
    @Column(name = "palabras_clave_seo", length = 300)
    private String palabrasClaveSeo;
    
    @Column(name = "id_google_analytics", length = 50)
    private String idGoogleAnalytics;
    
    @Column(name = "id_pixel_facebook", length = 50)
    private String idPixelFacebook;
    
    @Column(name = "enlaces_sociales", columnDefinition = "JSON")
    private String enlacesSociales;
    
    @Column(name = "monto_minimo_pedido", precision = 10, scale = 2)
    private BigDecimal montoMinimoPedido;
    
    @Column(name = "monto_maximo_pedido", precision = 12, scale = 2)
    private BigDecimal montoMaximoPedido;
    
    @Column(name = "terminos_condiciones", columnDefinition = "TEXT")
    private String terminosCondiciones;
    
    @Column(name = "politica_privacidad", columnDefinition = "TEXT")
    private String politicaPrivacidad;
    
    @Column(name = "politica_devoluciones", columnDefinition = "TEXT")
    private String politicaDevoluciones;
    
    @Column(name = "mostrar_precios_con_impuesto", nullable = false)
    private Boolean mostrarPreciosConImpuesto = true;
    
    @Column(name = "permitir_compra_invitado", nullable = false)
    private Boolean permitirCompraInvitado = false;
    
    @Column(name = "requiere_verificacion_edad", nullable = false)
    private Boolean requiereVerificacionEdad = true;
    
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
    
    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.creadoEn = OffsetDateTime.now();
        this.actualizadoEn = OffsetDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = OffsetDateTime.now();
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
    
    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }
    
    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    public OffsetDateTime getActualizadoEn() {
        return actualizadoEn;
    }
    
    public void setActualizadoEn(OffsetDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
