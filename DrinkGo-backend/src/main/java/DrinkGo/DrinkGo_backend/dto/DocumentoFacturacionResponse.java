package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response completo para documentos de facturación.
 * Alineado con la tabla documentos_facturacion de la BD.
 */
public class DocumentoFacturacionResponse {

    private Long id;
    private Long negocioId;
    private Long sedeId;
    private Long serieId;
    private Long ventaId;
    private Long pedidoId;
    private String tipoDocumento;
    private String serie;
    private Integer numeroCorrelativo;
    private String numeroCompleto;

    // --- Receptor ---
    private String tipoDocumentoReceptor;
    private String numeroDocumentoReceptor;
    private String nombreReceptor;
    private String direccionReceptor;
    private String emailReceptor;

    // --- Emisor (datos del Negocio, obtenidos al consultar, NO almacenados en documento) ---
    private String rucEmisor;
    private String razonSocialEmisor;
    private String direccionEmisor;

    // --- Montos ---
    private BigDecimal subtotal;
    private BigDecimal totalDescuento;
    private BigDecimal totalGravado;
    private BigDecimal totalIgv;
    private BigDecimal totalOtrosImpuestos;
    private BigDecimal total;
    private String moneda;
    private BigDecimal tipoCambio;

    // --- SUNAT ---
    private String codigoTipoDocumentoSunat;
    private String estadoSunat;
    private String ticketSunat;
    private String codigoRespuestaSunat;
    private String mensajeRespuestaSunat;
    private String hashSunat;
    private String urlXmlSunat;
    private String urlCdrSunat;
    private String urlPdfSunat;
    private LocalDateTime enviadoSunatEn;
    private LocalDateTime aceptadoSunatEn;

    // --- Fechas/Estado ---
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String estado;
    private Long creadoPor;
    private Long anuladoPor;
    private LocalDateTime anuladoEn;
    private String motivoAnulacion;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // --- Detalles ---
    private List<DetalleDocumentoFacturacionResponse> detalles;

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

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public Integer getNumeroCorrelativo() { return numeroCorrelativo; }
    public void setNumeroCorrelativo(Integer numeroCorrelativo) { this.numeroCorrelativo = numeroCorrelativo; }

    public String getNumeroCompleto() { return numeroCompleto; }
    public void setNumeroCompleto(String numeroCompleto) { this.numeroCompleto = numeroCompleto; }

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

    public String getRucEmisor() { return rucEmisor; }
    public void setRucEmisor(String rucEmisor) { this.rucEmisor = rucEmisor; }

    public String getRazonSocialEmisor() { return razonSocialEmisor; }
    public void setRazonSocialEmisor(String razonSocialEmisor) { this.razonSocialEmisor = razonSocialEmisor; }

    public String getDireccionEmisor() { return direccionEmisor; }
    public void setDireccionEmisor(String direccionEmisor) { this.direccionEmisor = direccionEmisor; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTotalDescuento() { return totalDescuento; }
    public void setTotalDescuento(BigDecimal totalDescuento) { this.totalDescuento = totalDescuento; }

    public BigDecimal getTotalGravado() { return totalGravado; }
    public void setTotalGravado(BigDecimal totalGravado) { this.totalGravado = totalGravado; }

    public BigDecimal getTotalIgv() { return totalIgv; }
    public void setTotalIgv(BigDecimal totalIgv) { this.totalIgv = totalIgv; }

    public BigDecimal getTotalOtrosImpuestos() { return totalOtrosImpuestos; }
    public void setTotalOtrosImpuestos(BigDecimal totalOtrosImpuestos) { this.totalOtrosImpuestos = totalOtrosImpuestos; }

    public String getCodigoTipoDocumentoSunat() { return codigoTipoDocumentoSunat; }
    public void setCodigoTipoDocumentoSunat(String codigoTipoDocumentoSunat) { this.codigoTipoDocumentoSunat = codigoTipoDocumentoSunat; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public BigDecimal getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(BigDecimal tipoCambio) { this.tipoCambio = tipoCambio; }

    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }

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

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

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

    public List<DetalleDocumentoFacturacionResponse> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleDocumentoFacturacionResponse> detalles) { this.detalles = detalles; }
}
