package DrinkGo.DrinkGo_backend.dto;

/**
 * Response para el resultado del envío simulado a SUNAT vía PSE.
 */
public class EnvioSunatResponse {

    private Long documentoId;
    private String numeroCompleto;
    private String estadoSunat;
    private String codigoRespuesta;
    private String mensajeRespuesta;
    private String hashSunat;
    private String urlXml;
    private String urlCdr;
    private String mensaje;

    // --- Getters y Setters ---

    public Long getDocumentoId() { return documentoId; }
    public void setDocumentoId(Long documentoId) { this.documentoId = documentoId; }

    public String getNumeroCompleto() { return numeroCompleto; }
    public void setNumeroCompleto(String numeroCompleto) { this.numeroCompleto = numeroCompleto; }

    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }

    public String getCodigoRespuesta() { return codigoRespuesta; }
    public void setCodigoRespuesta(String codigoRespuesta) { this.codigoRespuesta = codigoRespuesta; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }
    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public String getHashSunat() { return hashSunat; }
    public void setHashSunat(String hashSunat) { this.hashSunat = hashSunat; }

    public String getUrlXml() { return urlXml; }
    public void setUrlXml(String urlXml) { this.urlXml = urlXml; }

    public String getUrlCdr() { return urlCdr; }
    public void setUrlCdr(String urlCdr) { this.urlCdr = urlCdr; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
