package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad PlanSuscripcion - Mapea la tabla 'planes_suscripcion'.
 * Planes de suscripción de la plataforma (RF-PLT-002..005).
 */
@Entity
@Table(name = "planes_suscripcion")
@SQLDelete(sql = "UPDATE planes_suscripcion SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
public class PlanSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio = BigDecimal.ZERO;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "PEN";

    @Column(name = "periodo_facturacion", nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodoFacturacion periodoFacturacion = PeriodoFacturacion.mensual;

    @Column(name = "max_sedes", nullable = false)
    private Integer maxSedes = 1;

    @Column(name = "max_usuarios", nullable = false)
    private Integer maxUsuarios = 5;

    @Column(name = "max_productos", nullable = false)
    private Integer maxProductos = 500;

    @Column(name = "max_almacenes_por_sede", nullable = false)
    private Integer maxAlmacenesPorSede = 2;

    @Column(name = "permite_pos", nullable = false)
    private Boolean permitePos = true;

    @Column(name = "permite_tienda_online", nullable = false)
    private Boolean permiteTiendaOnline = false;

    @Column(name = "permite_delivery", nullable = false)
    private Boolean permiteDelivery = false;

    @Column(name = "permite_mesas", nullable = false)
    private Boolean permiteMesas = false;

    @Column(name = "permite_facturacion_electronica", nullable = false)
    private Boolean permiteFacturacionElectronica = false;

    @Column(name = "permite_multi_almacen", nullable = false)
    private Boolean permiteMultiAlmacen = false;

    @Column(name = "permite_reportes_avanzados", nullable = false)
    private Boolean permiteReportesAvanzados = false;

    @Column(name = "permite_acceso_api", nullable = false)
    private Boolean permiteAccesoApi = false;

    @Column(name = "funcionalidades_json", columnDefinition = "JSON")
    private String funcionalidadesJson;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    public enum PeriodoFacturacion {
        mensual, anual
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public PeriodoFacturacion getPeriodoFacturacion() {
        return periodoFacturacion;
    }

    public void setPeriodoFacturacion(PeriodoFacturacion periodoFacturacion) {
        this.periodoFacturacion = periodoFacturacion;
    }

    public Integer getMaxSedes() {
        return maxSedes;
    }

    public void setMaxSedes(Integer maxSedes) {
        this.maxSedes = maxSedes;
    }

    public Integer getMaxUsuarios() {
        return maxUsuarios;
    }

    public void setMaxUsuarios(Integer maxUsuarios) {
        this.maxUsuarios = maxUsuarios;
    }

    public Integer getMaxProductos() {
        return maxProductos;
    }

    public void setMaxProductos(Integer maxProductos) {
        this.maxProductos = maxProductos;
    }

    public Integer getMaxAlmacenesPorSede() {
        return maxAlmacenesPorSede;
    }

    public void setMaxAlmacenesPorSede(Integer maxAlmacenesPorSede) {
        this.maxAlmacenesPorSede = maxAlmacenesPorSede;
    }

    public Boolean getPermitePos() {
        return permitePos;
    }

    public void setPermitePos(Boolean permitePos) {
        this.permitePos = permitePos;
    }

    public Boolean getPermiteTiendaOnline() {
        return permiteTiendaOnline;
    }

    public void setPermiteTiendaOnline(Boolean permiteTiendaOnline) {
        this.permiteTiendaOnline = permiteTiendaOnline;
    }

    public Boolean getPermiteDelivery() {
        return permiteDelivery;
    }

    public void setPermiteDelivery(Boolean permiteDelivery) {
        this.permiteDelivery = permiteDelivery;
    }

    public Boolean getPermiteMesas() {
        return permiteMesas;
    }

    public void setPermiteMesas(Boolean permiteMesas) {
        this.permiteMesas = permiteMesas;
    }

    public Boolean getPermiteFacturacionElectronica() {
        return permiteFacturacionElectronica;
    }

    public void setPermiteFacturacionElectronica(Boolean permiteFacturacionElectronica) {
        this.permiteFacturacionElectronica = permiteFacturacionElectronica;
    }

    public Boolean getPermiteMultiAlmacen() {
        return permiteMultiAlmacen;
    }

    public void setPermiteMultiAlmacen(Boolean permiteMultiAlmacen) {
        this.permiteMultiAlmacen = permiteMultiAlmacen;
    }

    public Boolean getPermiteReportesAvanzados() {
        return permiteReportesAvanzados;
    }

    public void setPermiteReportesAvanzados(Boolean permiteReportesAvanzados) {
        this.permiteReportesAvanzados = permiteReportesAvanzados;
    }

    public Boolean getPermiteAccesoApi() {
        return permiteAccesoApi;
    }

    public void setPermiteAccesoApi(Boolean permiteAccesoApi) {
        this.permiteAccesoApi = permiteAccesoApi;
    }

    public String getFuncionalidadesJson() {
        return funcionalidadesJson;
    }

    public void setFuncionalidadesJson(String funcionalidadesJson) {
        this.funcionalidadesJson = funcionalidadesJson;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
