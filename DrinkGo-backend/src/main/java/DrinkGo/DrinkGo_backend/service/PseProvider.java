package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.facturacion.RespuestaPse;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;

/**
 * Interfaz para proveedores de servicios electrónicos (PSE).
 * <p>
 * Cada implementación conecta con un PSE diferente
 * (Nubefact, Efact, Bizlinks, o el simulador de pruebas).
 */
public interface PseProvider {

    /**
     * Envía un documento de facturación electrónica a SUNAT
     * a través de este proveedor PSE.
     *
     * @param documento El documento a enviar
     * @return La respuesta del PSE/SUNAT
     */
    RespuestaPse enviarDocumento(DocumentosFacturacion documento);
}
