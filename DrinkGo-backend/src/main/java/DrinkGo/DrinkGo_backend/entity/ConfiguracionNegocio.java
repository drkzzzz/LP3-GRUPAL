package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad ConfiguracionNegocio - Parámetros de configuración por negocio
 * Tabla: configuracion_negocio
 * RF-ADM-022
 */
@Entity
@Table(name = "configuracion_negocio", uniqueConstraints = @UniqueConstraint(columnNames = { "negocio_id",
        "clave_configuracion" }))
public class ConfiguracionNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "clave_configuracion", nullable = false, length = 150)
    private String claveConfiguracion;

    @Column(name = "valor_configuracion", nullable = false, columnDefinition = "TEXT")
    private String valorConfiguracion;

    @Column(name = "tipo_valor", nullable = false, length = 20)
    private String tipoValor = "texto"; // texto, numero, booleano, json, fecha

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria = "general";

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
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

    public String getClaveConfiguracion() {
        return claveConfiguracion;
    }

    public void setClaveConfiguracion(String claveConfiguracion) {
        this.claveConfiguracion = claveConfiguracion;
    }

    public String getValorConfiguracion() {
        return valorConfiguracion;
    }

    public void setValorConfiguracion(String valorConfiguracion) {
        this.valorConfiguracion = valorConfiguracion;
    }

    public String getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(String tipoValor) {
        this.tipoValor = tipoValor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
