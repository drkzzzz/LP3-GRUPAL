package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity para documentos de facturación electrónica.
 * Tabla: documentos_facturacion
 * RF-FACT-001..005: Facturación electrónica SUNAT
 */
@Entity
@Table(name = "documentos_facturacion")
public class DocumentoFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "sede_id", nullable = false)
    private Long sedeId;

    @Column(name = "serie_id", nullable = false)
    private Long serieId;

    @Column(name = "venta_id")
    private Long ventaId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "tipo_documento", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Column(name = "serie", nullable = false, length = 10)
    private String serie;

    @Column(name = "numero_correlativo", nullable = false)
    private Integer numeroCorrelativo;

    @Column(name = "numero_completo", nullable = false, length = 30)
    private String numeroCompleto;

    // --- Snapshot Emisor ---

    @Column(name = "ruc_emisor", nullable = false, length = 20)
    private String rucEmisor;

    @Column(name = "razon_social_emisor", nullable = false, length = 200)
    private String razonSocialEmisor;

    @Column(name = "direccion_emisor", length = 300)
    private String direccionEmisor;

    // --- Receptor ---

    @Column(name = "tipo_documento_receptor", length = 5)
    private String tipoDocumentoReceptor;

    @Column(name = "numero_documento_receptor", length = 20)
    private String numeroDocumentoReceptor;

    @Column(name = "nombre_receptor", nullable = false, length = 200)
    private String nombreReceptor;

    @Column(name = "direccion_receptor", length = 300)
    private String direccionReceptor;

    @Column(name = "email_receptor", length = 150)
    private String emailReceptor;

    // --- Montos ---

    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "total_descuento", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalDescuento = BigDecimal.ZERO;

    @Column(name = "total_gravado", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalGravado;

    @Column(name = "total_igv", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalIgv = BigDecimal.ZERO;

    @Column(name = "total_isc", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalIsc = BigDecimal.ZERO;

    @Column(name = "tasa_igv", precision = 5, scale = 2)
    private BigDecimal tasaIgv;

    @Column(name = "total_otros_impuestos", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalOtrosImpuestos = BigDecimal.ZERO;

    @Column(name = "total", precision = 12, scale = 2, nullable = false)
    private BigDecimal total;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "PEN";

    @Column(name = "tipo_cambio", precision = 10, scale = 6)
    private BigDecimal tipoCambio;

    // --- SUNAT ---

    @Column(name = "estado_sunat", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoSunat estadoSunat = EstadoSunat.pendiente;

    @Column(name = "ticket_sunat", length = 100)
    private String ticketSunat;

    @Column(name = "codigo_respuesta_sunat", length = 10)
    private String codigoRespuestaSunat;

    @Column(name = "mensaje_respuesta_sunat", columnDefinition = "TEXT")
    private String mensajeRespuestaSunat;

    @Column(name = "hash_sunat", length = 255)
    private String hashSunat;

    @Column(name = "url_xml_sunat", length = 500)
    private String urlXmlSunat;

    @Column(name = "url_cdr_sunat", length = 500)
    private String urlCdrSunat;

    @Column(name = "url_pdf_sunat", length = 500)
    private String urlPdfSunat;

    @Column(name = "enviado_sunat_en")
    private LocalDateTime enviadoSunatEn;

    @Column(name = "aceptado_sunat_en")
    private LocalDateTime aceptadoSunatEn;

    // --- Referencia (notas crédito/débito) ---

    @Column(name = "documento_referenciado_id")
    private Long documentoReferenciadoId;

    @Column(name = "motivo_referencia", length = 300)
    private String motivoReferencia;

    // --- Fechas/Estado ---

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoDocumento estado = EstadoDocumento.borrador;

    @Column(name = "creado_por")
    private Long creadoPor;

    @Column(name = "anulado_por")
    private Long anuladoPor;

    @Column(name = "anulado_en")
    private LocalDateTime anuladoEn;

    @Column(name = "motivo_anulacion", length = 300)
    private String motivoAnulacion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private LocalDateTime actualizadoEn;

    // --- ENUMS ---

    public enum TipoDocumento {
        boleta,
        factura,
        nota_credito,
        nota_debito,
        guia_remision
    }

    public enum EstadoSunat {
        pendiente,
        enviado,
        aceptado,
        rechazado,
        anulado,
        error
    }

    public enum EstadoDocumento {
        borrador,
        emitido,
        enviado,
        aceptado,
        anulado,
        error
    }

    // --- Constructores ---

    public DocumentoFacturacion() {
    }

    // --- PrePersist & PreUpdate ---

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNegocioId() { return negocioId; }
    public void setNegocioId(Long negocioId) { this.negocioId = negocioId; }

    public Long getSedeId() { return sedeId; }
    public void setSedeId(Long sedeId) { this.sedeId = sedeId; }

    public Long getSerieId() { return serieId; }
    public void setSerieId(Long serieId) { this.serieId = serieId; }

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public Integer getNumeroCorrelativo() { return numeroCorrelativo; }
    public void setNumeroCorrelativo(Integer numeroCorrelativo) { this.numeroCorrelativo = numeroCorrelativo; }

    public String getNumeroCompleto() { return numeroCompleto; }
    public void setNumeroCompleto(String numeroCompleto) { this.numeroCompleto = numeroCompleto; }

    public String getRucEmisor() { return rucEmisor; }
    public void setRucEmisor(String rucEmisor) { this.rucEmisor = rucEmisor; }

    public String getRazonSocialEmisor() { return razonSocialEmisor; }
    public void setRazonSocialEmisor(String razonSocialEmisor) { this.razonSocialEmisor = razonSocialEmisor; }

    public String getDireccionEmisor() { return direccionEmisor; }
    public void setDireccionEmisor(String direccionEmisor) { this.direccionEmisor = direccionEmisor; }

    public String getTipoDocumentoReceptor() { return tipoDocumentoReceptor; }
    public void setTipoDocumentoReceptor(String tipoDocumentoReceptor) { this.tipoDocumentoReceptor = tipoDocumentoReceptor; }

    public String getNumeroDocumentoReceptor() { return numeroDocumentoReceptor; }
    public void setNumeroDocumentoReceptor(String numeroDocumentoReceptor) { this.numeroDocumentoReceptor = numeroDocumentoReceptor; }

    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }

    public String getDireccionReceptor() { return direccionReceptor; }
    public void setDireccionReceptor(String direccionReceptor) { this.direccionReceptor = direccionReceptor; }

    public String getEmailReceptor() { return emailReceptor; }
    public void setEmailReceptor(String emailReceptor) { this.emailReceptor = emailReceptor; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTotalDescuento() { return totalDescuento; }
    public void setTotalDescuento(BigDecimal totalDescuento) { this.totalDescuento = totalDescuento; }

    public BigDecimal getTotalGravado() { return totalGravado; }
    public void setTotalGravado(BigDecimal totalGravado) { this.totalGravado = totalGravado; }

    public BigDecimal getTotalIgv() { return totalIgv; }
    public void setTotalIgv(BigDecimal totalIgv) { this.totalIgv = totalIgv; }

    public BigDecimal getTotalIsc() { return totalIsc; }
    public void setTotalIsc(BigDecimal totalIsc) { this.totalIsc = totalIsc; }

    public BigDecimal getTasaIgv() { return tasaIgv; }
    public void setTasaIgv(BigDecimal tasaIgv) { this.tasaIgv = tasaIgv; }

    public BigDecimal getTotalOtrosImpuestos() { return totalOtrosImpuestos; }
    public void setTotalOtrosImpuestos(BigDecimal totalOtrosImpuestos) { this.totalOtrosImpuestos = totalOtrosImpuestos; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(BigDecimal tipoCambio) { this.tipoCambio = tipoCambio; }

    public EstadoSunat getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(EstadoSunat estadoSunat) { this.estadoSunat = estadoSunat; }

    public String getTicketSunat() { return ticketSunat; }
    public void setTicketSunat(String ticketSunat) { this.ticketSunat = ticketSunat; }

    public String getCodigoRespuestaSunat() { return codigoRespuestaSunat; }
    public void setCodigoRespuestaSunat(String codigoRespuestaSunat) { this.codigoRespuestaSunat = codigoRespuestaSunat; }

    public String getMensajeRespuestaSunat() { return mensajeRespuestaSunat; }
    public void setMensajeRespuestaSunat(String mensajeRespuestaSunat) { this.mensajeRespuestaSunat = mensajeRespuestaSunat; }

    public String getHashSunat() { return hashSunat; }
    public void setHashSunat(String hashSunat) { this.hashSunat = hashSunat; }

    public String getUrlXmlSunat() { return urlXmlSunat; }
    public void setUrlXmlSunat(String urlXmlSunat) { this.urlXmlSunat = urlXmlSunat; }

    public String getUrlCdrSunat() { return urlCdrSunat; }
    public void setUrlCdrSunat(String urlCdrSunat) { this.urlCdrSunat = urlCdrSunat; }

    public String getUrlPdfSunat() { return urlPdfSunat; }
    public void setUrlPdfSunat(String urlPdfSunat) { this.urlPdfSunat = urlPdfSunat; }

    public LocalDateTime getEnviadoSunatEn() { return enviadoSunatEn; }
    public void setEnviadoSunatEn(LocalDateTime enviadoSunatEn) { this.enviadoSunatEn = enviadoSunatEn; }

    public LocalDateTime getAceptadoSunatEn() { return aceptadoSunatEn; }
    public void setAceptadoSunatEn(LocalDateTime aceptadoSunatEn) { this.aceptadoSunatEn = aceptadoSunatEn; }

    public Long getDocumentoReferenciadoId() { return documentoReferenciadoId; }
    public void setDocumentoReferenciadoId(Long documentoReferenciadoId) { this.documentoReferenciadoId = documentoReferenciadoId; }

    public String getMotivoReferencia() { return motivoReferencia; }
    public void setMotivoReferencia(String motivoReferencia) { this.motivoReferencia = motivoReferencia; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public EstadoDocumento getEstado() { return estado; }
    public void setEstado(EstadoDocumento estado) { this.estado = estado; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public Long getAnuladoPor() { return anuladoPor; }
    public void setAnuladoPor(Long anuladoPor) { this.anuladoPor = anuladoPor; }

    public LocalDateTime getAnuladoEn() { return anuladoEn; }
    public void setAnuladoEn(LocalDateTime anuladoEn) { this.anuladoEn = anuladoEn; }

    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
