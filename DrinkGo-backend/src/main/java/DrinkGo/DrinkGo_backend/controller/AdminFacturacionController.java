package DrinkGo.DrinkGo_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import DrinkGo.DrinkGo_backend.dto.facturacion.CrearNotaCreditoRequest;
import DrinkGo.DrinkGo_backend.dto.facturacion.CrearSerieRequest;
import DrinkGo.DrinkGo_backend.entity.DocumentosFacturacion;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.entity.SeriesFacturacion;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionPse;
import DrinkGo.DrinkGo_backend.entity.HistorialPse;
import DrinkGo.DrinkGo_backend.repository.DetalleDocumentosFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.DetalleVentasRepository;
import DrinkGo.DrinkGo_backend.repository.DocumentosFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.MetodosPagoRepository;
import DrinkGo.DrinkGo_backend.repository.NegociosRepository;
import DrinkGo.DrinkGo_backend.repository.PagosVentaRepository;
import DrinkGo.DrinkGo_backend.repository.SedesRepository;
import DrinkGo.DrinkGo_backend.repository.SeriesFacturacionRepository;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionPseRepository;
import DrinkGo.DrinkGo_backend.repository.HistorialPseRepository;
import DrinkGo.DrinkGo_backend.service.FacturacionService;
import DrinkGo.DrinkGo_backend.service.PseAsyncService;

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
    @Autowired private DetalleDocumentosFacturacionRepository detalleDocRepo;
    @Autowired private DetalleVentasRepository detalleVentasRepo;
    @Autowired private MetodosPagoRepository metodosPagoRepo;
    @Autowired private PagosVentaRepository pagosVentaRepo;
    @Autowired private NegociosRepository negociosRepo;
    @Autowired private SedesRepository sedesRepo;
    @Autowired private ConfiguracionPseRepository configPseRepo;
    @Autowired private HistorialPseRepository historialPseRepo;
    @Autowired private FacturacionService facturacionService;
    @Autowired private PseAsyncService pseAsyncService;

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

            // Validar formato de serie según SUNAT
            String serieCode = request.getSerie().toUpperCase();
            String tipoDoc = request.getTipoDocumento();
            String prefixError = validarPrefijoSerie(serieCode, tipoDoc);
            if (prefixError != null) {
                return ResponseEntity.badRequest().body(Map.of("error", prefixError));
            }

            // Check duplicate serie code
            if (seriesRepo.existsByNegocioIdAndSerie(request.getNegocioId(), serieCode)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ya existe una serie con ese código para este negocio"));
            }

            SeriesFacturacion serie = new SeriesFacturacion();
            serie.setNegocio(negociosRepo.getReferenceById(request.getNegocioId()));
            serie.setSede(sedesRepo.getReferenceById(request.getSedeId()));
            serie.setTipoDocumento(SeriesFacturacion.TipoDocumento.valueOf(tipoDoc));
            serie.setSerie(serieCode);
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
            Optional<DocumentosFacturacion> opt = documentosRepo.findByIdWithRelations(id);
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
            if (estadoNuevo == DocumentosFacturacion.EstadoDocumento.anulado) {
                String motivo = body.get("motivoAnulacion");
                doc.setMotivoAnulacion(motivo != null ? motivo.trim() : null);
            }
            documentosRepo.save(doc);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  NOTAS DE CRÉDITO / DÉBITO
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Obtiene los ítems de un comprobante para la UI de NC/ND.
     * Busca en DetalleDocumentosFacturacion; si vacío, cae a DetalleVentas.
     * También incluye las notas (NC/ND) ya emitidas sobre este comprobante
     * y el monto acumulado de NC.
     */
    @GetMapping("/comprobantes/{id}/items")
    public ResponseEntity<?> getItemsComprobante(@PathVariable Long id) {
        try {
            var docOpt = documentosRepo.findById(id);
            if (docOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Comprobante no encontrado"));
            }
            var doc = docOpt.get();

            // Obtener ítems
            var items = detalleDocRepo.findByDocumentoFacturacionId(id);

            // Si no hay detalles de facturación, obtener de la venta
            java.util.List<?> itemsResult;
            if (items.isEmpty() && doc.getVenta() != null) {
                itemsResult = detalleVentasRepo.findByVentaId(doc.getVenta().getId());
            } else {
                itemsResult = items;
            }

            // Notas existentes sobre este comprobante
            var notasExistentes = documentosRepo.findNotasByDocumentoReferenciaId(id);

            // Monto acumulado de NC
            java.math.BigDecimal totalNcAcumulado = documentosRepo
                    .sumTotalNotasCreditoByDocumentoReferenciaId(id);

            // Cantidades ya devueltas por NC motivo 07 anteriores (productoId → cantidad)
            java.util.Map<Long, java.math.BigDecimal> cantidadesDevueltas = new java.util.HashMap<>();
            for (var nota : notasExistentes) {
                if (nota.getTipoDocumento() != DocumentosFacturacion.TipoDocumento.nota_credito) continue;
                if (!"07".equals(nota.getCodigoMotivoNota())) continue;
                if (nota.getEstadoDocumento() == DocumentosFacturacion.EstadoDocumento.anulado) continue;
                var detallesNota = detalleDocRepo.findByDocumentoFacturacionId(nota.getId());
                for (var det : detallesNota) {
                    if (det.getProducto() == null) continue;
                    cantidadesDevueltas.merge(det.getProducto().getId(), det.getCantidad(), java.math.BigDecimal::add);
                }
            }

            // Determinar estado de la sesión de caja asociada a la venta
            String estadoSesionCaja = "sin_sesion";
            boolean tieneEfectivo = false;
            if (doc.getVenta() != null && doc.getVenta().getSesionCaja() != null) {
                var sesion = doc.getVenta().getSesionCaja();
                estadoSesionCaja = sesion.getEstadoSesion() != null
                        ? sesion.getEstadoSesion().name() : "cerrada";
                // Verificar si la venta tiene pagos en efectivo
                var pagos = pagosVentaRepo.findByVentaId(doc.getVenta().getId());
                for (var pago : pagos) {
                    if (pago.getMetodoPago() != null
                            && pago.getMetodoPago().getTipo() == MetodosPago.TipoMetodoPago.efectivo
                            && pago.getMonto() != null
                            && pago.getMonto().compareTo(java.math.BigDecimal.ZERO) > 0) {
                        tieneEfectivo = true;
                        break;
                    }
                }
            }

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("items", itemsResult);
            response.put("notasExistentes", notasExistentes);
            response.put("totalNcAcumulado", totalNcAcumulado);
            response.put("totalComprobante", doc.getTotal());
            response.put("cantidadesDevueltas", cantidadesDevueltas);
            response.put("estadoSesionCaja", estadoSesionCaja);
            response.put("tieneEfectivo", tieneEfectivo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/comprobantes/nota-credito-debito")
    @Transactional
    public ResponseEntity<?> emitirNotaCreditoDebito(
            @RequestBody CrearNotaCreditoRequest request) {
        try {
            if (request.getDocumentoReferenciaId() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "documentoReferenciaId es requerido"));
            }
            if (request.getTipoNota() == null || request.getTipoNota().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "tipoNota es requerido (nota_credito o nota_debito)"));
            }
            if (request.getCodigoMotivo() == null || request.getCodigoMotivo().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "codigoMotivo es requerido"));
            }

            DocumentosFacturacion doc = facturacionService.emitirNotaCreditoDebito(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(doc);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  REEMISIÓN DE COMPROBANTE (TRAS NC MOTIVO 02 - ERROR EN RUC)
    // ═══════════════════════════════════════════════════════════════════

    @PostMapping("/comprobantes/{ncId}/reemitir")
    @Transactional
    public ResponseEntity<?> reemitirComprobanteConNuevoRuc(
            @PathVariable Long ncId,
            @RequestBody Map<String, String> request) {
        try {
            String tipoDocumento = request.get("tipoDocumento");
            String numeroDocumento = request.get("numeroDocumento");
            String nombreCliente = request.get("nombreCliente");
            String direccion = request.get("direccion");
            String usuarioIdStr = request.get("usuarioId");

            if (tipoDocumento == null || tipoDocumento.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "tipoDocumento es requerido"));
            }
            if (numeroDocumento == null || numeroDocumento.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "numeroDocumento es requerido"));
            }

            Long usuarioId = null;
            if (usuarioIdStr != null && !usuarioIdStr.isBlank()) {
                usuarioId = Long.parseLong(usuarioIdStr);
            }

            DocumentosFacturacion nuevoDoc = facturacionService
                    .reemitirComprobanteConNuevoCliente(
                            ncId, tipoDocumento, numeroDocumento,
                            nombreCliente, direccion, usuarioId);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDoc);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
    //  PSE (Proveedor de Servicios Electrónicos)
    // ═══════════════════════════════════════════════════════════════════

    @GetMapping("/pse/configuracion/{negocioId}")
    public ResponseEntity<?> getConfiguracionPse(@PathVariable Long negocioId) {
        try {
            Optional<ConfiguracionPse> config = configPseRepo.findFirstByNegocioId(negocioId);
            if (config.isEmpty()) {
                // Sin configuración previa: retornar objeto vacío sin defaults
                return ResponseEntity.ok(Map.of("estaActivo", false));
            }
            return ResponseEntity.ok(config.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/pse/configuracion/{negocioId}")
    @Transactional
    public ResponseEntity<?> guardarConfiguracionPse(
            @PathVariable Long negocioId, @RequestBody Map<String, Object> body) {
        try {
            ConfiguracionPse config = configPseRepo.findFirstByNegocioId(negocioId)
                    .orElseGet(() -> {
                        ConfiguracionPse c = new ConfiguracionPse();
                        c.setNegocio(negociosRepo.getReferenceById(negocioId));
                        return c;
                    });
            if (body.containsKey("proveedor")) config.setProveedor((String) body.get("proveedor"));
            if (body.containsKey("entorno")) config.setEntorno((String) body.get("entorno"));
            if (body.containsKey("apiToken")) config.setApiToken((String) body.get("apiToken"));
            if (body.containsKey("urlServicio")) config.setUrlServicio((String) body.get("urlServicio"));
            if (body.containsKey("rucEmisor")) config.setRucEmisor((String) body.get("rucEmisor"));
            if (body.containsKey("estaActivo")) config.setEstaActivo((Boolean) body.get("estaActivo"));

            configPseRepo.save(config);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pse/probar-conexion/{negocioId}")
    public ResponseEntity<?> probarConexionPse(
            @PathVariable Long negocioId,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            String tokenEnviado = body != null ? (String) body.get("apiToken") : null;

            // Guardar el token si se proporcionó
            if (tokenEnviado != null && !tokenEnviado.isBlank()) {
                ConfiguracionPse cfg = configPseRepo.findFirstByNegocioId(negocioId)
                        .orElseGet(() -> {
                            ConfiguracionPse c = new ConfiguracionPse();
                            c.setNegocio(negociosRepo.getReferenceById(negocioId));
                            return c;
                        });
                cfg.setApiToken(tokenEnviado);
                if (cfg.getProveedor() == null) cfg.setProveedor("SIMULADOR");
                if (cfg.getEntorno() == null) cfg.setEntorno("SANDBOX");
                configPseRepo.save(cfg);
            }

            Optional<ConfiguracionPse> configOpt = configPseRepo.findFirstByNegocioId(negocioId);
            String proveedor = configOpt.map(ConfiguracionPse::getProveedor).orElse("SIMULADOR");
            String entorno = configOpt.map(ConfiguracionPse::getEntorno).orElse("SANDBOX");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Conexión exitosa con el PSE (" + proveedor + " - " + entorno + ")",
                    "proveedor", proveedor,
                    "entorno", entorno
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PatchMapping("/pse/toggle/{negocioId}")
    @Transactional
    public ResponseEntity<?> togglePse(@PathVariable Long negocioId) {
        try {
            Optional<Negocios> negOpt = negociosRepo.findById(negocioId);
            if (negOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Negocio no encontrado"));
            }
            Negocios negocio = negOpt.get();
            boolean nuevoEstado = !Boolean.TRUE.equals(negocio.getTienePse());
            negocio.setTienePse(nuevoEstado);
            negociosRepo.save(negocio);

            // Crear configuración si no existe y se activa
            if (nuevoEstado) {
                configPseRepo.findFirstByNegocioId(negocioId).orElseGet(() -> {
                    ConfiguracionPse config = new ConfiguracionPse();
                    config.setNegocio(negocio);
                    config.setProveedor("SIMULADOR");
                    config.setEntorno("SANDBOX");
                    config.setEstaActivo(true);
                    return configPseRepo.save(config);
                });
            }

            return ResponseEntity.ok(Map.of(
                    "tienePse", nuevoEstado,
                    "message", nuevoEstado ? "PSE activado" : "PSE desactivado"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pse/bandeja/{negocioId}")
    public ResponseEntity<?> getBandejaPse(
            @PathVariable Long negocioId,
            @RequestParam(required = false) String estado) {
        try {
            List<DocumentosFacturacion> docs;
            if (estado != null && !estado.isEmpty()) {
                DocumentosFacturacion.EstadoDocumento estadoEnum =
                        DocumentosFacturacion.EstadoDocumento.valueOf(estado);
                docs = documentosRepo.findByNegocioIdAndEstadoDocumento(negocioId, estadoEnum);
            } else {
                docs = documentosRepo.findByNegocioIdAndModoEmisionOrderByCreadoEnDesc(
                        negocioId, DocumentosFacturacion.ModoEmision.PSE);
            }
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pse/enviar/{documentoId}")
    public ResponseEntity<?> enviarDocumentoPse(@PathVariable Long documentoId) {
        try {
            // 1. Cambiar estado a "enviado" de forma síncrona y retornar inmediatamente
            DocumentosFacturacion doc = facturacionService.marcarComoEnviando(documentoId);
            // 2. Procesar con el PSE en hilo async (30 s de simulación)
            pseAsyncService.procesarDocumentoAsync(documentoId);
            return ResponseEntity.ok(Map.of(
                    "estado", doc.getEstadoDocumento().name(),
                    "message", "Documento enviado a SUNAT. Procesando respuesta..."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/pse/reenviar/{documentoId}")
    public ResponseEntity<?> reenviarDocumentoPse(@PathVariable Long documentoId) {
        try {
            DocumentosFacturacion doc = facturacionService.marcarComoEnviando(documentoId);
            pseAsyncService.procesarDocumentoAsync(documentoId);
            return ResponseEntity.ok(Map.of(
                    "estado", doc.getEstadoDocumento().name(),
                    "message", "Documento reenviado a SUNAT. Procesando respuesta..."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Resetea manualmente un documento atascado en estado {@code enviado} → {@code pendiente_envio}.
     * Útil cuando el backend fue reiniciado mientras el async estaba corriendo.
     */
    @PostMapping("/pse/reset/{documentoId}")
    public ResponseEntity<?> resetDocumentoPseAtascado(@PathVariable Long documentoId) {
        try {
            DocumentosFacturacion doc = documentosRepo.findById(documentoId)
                    .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + documentoId));
            if (doc.getEstadoDocumento() != DocumentosFacturacion.EstadoDocumento.enviado) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El documento no está en estado 'enviado'. Estado actual: "
                                + doc.getEstadoDocumento().name()));
            }
            doc.setEstadoDocumento(DocumentosFacturacion.EstadoDocumento.pendiente_envio);
            doc.setRespuestaSunat("Reseteado manualmente — backend reiniciado durante el procesamiento");
            documentosRepo.save(doc);
            return ResponseEntity.ok(Map.of(
                    "estado", "pendiente_envio",
                    "message", "Documento reseteado a pendiente_envio. Ya puede volver a enviar."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pse/historial/{negocioId}")
    public ResponseEntity<?> getHistorialPse(@PathVariable Long negocioId) {
        try {
            List<HistorialPse> historial = historialPseRepo
                    .findByNegocioIdOrderByCreadoEnDesc(negocioId);
            return ResponseEntity.ok(historial);
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
                    || to == DocumentosFacturacion.EstadoDocumento.rechazado
                    || to == DocumentosFacturacion.EstadoDocumento.observado;
            case rechazado -> to == DocumentosFacturacion.EstadoDocumento.pendiente_envio;
            case observado -> to == DocumentosFacturacion.EstadoDocumento.aceptado
                    || to == DocumentosFacturacion.EstadoDocumento.anulado;
            case aceptado, anulado -> false; // terminal states
        };
    }

    /**
     * Valida que el prefijo de serie corresponda al tipo de documento según SUNAT.
     * <p>
     * Reglas:
     *   boleta       → debe empezar con "B" (ej: B001, B002)
     *   factura      → debe empezar con "F" (ej: F001, F002)
     *   nota_credito → debe empezar con "B" o "F" seguido de "C" o "N" (ej: BC01, FC01, BN01, FN01)
     *                   o simplemente "B" o "F" (prefijo corto)
     *   nota_debito  → debe empezar con "B" o "F" seguido de "D" (ej: BD01, FD01)
     *                   o simplemente "B" o "F" (prefijo corto)
     *
     * @return null si es válido, o mensaje de error si no cumple
     */
    private String validarPrefijoSerie(String serie, String tipoDocumento) {
        if (serie == null || serie.isBlank()) {
            return "El código de serie no puede estar vacío";
        }
        // Debe tener al menos 4 caracteres (ej: B001)
        if (serie.length() < 4) {
            return "El código de serie debe tener al menos 4 caracteres (ej: B001, F001)";
        }

        char firstChar = serie.charAt(0);
        return switch (tipoDocumento) {
            case "boleta" -> firstChar == 'B' ? null
                    : "Las series de boleta deben empezar con 'B' (ej: B001)";
            case "factura" -> firstChar == 'F' ? null
                    : "Las series de factura deben empezar con 'F' (ej: F001)";
            case "nota_credito" -> (firstChar == 'B' || firstChar == 'F') ? null
                    : "Las series de nota de crédito deben empezar con 'B' o 'F' (ej: BC01, FC01)";
            case "nota_debito" -> (firstChar == 'B' || firstChar == 'F') ? null
                    : "Las series de nota de débito deben empezar con 'B' o 'F' (ej: BD01, FD01)";
            default -> "Tipo de documento no reconocido: " + tipoDocumento;
        };
    }
}
