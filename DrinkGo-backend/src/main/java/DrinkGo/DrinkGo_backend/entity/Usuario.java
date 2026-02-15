package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de referencia para usuarios del negocio.
 * Utilizada para autenticación y validación de seguridad.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "negocio_id", nullable = false, insertable = false, updatable = false)
    private Long negocioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocio negocio;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "hash_contrasena", nullable = false)
    private String hashContrasena;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    @Column(name = "ultimo_acceso_en")
    private LocalDateTime ultimoAccesoEn;

    @Column(name = "ip_ultimo_acceso")
    private String ipUltimoAcceso;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public Long getNegocioId() { return negocioId; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public LocalDateTime getEliminadoEn() { return eliminadoEn; }
    public void setEliminadoEn(LocalDateTime eliminadoEn) { this.eliminadoEn = eliminadoEn; }

    public LocalDateTime getUltimoAccesoEn() { return ultimoAccesoEn; }
    public void setUltimoAccesoEn(LocalDateTime ultimoAccesoEn) { this.ultimoAccesoEn = ultimoAccesoEn; }

    public String getIpUltimoAcceso() { return ipUltimoAcceso; }
    public void setIpUltimoAcceso(String ipUltimoAcceso) { this.ipUltimoAcceso = ipUltimoAcceso; }
}
