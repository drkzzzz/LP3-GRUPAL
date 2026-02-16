package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad RestriccionVentaAlcohol - Restricciones de venta de alcohol
 * Tabla: restricciones_venta_alcohol
 * RF-ADM-008
 */
@Entity
@Table(name = "restricciones_venta_alcohol")
public class RestriccionVentaAlcohol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sede_id")
    private Long sedeId; // NULL aplica a todo el negocio

    @Column(name = "tipo_restriccion", nullable = false, length = 50)
    private String tipoRestriccion; // verificacion_edad, restriccion_horaria, restriccion_dia, evento_especial

    @Column(name = "edad_minima")
    private Integer edadMinima = 18;

    @Column(name = "hora_permitida_desde")
    private LocalTime horaPermitidaDesde;

    @Column(name = "hora_permitida_hasta")
    private LocalTime horaPermitidaHasta;

    @Column(name = "dias_restringidos", columnDefinition = "JSON")
    private String diasRestringidos; // Array de días restringidos [0,6]

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

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

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public String getTipoRestriccion() {
        return tipoRestriccion;
    }

    public void setTipoRestriccion(String tipoRestriccion) {
        this.tipoRestriccion = tipoRestriccion;
    }

    public Integer getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(Integer edadMinima) {
        this.edadMinima = edadMinima;
    }

    public LocalTime getHoraPermitidaDesde() {
        return horaPermitidaDesde;
    }

    public void setHoraPermitidaDesde(LocalTime horaPermitidaDesde) {
        this.horaPermitidaDesde = horaPermitidaDesde;
    }

    public LocalTime getHoraPermitidaHasta() {
        return horaPermitidaHasta;
    }

    public void setHoraPermitidaHasta(LocalTime horaPermitidaHasta) {
        this.horaPermitidaHasta = horaPermitidaHasta;
    }

    public String getDiasRestringidos() {
        return diasRestringidos;
    }

    public void setDiasRestringidos(String diasRestringidos) {
        this.diasRestringidos = diasRestringidos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
