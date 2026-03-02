package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.service.PeruDevsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller proxy para consultas de documentos (RUC / DNI)
 * a través de la API PeruDevs.
 *
 * Endpoints:
 * GET /restful/consulta/ruc?numero=20601030013
 * GET /restful/consulta/dni?numero=46027897
 */
@RestController
@RequestMapping("/restful/consulta")
public class ConsultaDocumentoController {

    @Autowired
    private PeruDevsService peruDevsService;

    /**
     * Consultar datos de empresa por RUC (SUNAT).
     * 
     * @param numero RUC de 11 dígitos
     */
    @GetMapping("/ruc")
    public ResponseEntity<?> consultarRuc(@RequestParam String numero) {
        if (numero == null || !numero.matches("\\d{11}")) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "El RUC debe tener exactamente 11 dígitos numéricos"));
        }
        try {
            Map<String, Object> resultado = peruDevsService.consultarRuc(numero);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error al consultar RUC: " + e.getMessage()));
        }
    }

    /**
     * Consultar datos de persona por DNI (RENIEC).
     * 
     * @param numero DNI de 8 dígitos
     */
    @GetMapping("/dni")
    public ResponseEntity<?> consultarDni(@RequestParam String numero) {
        if (numero == null || !numero.matches("\\d{8}")) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "El DNI debe tener exactamente 8 dígitos numéricos"));
        }
        try {
            Map<String, Object> resultado = peruDevsService.consultarDni(numero);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Error al consultar DNI: " + e.getMessage()));
        }
    }
}
