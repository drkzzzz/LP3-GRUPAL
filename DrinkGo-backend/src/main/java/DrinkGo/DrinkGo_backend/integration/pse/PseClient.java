package DrinkGo.DrinkGo_backend.integration.pse;

import DrinkGo.DrinkGo_backend.entity.DocumentoFacturacion;

/**
 * Interface desacoplada para el envío de documentos electrónicos a SUNAT.
 * 
 * DISEÑO:
 * - Esta interfaz define el contrato para cualquier proveedor PSE.
 * - Permite intercambiar la implementación sin modificar la lógica de negocio.
 * - En el futuro se puede crear PseNubefactClient, PseEfactClient, etc.
 * 
 * IMPLEMENTACIONES DISPONIBLES:
 * - PseSimulationClient: Simulación local (modo desarrollo/pruebas)
 * - (Futuro) PseNubefactClient: Integración real con Nubefact
 * - (Futuro) PseEfactClient: Integración real con Efact
 */
public interface PseClient {

    /**
     * Envía un documento de facturación a SUNAT a través del PSE.
     *
     * @param documento el documento de facturación a enviar
     * @return PseResponse con el resultado del envío
     */
    PseResponse enviarDocumento(DocumentoFacturacion documento);

    /**
     * Consulta el estado de un documento previamente enviado.
     *
     * @param ticketSunat el ticket de SUNAT para consulta
     * @return PseResponse con el estado actual
     */
    PseResponse consultarEstado(String ticketSunat);

    /**
     * Solicita la anulación de un documento ante SUNAT.
     *
     * @param documento el documento a anular
     * @param motivo el motivo de anulación
     * @return PseResponse con el resultado de la solicitud
     */
    PseResponse anularDocumento(DocumentoFacturacion documento, String motivo);

    /**
     * Retorna el nombre del proveedor PSE.
     *
     * @return nombre del proveedor
     */
    String getNombreProveedor();
}
