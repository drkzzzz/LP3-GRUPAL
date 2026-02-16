package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad HorarioEspecialSede - Horarios especiales/feriados por sede
 * Tabla: horarios_especiales_sede
 * RF-ADM-007
 */
@Entity
@Table(name = "horarios_especiales_sede", uniqueConstraints = @UniqueConstraint(columnNames = { "sede_id", "fecha" }))
public class HorarioEspecialSede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_apertura")
    private LocalTime horaApertura;

    @Column(name = "hora_cierre")
    private LocalTime horaCierre;

    @Column(name = "esta_cerrado", nullable = false)
    private Boolean estaCerrado = false;

    @Column(name = "motivo", length = 200)
    private String motivo;

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

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public Boolean getEstaCerrado() {
        return estaCerrado;
    }

    public void setEstaCerrado(Boolean estaCerrado) {
        this.estaCerrado = estaCerrado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
