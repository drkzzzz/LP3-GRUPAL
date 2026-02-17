package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.*;
import DrinkGo.DrinkGo_backend.exception.OperacionInvalidaException;
import DrinkGo.DrinkGo_backend.exception.RecursoNoEncontradoException;
import DrinkGo.DrinkGo_backend.service.SerieFacturacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de series de facturación.
 * Base: /restful/facturacion/series
 * 
 * Endpoints:
 * - GET    /restful/facturacion/series           - Listar series
 * - GET    /restful/facturacion/series/{id}       - Obtener serie por ID
 * - POST   /restful/facturacion/series           - Crear nueva serie
 * - PUT    /restful/facturacion/series/{id}       - Actualizar serie
 * - DELETE /restful/facturacion/series/{id}       - Desactivar serie (DELETE lógico)
 */
@RestController
@RequestMapping("/restful/facturacion/series")
@CrossOrigin(origins = "*")
public class SerieFacturacionController {

    @Autowired
    private SerieFacturacionService serieFacturacionService;

    /**
     * Listar series por negocio, opcionalmente filtrando por sede.
     */
    @GetMapping
    public ResponseEntity<?> listarSeries(
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestParam(name = "sedeId", required = false) Long sedeId) {
        try {
            List<SerieFacturacionResponse> series = serieFacturacionService.obtenerSeries(negocioId, sedeId);
            return ResponseEntity.ok(series);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar series: " + e.getMessage()));
        }
    }

    /**
     * Obtener una serie por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSerie(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            SerieFacturacionResponse serie = serieFacturacionService.obtenerPorId(id, negocioId);
            return ResponseEntity.ok(serie);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener la serie de facturación"));
        }
    }

    /**
     * Crear una nueva serie de facturación.
     * Valida prefijo según normativa SUNAT (B* boletas, F* facturas, T* guías).
     */
    @PostMapping
    public ResponseEntity<?> crearSerie(@RequestBody CreateSerieFacturacionRequest request) {
        try {
            SerieFacturacionResponse serie = serieFacturacionService.crearSerie(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(serie);
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear la serie de facturación: " + e.getMessage()));
        }
    }

    /**
     * Actualizar una serie existente.
     * No permite cambiar prefijo si ya tiene documentos emitidos.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSerie(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId,
            @RequestBody UpdateSerieFacturacionRequest request) {
        try {
            SerieFacturacionResponse serie = serieFacturacionService.actualizarSerie(id, negocioId, request);
            return ResponseEntity.ok(serie);
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar la serie de facturación"));
        }
    }

    /**
     * Desactivar serie (DELETE lógico).
     * Solo cambia esta_activo a false, no elimina el registro.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivarSerie(
            @PathVariable Long id,
            @RequestParam(name = "negocioId", required = true) Long negocioId) {
        try {
            SerieFacturacionResponse serie = serieFacturacionService.desactivarSerie(id, negocioId);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Serie desactivada correctamente",
                    "serie", serie));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (OperacionInvalidaException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al desactivar la serie de facturación"));
        }
    }
}
