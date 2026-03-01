package DrinkGo.DrinkGo_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import DrinkGo.DrinkGo_backend.dto.pos.AbrirCajaRequest;
import DrinkGo.DrinkGo_backend.dto.pos.AnularVentaRequest;
import DrinkGo.DrinkGo_backend.dto.pos.CerrarCajaRequest;
import DrinkGo.DrinkGo_backend.dto.pos.CrearVentaPosRequest;
import DrinkGo.DrinkGo_backend.dto.pos.MovimientoCajaRequest;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import DrinkGo.DrinkGo_backend.service.FacturacionService;
import DrinkGo.DrinkGo_backend.service.PosService;

/**
 * Controlador REST del modulo POS (Punto de Venta).
 * Controlador delgado: delega toda la logica de negocio a PosService
 * y FacturacionService.
 */
@RestController
@RequestMapping("/restful/pos")
public class PosController {

    @Autowired private PosService posService;
    @Autowired private FacturacionService facturacionService;

    @Autowired private VentasRepository ventasRepo;
    @Autowired private DetalleVentasRepository detalleVentasRepo;
    @Autowired private PagosVentaRepository pagosVentaRepo;
    @Autowired private CajasRegistradorasRepository cajasRepo;
    @Autowired private SesionesCajaRepository sesionesRepo;
    @Autowired private MovimientosCajaRepository movimientosRepo;
    @Autowired private MetodosPagoRepository metodosPagoRepo;
    @Autowired private DocumentosFacturacionRepository documentosRepo;
    @Autowired private NegociosRepository negociosRepo;
    @Autowired private SedesRepository sedesRepo;
    @Autowired private CategoriasGastoRepository categoriasGastoRepo;

    // PRODUCTOS POS (enriquecidos con stock y precio)

    @GetMapping("/productos/negocio/{negocioId}")
    public ResponseEntity<?> getProductosPosConStock(@PathVariable Long negocioId) {
        try {
            List<Productos> productos = posService.getProductosPosConStock(negocioId);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // VENTAS

    @GetMapping("/ventas/negocio/{negocioId}")
    public ResponseEntity<?> getVentasByNegocio(@PathVariable Long negocioId) {
        try {
            List<Ventas> ventas = ventasRepo.findByNegocioIdOrderByCreadoEnDesc(negocioId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/sesion/{sesionCajaId}")
    public ResponseEntity<?> getVentasBySesion(@PathVariable Long sesionCajaId) {
        try {
            List<Ventas> ventas = ventasRepo.findBySesionCajaIdOrderByCreadoEnDesc(sesionCajaId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/{ventaId}/negocio/{negocioId}")
    public ResponseEntity<?> getVentaById(@PathVariable Long ventaId, @PathVariable Long negocioId) {
        try {
            Optional<Ventas> opt = ventasRepo.findById(ventaId);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Venta no encontrada"));
            }
            return ResponseEntity.ok(opt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/{ventaId}/detalle")
    public ResponseEntity<?> getDetalleVenta(@PathVariable Long ventaId) {
        try {
            List<DetalleVentas> detalle = detalleVentasRepo.findByVentaId(ventaId);
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/{ventaId}/pagos")
    public ResponseEntity<?> getPagosVenta(@PathVariable Long ventaId) {
        try {
            List<PagosVenta> pagos = pagosVentaRepo.findByVentaId(ventaId);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/ventas")
    public ResponseEntity<?> crearVentaPos(@RequestBody CrearVentaPosRequest request) {
        try {
            Ventas venta = posService.crearVentaPos(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(venta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/ventas/anular")
    public ResponseEntity<?> anularVenta(@RequestBody AnularVentaRequest request) {
        try {
            Ventas venta = posService.anularVenta(request);
            return ResponseEntity.ok(venta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // COMPROBANTES DE FACTURACION

    @PostMapping("/facturacion/reenviar/{documentoId}")
    public ResponseEntity<?> reenviarComprobante(@PathVariable Long documentoId) {
        try {
            DocumentosFacturacion doc = facturacionService.reenviarComprobante(documentoId);
            return ResponseEntity.ok(doc);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/{ventaId}/comprobantes")
    public ResponseEntity<?> getComprobantesByVenta(@PathVariable Long ventaId) {
        try {
            List<DocumentosFacturacion> docs = documentosRepo.findByVentaId(ventaId);
            return ResponseEntity.ok(docs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // METODOS DE PAGO

    @GetMapping("/metodos-pago/negocio/{negocioId}")
    public ResponseEntity<?> getMetodosPagoPOS(@PathVariable Long negocioId) {
        try {
            // @SQLRestriction ya filtra esta_activo = 1
            List<MetodosPago> metodos = metodosPagoRepo.findByNegocioId(negocioId);
            // Devolver solo los campos necesarios para POS (evita lazy-loading del negocio)
            List<Map<String, Object>> result = metodos.stream()
                    .filter(m -> Boolean.TRUE.equals(m.getDisponiblePos()))
                    .map(m -> {
                        Map<String, Object> dto = new java.util.LinkedHashMap<>();
                        dto.put("id", m.getId());
                        dto.put("nombre", m.getNombre());
                        dto.put("codigo", m.getCodigo());
                        dto.put("tipo", m.getTipo() != null ? m.getTipo().name() : "otro");
                        dto.put("disponiblePos", m.getDisponiblePos());
                        dto.put("disponibleTiendaOnline", m.getDisponibleTiendaOnline());
                        dto.put("orden", m.getOrden());
                        dto.put("estaActivo", m.getEstaActivo());
                        return dto;
                    })
                    .toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // CAJAS REGISTRADORAS

    @GetMapping("/cajas/negocio/{negocioId}")
    public ResponseEntity<?> getCajasByNegocio(@PathVariable Long negocioId) {
        try {
            List<CajasRegistradoras> cajas = cajasRepo.findByNegocioId(negocioId);
            return ResponseEntity.ok(cajas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/cajas/{cajaId}/negocio/{negocioId}")
    public ResponseEntity<?> getCajaById(@PathVariable Long cajaId, @PathVariable Long negocioId) {
        try {
            Optional<CajasRegistradoras> opt = cajasRepo.findById(cajaId);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Caja no encontrada"));
            }
            return ResponseEntity.ok(opt.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cajas")
    public ResponseEntity<?> crearCaja(@RequestBody Map<String, Object> body) {
        try {
            Long negocioId = Long.valueOf(body.get("negocioId").toString());
            Negocios negocio = negociosRepo.findById(negocioId)
                    .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

            // Obtener la primera sede del negocio
            List<Sedes> sedes = sedesRepo.findByNegocioId(negocioId);
            if (sedes.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El negocio no tiene sedes configuradas"));
            }

            CajasRegistradoras caja = new CajasRegistradoras();
            caja.setNegocio(negocio);
            caja.setSede(sedes.get(0));
            caja.setNombreCaja(body.get("nombreCaja").toString());
            caja.setCodigo(body.get("codigo").toString());

            cajasRepo.save(caja);
            return ResponseEntity.status(HttpStatus.CREATED).body(caja);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/cajas")
    public ResponseEntity<?> actualizarCaja(@RequestBody Map<String, Object> body) {
        try {
            if (!body.containsKey("id") || body.get("id") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID es requerido para actualizar"));
            }
            Long cajaId = Long.valueOf(body.get("id").toString());
            CajasRegistradoras caja = cajasRepo.findById(cajaId)
                    .orElseThrow(() -> new RuntimeException("Caja no encontrada"));

            if (body.containsKey("nombreCaja")) caja.setNombreCaja(body.get("nombreCaja").toString());
            if (body.containsKey("codigo")) caja.setCodigo(body.get("codigo").toString());

            cajasRepo.save(caja);
            return ResponseEntity.ok(caja);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // SESIONES DE CAJA

    @PostMapping("/sesiones/abrir")
    public ResponseEntity<?> abrirCaja(@RequestBody AbrirCajaRequest request) {
        try {
            SesionesCaja sesion = posService.abrirCaja(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sesiones/cerrar")
    public ResponseEntity<?> cerrarCaja(@RequestBody CerrarCajaRequest request) {
        try {
            SesionesCaja sesion = posService.cerrarCaja(request);
            return ResponseEntity.ok(sesion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sesiones/activa/usuario/{usuarioId}")
    public ResponseEntity<?> getSesionActiva(@PathVariable Long usuarioId) {
        try {
            Optional<SesionesCaja> sesion = sesionesRepo
                    .findByUsuarioIdAndEstadoSesion(usuarioId, SesionesCaja.EstadoSesion.abierta);
            if (sesion.isEmpty()) {
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.ok(sesion.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sesiones/negocio/{negocioId}")
    public ResponseEntity<?> getSesionesByNegocio(@PathVariable Long negocioId) {
        try {
            List<SesionesCaja> sesiones = sesionesRepo
                    .findByCajaNegocioIdOrderByFechaAperturaDesc(negocioId);
            return ResponseEntity.ok(sesiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sesiones/caja/{cajaId}")
    public ResponseEntity<?> getSesionesByCaja(@PathVariable Long cajaId) {
        try {
            List<SesionesCaja> sesiones = sesionesRepo
                    .findByCajaIdOrderByFechaAperturaDesc(cajaId);
            return ResponseEntity.ok(sesiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sesiones/{sesionCajaId}/resumen")
    public ResponseEntity<?> getResumenTurno(@PathVariable Long sesionCajaId) {
        try {
            Map<String, Object> resumen = posService.getResumenTurno(sesionCajaId);
            return ResponseEntity.ok(resumen);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // MOVIMIENTOS DE CAJA

    @PostMapping("/movimientos")
    public ResponseEntity<?> registrarMovimiento(@RequestBody MovimientoCajaRequest request) {
        try {
            MovimientosCaja movimiento = posService.registrarMovimiento(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/movimientos/sesion/{sesionCajaId}")
    public ResponseEntity<?> getMovimientosBySesion(@PathVariable Long sesionCajaId) {
        try {
            List<MovimientosCaja> movimientos = movimientosRepo
                    .findBySesionCajaIdOrderByFechaMovimientoDesc(sesionCajaId);
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/movimientos/negocio/{negocioId}")
    public ResponseEntity<?> getMovimientosByNegocio(
            @PathVariable Long negocioId,
            @RequestParam(required = false) Long cajaId,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta) {
        try {
            java.time.LocalDateTime desdeDate = java.time.LocalDateTime.of(2000, 1, 1, 0, 0);
            java.time.LocalDateTime hastaDate = java.time.LocalDateTime.now().plusDays(1);

            if (desde != null && !desde.isEmpty()) {
                desdeDate = java.time.LocalDate.parse(desde).atStartOfDay();
            }
            if (hasta != null && !hasta.isEmpty()) {
                hastaDate = java.time.LocalDate.parse(hasta).atTime(23, 59, 59);
            }

            List<MovimientosCaja> movimientos;
            if (cajaId != null) {
                movimientos = movimientosRepo.findByNegocioAndCajaAndDateRange(
                        negocioId, cajaId, desdeDate, hastaDate);
            } else {
                movimientos = movimientosRepo.findByNegocioAndDateRange(
                        negocioId, desdeDate, hastaDate);
            }

            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // CATEGORÍAS DE GASTO

    @GetMapping("/categorias-gasto/negocio/{negocioId}")
    public ResponseEntity<?> getCategoriasByNegocio(@PathVariable Long negocioId) {
        try {
            List<CategoriasGasto> categorias = categoriasGastoRepo.findByNegocioId(negocioId);
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/categorias-gasto")
    public ResponseEntity<?> crearCategoria(@RequestBody Map<String, Object> body) {
        try {
            Long negocioId = Long.valueOf(body.get("negocioId").toString());
            Negocios negocio = negociosRepo.findById(negocioId)
                    .orElseThrow(() -> new RuntimeException("Negocio no encontrado"));

            CategoriasGasto cat = new CategoriasGasto();
            cat.setNegocio(negocio);
            cat.setNombre(body.get("nombre").toString());
            cat.setCodigo(body.getOrDefault("codigo", "").toString());
            if (body.containsKey("tipo") && body.get("tipo") != null) {
                cat.setTipo(CategoriasGasto.TipoCategoria.valueOf(body.get("tipo").toString()));
            }
            if (body.containsKey("descripcion") && body.get("descripcion") != null) {
                cat.setDescripcion(body.get("descripcion").toString());
            }

            categoriasGastoRepo.save(cat);
            return ResponseEntity.status(HttpStatus.CREATED).body(cat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/categorias-gasto/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            CategoriasGasto cat = categoriasGastoRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            if (body.containsKey("nombre")) cat.setNombre(body.get("nombre").toString());
            if (body.containsKey("codigo")) cat.setCodigo(body.get("codigo").toString());
            if (body.containsKey("tipo") && body.get("tipo") != null) {
                cat.setTipo(CategoriasGasto.TipoCategoria.valueOf(body.get("tipo").toString()));
            }
            if (body.containsKey("descripcion")) {
                cat.setDescripcion(body.get("descripcion") != null ? body.get("descripcion").toString() : null);
            }

            categoriasGastoRepo.save(cat);
            return ResponseEntity.ok(cat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/categorias-gasto/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriasGastoRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Categoría eliminada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
