package DrinkGo.DrinkGo_backend.integration.pse;

import DrinkGo.DrinkGo_backend.entity.DocumentoFacturacion;
import DrinkGo.DrinkGo_backend.entity.Negocio;

/**
 * Interface desacoplada para el envío de documentos electrónicos a SUNAT.
 * 
 * DISEÑO:
 * - Esta interfaz define el contrato para cualquier proveedor PSE.
 * - Permite intercambiar la implementación sin modificar la lógica de negocio.
 * - En el futuro se puede crear PseNubefactClient, PseEfactClient, etc.
 * 
 * DATOS DEL EMISOR:
 * - Los datos del emisor (RUC, razón social, dirección) se obtienen de la
 *   entidad Negocio (tabla negocios), identificada por negocio_id.
 * - Estos datos son requeridos por SUNAT en cada envío electrónico.
 * - NO se almacenan en el documento, se consultan del Negocio al momento del envío.
 * 
 * IMPLEMENTACIONES DISPONIBLES:
 * - PseSimulationClient: Simulación local (modo desarrollo/pruebas)
 * - (Futuro) PseNubefactClient: Integración real con Nubefact
 * - (Futuro) PseEfactClient: Integración real con Efact
 */
public interface PseClient {

    /**
     * Envía un documento de facturación a SUNAT a través del PSE.
     * Requiere los datos del negocio emisor (RUC, razón social, dirección)
     * que SUNAT exige en cada comprobante electrónico.
     *
     * @param documento el documento de facturación a enviar
     * @param negocio   el negocio emisor (contiene RUC, razón social, dirección)
     * @return PseResponse con el resultado del envío
     */
    PseResponse enviarDocumento(DocumentoFacturacion documento, Negocio negocio);

    /**
     * Consulta el estado de un documento previamente enviado.
     *
     * @param ticketSunat el ticket de SUNAT para consulta
     * @return PseResponse con el estado actual
     */
    PseResponse consultarEstado(String ticketSunat);

    /**
     * Solicita la anulación de un documento ante SUNAT.
     * Requiere datos del negocio emisor para la Comunicación de Baja.
     *
     * @param documento el documento a anular
     * @param negocio   el negocio emisor
     * @param motivo    el motivo de anulación
     * @return PseResponse con el resultado de la solicitud
     */
    PseResponse anularDocumento(DocumentoFacturacion documento, Negocio negocio, String motivo);

    /**
     * Retorna el nombre del proveedor PSE.
     *
     * @return nombre del proveedor
     */
    String getNombreProveedor();
}
