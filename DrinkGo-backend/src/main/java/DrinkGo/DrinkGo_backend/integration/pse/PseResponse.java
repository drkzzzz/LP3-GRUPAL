package DrinkGo.DrinkGo_backend.integration.pse;

/**
 * Resultado del envío de un documento electrónico a SUNAT vía PSE.
 * Estructura desacoplada para permitir intercambiar implementaciones de PSE.
 */
public class PseResponse {

    private boolean exito;
    private String estado;           // aceptado, observado, rechazado
    private String codigoRespuesta;  // Código SUNAT (0 = aceptado, etc.)
    private String mensajeRespuesta;
    private String hashDocumento;
    private String urlXml;
    private String urlCdr;
    private String ticketSunat;

    public PseResponse() {
    }

    public PseResponse(boolean exito, String estado, String codigoRespuesta,
                        String mensajeRespuesta, String hashDocumento,
                        String urlXml, String urlCdr, String ticketSunat) {
        this.exito = exito;
        this.estado = estado;
        this.codigoRespuesta = codigoRespuesta;
        this.mensajeRespuesta = mensajeRespuesta;
        this.hashDocumento = hashDocumento;
        this.urlXml = urlXml;
        this.urlCdr = urlCdr;
        this.ticketSunat = ticketSunat;
    }

    // --- Getters y Setters ---

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCodigoRespuesta() { return codigoRespuesta; }
    public void setCodigoRespuesta(String codigoRespuesta) { this.codigoRespuesta = codigoRespuesta; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }
    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public String getHashDocumento() { return hashDocumento; }
    public void setHashDocumento(String hashDocumento) { this.hashDocumento = hashDocumento; }

    public String getUrlXml() { return urlXml; }
    public void setUrlXml(String urlXml) { this.urlXml = urlXml; }

    public String getUrlCdr() { return urlCdr; }
    public void setUrlCdr(String urlCdr) { this.urlCdr = urlCdr; }

    public String getTicketSunat() { return ticketSunat; }
    public void setTicketSunat(String ticketSunat) { this.ticketSunat = ticketSunat; }
}
