package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "planes_suscripcion")
@SQLDelete(sql = "UPDATE planes_suscripcion SET esta_activo = 0 WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "nombre", "descripcion", "precio", "moneda", "periodoFacturacion",
        "maxSedes", "maxUsuarios", "maxProductos", "maxAlmacenesPorSede", "permitePos",
        "permiteTiendaOnline", "permiteDelivery", "permiteMesas", "permiteFacturacionElectronica",
        "permiteMultiAlmacen", "permiteReportesAvanzados", "permiteAccesoApi", "funcionalidadesJson",
        "estaActivo", "orden", "creadoEn", "actualizadoEn" })
public class PlanesSuscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_facturacion")
    private PeriodoFacturacion periodoFacturacion;

    @Column(name = "max_sedes")
    private Integer maxSedes;

    @Column(name = "max_usuarios")
    private Integer maxUsuarios;

    @Column(name = "max_productos")
    private Integer maxProductos;

    @Column(name = "max_almacenes_por_sede")
    private Integer maxAlmacenesPorSede;

    @Column(name = "permite_pos")
    private Boolean permitePos;

    @Column(name = "permite_tienda_online")
    private Boolean permiteTiendaOnline;

    @Column(name = "permite_delivery")
    private Boolean permiteDelivery;

    @Column(name = "permite_mesas")
    private Boolean permiteMesas;

    @Column(name = "permite_facturacion_electronica")
    private Boolean permiteFacturacionElectronica;

    @Column(name = "permite_multi_almacen")
    private Boolean permiteMultiAlmacen;

    @Column(name = "permite_reportes_avanzados")
    private Boolean permiteReportesAvanzados;

    @Column(name = "permite_acceso_api")
    private Boolean permiteAccesoApi;

    @Column(name = "funcionalidades_json", columnDefinition = "JSON")
    private String funcionalidadesJson;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    private Integer orden = 0;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum PeriodoFacturacion {
        mensual, anual
    }

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return "PlanesSuscripcion [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", precio="
                + precio + ", moneda=" + moneda + ", periodoFacturacion=" + periodoFacturacion + ", maxSedes="
                + maxSedes + ", maxUsuarios=" + maxUsuarios + ", maxProductos=" + maxProductos
                + ", maxAlmacenesPorSede=" + maxAlmacenesPorSede + ", permitePos=" + permitePos
                + ", permiteTiendaOnline=" + permiteTiendaOnline + ", permiteDelivery=" + permiteDelivery
                + ", permiteMesas=" + permiteMesas + ", permiteFacturacionElectronica=" + permiteFacturacionElectronica
                + ", permiteMultiAlmacen=" + permiteMultiAlmacen + ", permiteReportesAvanzados="
                + permiteReportesAvanzados + ", permiteAccesoApi=" + permiteAccesoApi + ", funcionalidadesJson="
                + funcionalidadesJson + ", estaActivo=" + estaActivo + ", orden=" + orden + ", creadoEn=" + creadoEn
                + ", actualizadoEn=" + actualizadoEn + "]";
    }
}
