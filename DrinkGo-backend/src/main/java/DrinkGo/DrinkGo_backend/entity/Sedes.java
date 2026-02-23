package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sedes")
@SQLDelete(sql = "UPDATE sedes SET esta_activo = 0, desactivado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "codigo", "nombre", "direccion", "ciudad", "departamento", "pais",
        "codigoPostal", "latitud", "longitud", "telefono", "usuarioGerenteId", "esPrincipal", "deliveryHabilitado",
        "recojoHabilitado", "horarioConfig", "calendarioEspecial", "configuracion", "estaActivo", "creadoEn",
        "actualizadoEn", "desactivadoEn" })
public class Sedes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    private String ciudad;

    private String departamento;

    private String pais = "PE";

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitud;

    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_gerente_id")
    private Usuarios usuarioGerente;

    @Column(name = "es_principal")
    private Boolean esPrincipal = false;

    @Column(name = "delivery_habilitado")
    private Boolean deliveryHabilitado = false;

    @Column(name = "recojo_habilitado")
    private Boolean recojoHabilitado = false;

    @Column(name = "horario_config", columnDefinition = "JSON")
    private String horarioConfig;

    @Column(name = "calendario_especial", columnDefinition = "JSON")
    private String calendarioEspecial;

    @Column(columnDefinition = "JSON")
    private String configuracion;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "desactivado_en")
    private LocalDateTime desactivadoEn;

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

    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuarios getUsuarioGerente() {
        return usuarioGerente;
    }

    public void setUsuarioGerente(Usuarios usuarioGerente) {
        this.usuarioGerente = usuarioGerente;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public Boolean getDeliveryHabilitado() {
        return deliveryHabilitado;
    }

    public void setDeliveryHabilitado(Boolean deliveryHabilitado) {
        this.deliveryHabilitado = deliveryHabilitado;
    }

    public Boolean getRecojoHabilitado() {
        return recojoHabilitado;
    }

    public void setRecojoHabilitado(Boolean recojoHabilitado) {
        this.recojoHabilitado = recojoHabilitado;
    }

    public String getHorarioConfig() {
        return horarioConfig;
    }

    public void setHorarioConfig(String horarioConfig) {
        this.horarioConfig = horarioConfig;
    }

    public String getCalendarioEspecial() {
        return calendarioEspecial;
    }

    public void setCalendarioEspecial(String calendarioEspecial) {
        this.calendarioEspecial = calendarioEspecial;
    }

    public String getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(String configuracion) {
        this.configuracion = configuracion;
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

    public LocalDateTime getDesactivadoEn() {
        return desactivadoEn;
    }

    public void setDesactivadoEn(LocalDateTime desactivadoEn) {
        this.desactivadoEn = desactivadoEn;
    }

    @Override
    public String toString() {
        return "Sedes [id=" + id + ", negocio=" + (negocio != null ? negocio.getId() : null) + ", codigo=" + codigo
                + ", nombre=" + nombre + ", direccion=" + direccion + ", ciudad=" + ciudad + ", departamento="
                + departamento + ", pais=" + pais + ", codigoPostal=" + codigoPostal + ", latitud=" + latitud
                + ", longitud=" + longitud + ", telefono=" + telefono + ", usuarioGerente="
                + (usuarioGerente != null ? usuarioGerente.getId() : null) + ", esPrincipal=" + esPrincipal
                + ", deliveryHabilitado=" + deliveryHabilitado + ", recojoHabilitado=" + recojoHabilitado
                + ", estaActivo=" + estaActivo + ", creadoEn=" + creadoEn + ", actualizadoEn=" + actualizadoEn
                + ", desactivadoEn=" + desactivadoEn + "]";
    }
}
