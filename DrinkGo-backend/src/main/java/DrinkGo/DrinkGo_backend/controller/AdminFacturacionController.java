package DrinkGo.DrinkGo_backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import DrinkGo.DrinkGo_backend.dto.facturacion.CrearSerieRequest;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.entity.SeriesFacturacion;
import DrinkGo.DrinkGo_backend.repository.DocumentosFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.MetodosPagoRepository;
import DrinkGo.DrinkGo_backend.repository.NegociosRepository;
import DrinkGo.DrinkGo_backend.repository.SedesRepository;
import DrinkGo.DrinkGo_backend.repository.SeriesFacturacionRepository;

/**
 * Controller for Facturación admin module.
 * Manages: Series, Comprobantes (Documentos), Métodos de Pago.
 * All endpoints under /restful/admin/facturacion/*
 */
@RestController
@RequestMapping("/restful/admin/facturacion")
public class AdminFacturacionController {

    @Autowired private SeriesFacturacionRepository seriesRepo;
    @Autowired private DocumentosFacturacionRepository documentosRepo;
    @Autowired private MetodosPagoRepository metodosPagoRepo;
    @Autowired private NegociosRepository negociosRepo;
    @Autowired private SedesRepository sedesRepo;

    // ═══════════════════════════════════════════════════════════════════
    //  SERIES DE FACTURACIÓN
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/series/negocio/{negocioId}")
    public ResponseEntity<?> getSeriesByNegocio(@PathVariable Long negocioId) {
        try {
            List<SeriesFacturacion> series = seriesRepo.findByNegocioId(negocioId);
            return ResponseEntity.ok(series);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/series/{id}")
    public ResponseEntity<?> getSerieById(@PathVariable Long id) {
        try {
            Optional<SeriesFacturacion> opt = seriesRepo.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Serie no encontrada"));
            }
            return ResponseEntity.ok(opt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/series")
    @Transactional
    public ResponseEntity<?> crearSerie(@RequestBody CrearSerieRequest request) {
        try {
            if (request.getNegocioId() == null || request.getSedeId() == null
                    || request.getTipoDocumento() == null || request.getSerie() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "negocioId, sedeId, tipoDocumento y serie son requeridos"));
            }

            // Check duplicate serie code
            if (seriesRepo.existsByNegocioIdAndSerie(request.getNegocioId(), request.getSerie())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ya existe una serie con ese código para este negocio"));
            }

            SeriesFacturacion serie = new SeriesFacturacion();
            serie.setNegocio(negociosRepo.getReferenceById(request.getNegocioId()));
            serie.setSede(sedesRepo.getReferenceById(request.getSedeId()));
            serie.setTipoDocumento(SeriesFacturacion.TipoDocumento.valueOf(request.getTipoDocumento()));
            serie.setSerie(request.getSerie());
            serie.setNumeroActual(1);
            serie.setEsPredeterminada(request.getEsPredeterminada() != null ? request.getEsPredeterminada() : false);
            serie.setEstaActivo(true);

            seriesRepo.save(serie);
            return ResponseEntity.status(HttpStatus.CREATED).body(serie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/series/{id}")
    @Transactional
    public ResponseEntity<?> actualizarSerie(@PathVariable Long id, @RequestBody SeriesFacturacion update) {
        try {
            Optional<SeriesFacturacion> opt = seriesRepo.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Serie no encontrada"));
            }
            SeriesFacturacion serie = opt.get();
            if (update.getSerie() != null) serie.setSerie(update.getSerie());
            if (update.getEsPredeterminada() != null) serie.setEsPredeterminada(update.getEsPredeterminada());
            if (update.getEstaActivo() != null) serie.setEstaActivo(update.getEstaActivo());

            seriesRepo.save(serie);
            return ResponseEntity.ok(serie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/series/{id}")
    public ResponseEntity<?> eliminarSerie(@PathVariable Long id) {
        try {
            seriesRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Serie eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  COMPROBANTES (DOCUMENTOS DE FACTURACIÓN)
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/comprobantes/negocio/{negocioId}")
    public ResponseEntity<?> getComprobantesByNegocio(@PathVariable Long negocioId) {
        try {
            List<DocumentosFacturacion> docs = documentosRepo
                    .findByNegocioIdOrderByCreadoEnDesc(negocioId);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/comprobantes/{id}")
    public ResponseEntity<?> getComprobanteById(@PathVariable Long id) {
        try {
            Optional<DocumentosFacturacion> opt = documentosRepo.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Comprobante no encontrado"));
            }
            return ResponseEntity.ok(opt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/comprobantes/{id}/estado")
    @Transactional
    public ResponseEntity<?> cambiarEstadoComprobante(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Optional<DocumentosFacturacion> opt = documentosRepo.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Comprobante no encontrado"));
            }
            DocumentosFacturacion doc = opt.get();
            String nuevoEstado = body.get("estado");
            if (nuevoEstado == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "estado es requerido"));
            }

            // Validate state transition
            DocumentosFacturacion.EstadoDocumento estadoActual = doc.getEstadoDocumento();
            DocumentosFacturacion.EstadoDocumento estadoNuevo;
            try {
                estadoNuevo = DocumentosFacturacion.EstadoDocumento.valueOf(nuevoEstado);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Estado inválido: " + nuevoEstado));
            }

            if (!isValidTransition(estadoActual, estadoNuevo)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "Transición no permitida: " + estadoActual + " → " + estadoNuevo));
            }

            doc.setEstadoDocumento(estadoNuevo);
            documentosRepo.save(doc);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  MÉTODOS DE PAGO
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/metodos-pago/negocio/{negocioId}")
    public ResponseEntity<?> getMetodosPagoByNegocio(@PathVariable Long negocioId) {
        try {
            List<MetodosPago> metodos = metodosPagoRepo.findByNegocioId(negocioId);
            return ResponseEntity.ok(metodos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/metodos-pago/{id}")
    public ResponseEntity<?> getMetodoPagoById(@PathVariable Long id) {
        try {
            Optional<MetodosPago> opt = metodosPagoRepo.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Método de pago no encontrado"));
            }
            return ResponseEntity.ok(opt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/metodos-pago")
    public ResponseEntity<?> crearMetodoPago(@RequestBody MetodosPago metodo) {
        try {
            metodosPagoRepo.save(metodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(metodo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/metodos-pago/{id}")
    public ResponseEntity<?> actualizarMetodoPago(@PathVariable Long id, @RequestBody MetodosPago update) {
        try {
            Optional<MetodosPago> opt = metodosPagoRepo.findById(id);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Método de pago no encontrado"));
            }
            MetodosPago metodo = opt.get();
            if (update.getNombre() != null) metodo.setNombre(update.getNombre());
            if (update.getCodigo() != null) metodo.setCodigo(update.getCodigo());
            if (update.getTipo() != null) metodo.setTipo(update.getTipo());
            if (update.getDisponiblePos() != null) metodo.setDisponiblePos(update.getDisponiblePos());
            if (update.getDisponibleTiendaOnline() != null) metodo.setDisponibleTiendaOnline(update.getDisponibleTiendaOnline());
            if (update.getEstaActivo() != null) metodo.setEstaActivo(update.getEstaActivo());
            if (update.getOrden() != null) metodo.setOrden(update.getOrden());

            metodosPagoRepo.save(metodo);
            return ResponseEntity.ok(metodo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/metodos-pago/{id}")
    public ResponseEntity<?> eliminarMetodoPago(@PathVariable Long id) {
        try {
            metodosPagoRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Método de pago eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * State machine for comprobante status transitions.
     * Valid transitions:
     *   borrador → pendiente_envio, anulado
     *   pendiente_envio → enviado, anulado
     *   enviado → aceptado, rechazado
     *   rechazado → pendiente_envio (retry)
     *   aceptado → (terminal)
     *   anulado → (terminal)
     */
    private boolean isValidTransition(
            DocumentosFacturacion.EstadoDocumento from,
            DocumentosFacturacion.EstadoDocumento to) {
        return switch (from) {
            case borrador -> to == DocumentosFacturacion.EstadoDocumento.pendiente_envio
                    || to == DocumentosFacturacion.EstadoDocumento.anulado;
            case pendiente_envio -> to == DocumentosFacturacion.EstadoDocumento.enviado
                    || to == DocumentosFacturacion.EstadoDocumento.anulado;
            case enviado -> to == DocumentosFacturacion.EstadoDocumento.aceptado
                    || to == DocumentosFacturacion.EstadoDocumento.rechazado;
            case rechazado -> to == DocumentosFacturacion.EstadoDocumento.pendiente_envio;
            case aceptado, anulado -> false; // terminal states
        };
    }
}
