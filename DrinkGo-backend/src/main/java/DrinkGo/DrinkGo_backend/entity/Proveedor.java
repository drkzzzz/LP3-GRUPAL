package DrinkGo.DrinkGo_backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Proveedor - Mapea la tabla 'proveedores' de drinkgo_database.sql.
 * Bloque 6: Proveedores y Compras (RF-COM-001..003).
 * Borrado lógico: campo esta_activo = 0 (soft delete).
 */
@Entity
@Table(name = "proveedores")
@SQLDelete(sql = "UPDATE proveedores SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "direccion", length = 300)
    private String direccion;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "rubro", columnDefinition = "TEXT")
    private String rubro;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}
