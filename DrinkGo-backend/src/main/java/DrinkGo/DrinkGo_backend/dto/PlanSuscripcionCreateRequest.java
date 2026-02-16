package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;

/**
 * DTO para crear un nuevo plan de suscripción.
 */
public class PlanSuscripcionCreateRequest {

    private String nombre;
    private String slug;
    private String descripcion;
    private BigDecimal precio;
    private String moneda;
    private String periodoFacturacion; // "mensual" o "anual"
    private Integer maxSedes;
    private Integer maxUsuarios;
    private Integer maxProductos;
    private Integer maxAlmacenesPorSede;
    private Boolean permitePos;
    private Boolean permiteTiendaOnline;
    private Boolean permiteDelivery;
    private Boolean permiteMesas;
    private Boolean permiteFacturacionElectronica;
    private Boolean permiteMultiAlmacen;
    private Boolean permiteReportesAvanzados;
    private Boolean permiteAccesoApi;
    private String funcionalidadesJson;
    private Integer orden;

    // --- Getters y Setters ---

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

    public String getPeriodoFacturacion() {
        return periodoFacturacion;
    }

    public void setPeriodoFacturacion(String periodoFacturacion) {
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

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
