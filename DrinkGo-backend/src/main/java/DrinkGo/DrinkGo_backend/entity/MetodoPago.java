package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad MetodoPago - Métodos de pago por negocio
 * Tabla: metodos_pago
 * RF-ADM-025
 */
@Entity
@Table(name = "metodos_pago", uniqueConstraints = @UniqueConstraint(columnNames = { "negocio_id", "codigo" }))
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo; // efectivo, tarjeta_credito, tarjeta_debito, transferencia_bancaria,
                         // billetera_digital, yape, plin, qr, otro

    @Column(name = "proveedor", length = 100)
    private String proveedor; // Proveedor: Visa, Mastercard, Yape, etc.

    @Column(name = "configuracion_json", columnDefinition = "JSON")
    private String configuracionJson; // Configuración del método (llaves, endpoints)

    @Column(name = "requiere_referencia", nullable = false)
    private Boolean requiereReferencia = false;

    @Column(name = "requiere_aprobacion", nullable = false)
    private Boolean requiereAprobacion = false;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "disponible_pos", nullable = false)
    private Boolean disponiblePos = true;

    @Column(name = "disponible_tienda_online", nullable = false)
    private Boolean disponibleTiendaOnline = false;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getConfiguracionJson() {
        return configuracionJson;
    }

    public void setConfiguracionJson(String configuracionJson) {
        this.configuracionJson = configuracionJson;
    }

    public Boolean getRequiereReferencia() {
        return requiereReferencia;
    }

    public void setRequiereReferencia(Boolean requiereReferencia) {
        this.requiereReferencia = requiereReferencia;
    }

    public Boolean getRequiereAprobacion() {
        return requiereAprobacion;
    }

    public void setRequiereAprobacion(Boolean requiereAprobacion) {
        this.requiereAprobacion = requiereAprobacion;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public Boolean getDisponiblePos() {
        return disponiblePos;
    }

    public void setDisponiblePos(Boolean disponiblePos) {
        this.disponiblePos = disponiblePos;
    }

    public Boolean getDisponibleTiendaOnline() {
        return disponibleTiendaOnline;
    }

    public void setDisponibleTiendaOnline(Boolean disponibleTiendaOnline) {
        this.disponibleTiendaOnline = disponibleTiendaOnline;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
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
