package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos_facturacion")
@SQLDelete(sql = "UPDATE documentos_facturacion SET esta_activo = 0, eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "serieFacturacionId", "tipoDocumento", "numeroDocumento", "clienteId",
        "ventaId", "pedidoId", "fechaEmision", "fechaVencimiento", "subtotal", "impuestos", "total",
        "estadoDocumento", "modoEmision", "fechaEnvio", "intentosEnvio", "codigoRespuestaSunat",
        "hashSunat", "respuestaSunat", "xmlDocumento", "pdfUrl", "usuarioId", "estaActivo",
        "creadoEn", "actualizadoEn", "eliminadoEn" })
public class DocumentosFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_facturacion_id", nullable = false)
    private SeriesFacturacion serieFacturacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true)
    private String numeroDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Clientes cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Ventas venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_documento", nullable = false)
    private EstadoDocumento estadoDocumento = EstadoDocumento.borrador;

    /** Modo de emisión: LOCAL (sin SUNAT) o PSE (facturación electrónica). */
    @Enumerated(EnumType.STRING)
    @Column(name = "modo_emision")
    private ModoEmision modoEmision = ModoEmision.LOCAL;

    /** Fecha/hora en que se envió al PSE/SUNAT. */
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "hash_sunat")
    private String hashSunat;

    @Column(name = "respuesta_sunat", columnDefinition = "TEXT")
    private String respuestaSunat;

    @Column(name = "xml_documento", columnDefinition = "TEXT")
    private String xmlDocumento;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    /** Número de intentos de envío al PSE/SUNAT. */
    @Column(name = "intentos_envio")
    private Integer intentosEnvio = 0;

    /** Código de respuesta SUNAT (0 = OK, 2001 = error RUC, 2800 = estructura, etc.). */
    @Column(name = "codigo_respuesta_sunat", length = 10)
    private String codigoRespuestaSunat;

    @Column(name = "motivo_anulacion", length = 300)
    private String motivoAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    public enum TipoDocumento {
        boleta, factura, nota_credito, nota_debito
    }

    public enum EstadoDocumento {
        borrador, pendiente_envio, enviado, aceptado, observado, rechazado, anulado
    }

    public enum ModoEmision {
        LOCAL, PSE
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

    @JsonIgnore
    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    @JsonProperty("negocioId")
    public Long getNegocioId() {
        return negocio != null ? negocio.getId() : null;
    }

    @JsonIgnore
    public SeriesFacturacion getSerieFacturacion() {
        return serieFacturacion;
    }

    public void setSerieFacturacion(SeriesFacturacion serieFacturacion) {
        this.serieFacturacion = serieFacturacion;
    }

    @JsonProperty("serieFacturacionId")
    public Long getSerieFacturacionId() {
        return serieFacturacion != null ? serieFacturacion.getId() : null;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    @JsonIgnore
    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    @JsonProperty("clienteId")
    public Long getClienteId() {
        return cliente != null ? cliente.getId() : null;
    }

    @JsonProperty("clienteNombre")
    public String getClienteNombre() {
        if (cliente == null) return null;
        if (cliente.getRazonSocial() != null && !cliente.getRazonSocial().isBlank())
            return cliente.getRazonSocial();
        return (cliente.getNombres() != null ? cliente.getNombres() : "")
             + (cliente.getApellidos() != null ? " " + cliente.getApellidos() : "");
    }

    @JsonProperty("clienteNumeroDocumento")
    public String getClienteNumeroDocumento() {
        return cliente != null ? cliente.getNumeroDocumento() : null;
    }

    @JsonIgnore
    public Ventas getVenta() {
        return venta;
    }

    public void setVenta(Ventas venta) {
        this.venta = venta;
    }

    @JsonProperty("ventaId")
    public Long getVentaId() {
        return venta != null ? venta.getId() : null;
    }

    /**
     * Expone el ID de la caja registradora asociada a la venta
     * (venta → sesionCaja → caja → id).
     * Permite al frontend filtrar comprobantes por caja del cajero.
     */
    @JsonProperty("ventaCajaId")
    public Long getVentaCajaId() {
        if (venta == null) return null;
        SesionesCaja sc = venta.getSesionCaja();
        if (sc == null) return null;
        CajasRegistradoras c = sc.getCaja();
        return c != null ? c.getId() : null;
    }

    @JsonIgnore
    public Pedidos getPedido() {
        return pedido;
    }

    public void setPedido(Pedidos pedido) {
        this.pedido = pedido;
    }

    @JsonProperty("pedidoId")
    public Long getPedidoId() {
        return pedido != null ? pedido.getId() : null;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoDocumento getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(EstadoDocumento estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public ModoEmision getModoEmision() {
        return modoEmision;
    }

    public void setModoEmision(ModoEmision modoEmision) {
        this.modoEmision = modoEmision;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getHashSunat() {
        return hashSunat;
    }

    public void setHashSunat(String hashSunat) {
        this.hashSunat = hashSunat;
    }

    public String getRespuestaSunat() {
        return respuestaSunat;
    }

    public void setRespuestaSunat(String respuestaSunat) {
        this.respuestaSunat = respuestaSunat;
    }

    public String getXmlDocumento() {
        return xmlDocumento;
    }

    public void setXmlDocumento(String xmlDocumento) {
        this.xmlDocumento = xmlDocumento;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public Integer getIntentosEnvio() {
        return intentosEnvio != null ? intentosEnvio : 0;
    }

    public void setIntentosEnvio(Integer intentosEnvio) {
        this.intentosEnvio = intentosEnvio;
    }

    public String getCodigoRespuestaSunat() {
        return codigoRespuestaSunat;
    }

    public void setCodigoRespuestaSunat(String codigoRespuestaSunat) {
        this.codigoRespuestaSunat = codigoRespuestaSunat;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    @JsonIgnore
    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    @JsonProperty("usuarioId")
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
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

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void setEliminadoEn(LocalDateTime eliminadoEn) {
        this.eliminadoEn = eliminadoEn;
    }

    @Override
    public String toString() {
        return "DocumentosFacturacion [id=" + id + ", tipoDocumento=" + tipoDocumento + ", numeroDocumento="
                + numeroDocumento + ", total=" + total + ", estadoDocumento=" + estadoDocumento + ", estaActivo="
                + estaActivo + "]";
    }
}
