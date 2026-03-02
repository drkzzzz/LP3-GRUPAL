package DrinkGo.DrinkGo_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * Servicio que consume la API de PeruDevs (https://api.perudevs.com)
 * para consultas de RUC (SUNAT) y DNI (RENIEC).
 *
 * Endpoints:
 * RUC: GET /api/v1/ruc?document={ruc}&key={token}
 * DNI: GET /api/v1/dni/complete?document={dni}&key={token}
 *
 * La respuesta viene envuelta en { estado, mensaje, resultado }.
 * Este servicio extrae y retorna solo el objeto "resultado".
 */
@Service
public class PeruDevsService {

    private static final String BASE_URL = "https://api.perudevs.com";
    private static final String API_TOKEN = "cGVydWRldnMucHJvZHVjdGlvbi5maXRjb2RlcnMuNjlhNTI0NjUwNGEyNjc2MDk2ZjkzZDZm";

    private final RestClient restClient;

    public PeruDevsService() {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    /**
     * Consulta información de una empresa/persona por RUC (SUNAT).
     * Endpoint: GET /api/v1/ruc?document={ruc}&key={token}
     *
     * Campos del resultado:
     * razon_social, condicion, nombre_comercial, tipo,
     * fecha_inscripcion, estado, direccion, sistema_emision,
     * actividad_exterior, sistema_contabilidad
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> consultarRuc(String numero) {
        Map<String, Object> response = restClient.get()
                .uri("/api/v1/ruc?document={document}&key={key}", numero, API_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Map.class);

        if (response != null && Boolean.TRUE.equals(response.get("estado"))) {
            Object resultado = response.get("resultado");
            if (resultado instanceof Map) {
                return (Map<String, Object>) resultado;
            }
        }
        String mensaje = response != null ? String.valueOf(response.get("mensaje")) : "Sin respuesta";
        throw new RuntimeException("No se encontraron datos para el RUC: " + mensaje);
    }

    /**
     * Consulta información de una persona por DNI (RENIEC).
     * Endpoint: GET /api/v1/dni/complete?document={dni}&key={token}
     *
     * Campos del resultado:
     * id, nombres, apellido_paterno, apellido_materno,
     * nombre_completo, genero, fecha_nacimiento, codigo_verificacion
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> consultarDni(String numero) {
        Map<String, Object> response = restClient.get()
                .uri("/api/v1/dni/complete?document={document}&key={key}", numero, API_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Map.class);

        if (response != null && Boolean.TRUE.equals(response.get("estado"))) {
            Object resultado = response.get("resultado");
            if (resultado instanceof Map) {
                return (Map<String, Object>) resultado;
            }
        }
        String mensaje = response != null ? String.valueOf(response.get("mensaje")) : "Sin respuesta";
        throw new RuntimeException("No se encontraron datos para el DNI: " + mensaje);
    }
}
