package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de referencia para negocios (tenants).
 * Solo se mapean los campos necesarios para validación de seguridad multi-tenant.
 * NO se implementa lógica de otro bloque.
 */
@Entity
@Table(name = "negocios")
public class Negocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "nombre_comercial")
    private String nombreComercial;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }
}
