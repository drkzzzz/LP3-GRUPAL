package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad ConfiguracionGlobalPlataforma - Mapea la tabla 'configuracion_global_plataforma'.
 * Parámetros globales de la plataforma (RF-CGL-001).
 */
@Entity
@Table(name = "configuracion_global_plataforma")
public class ConfiguracionGlobalPlataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave_configuracion", nullable = false, unique = true, length = 100)
    private String claveConfiguracion;

    @Column(name = "valor", columnDefinition = "TEXT")
    private String valor;

    @Column(name = "valor_configuracion", columnDefinition = "TEXT", nullable = false)
    private String valorConfiguracion;

    @Column(name = "tipo_dato", nullable = false, length = 50)
    private String tipoDato;
    
    @Column(name = "tipo_valor", nullable = false, length = 50)
    private String tipoValor = "texto";

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "es_publica", nullable = false)
    private Boolean esPublica = false;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaveConfiguracion() {
        return claveConfiguracion;
    }

    public void setClaveConfiguracion(String claveConfiguracion) {
        this.claveConfiguracion = claveConfiguracion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(String tipoValor) {
        this.tipoValor = tipoValor;
    }

    public String getValorConfiguracion() {
        return valorConfiguracion;
    }

    public void setValorConfiguracion(String valorConfiguracion) {
        this.valorConfiguracion = valorConfiguracion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsPublica() {
        return esPublica;
    }

    public void setEsPublica(Boolean esPublica) {
        this.esPublica = esPublica;
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
