package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad Notificacion - Notificaciones del sistema
 * Tabla: notificaciones
 * RF-ADM-024
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id")
    private Long negocioId;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_plataforma_id")
    private Long usuarioPlataformaId;

    @Column(name = "plantilla_id")
    private Long plantillaId;

    @Column(name = "titulo", nullable = false, length = 300)
    private String titulo;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "canal", nullable = false, length = 20)
    private String canal = "en_app"; // email, sms, push, en_app, whatsapp

    @Column(name = "datos_json", columnDefinition = "JSON")
    private String datosJson;

    @Column(name = "esta_leido", nullable = false)
    private Boolean estaLeido = false;

    @Column(name = "leido_en")
    private LocalDateTime leidoEn;

    @Column(name = "enviado_en")
    private LocalDateTime enviadoEn;

    @Column(name = "estado_entrega", nullable = false, length = 20)
    private String estadoEntrega = "pendiente"; // pendiente, enviada, entregada, fallida

    @Column(name = "prioridad", nullable = false, length = 20)
    private String prioridad = "normal"; // baja, normal, alta, urgente

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
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

    public Long getUsuarioPlataformaId() {
        return usuarioPlataformaId;
    }

    public void setUsuarioPlataformaId(Long usuarioPlataformaId) {
        this.usuarioPlataformaId = usuarioPlataformaId;
    }

    public Long getPlantillaId() {
        return plantillaId;
    }

    public void setPlantillaId(Long plantillaId) {
        this.plantillaId = plantillaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getDatosJson() {
        return datosJson;
    }

    public void setDatosJson(String datosJson) {
        this.datosJson = datosJson;
    }

    public Boolean getEstaLeido() {
        return estaLeido;
    }

    public void setEstaLeido(Boolean estaLeido) {
        this.estaLeido = estaLeido;
    }

    public LocalDateTime getLeidoEn() {
        return leidoEn;
    }

    public void setLeidoEn(LocalDateTime leidoEn) {
        this.leidoEn = leidoEn;
    }

    public LocalDateTime getEnviadoEn() {
        return enviadoEn;
    }

    public void setEnviadoEn(LocalDateTime enviadoEn) {
        this.enviadoEn = enviadoEn;
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
