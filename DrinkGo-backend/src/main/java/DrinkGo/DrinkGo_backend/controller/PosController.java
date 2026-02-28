package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.pos.*;
import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.service.IPosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador del módulo POS (Punto de Venta).
 * Todos los endpoints bajo /restful/pos/
 */
@RestController
@RequestMapping("/restful/pos")
public class PosController {

    @Autowired
    private IPosService posService;

    // ════════════════════════════════════════════════════════════
    //  CAJAS REGISTRADORAS
    // ════════════════════════════════════════════════════════════

    @GetMapping("/cajas/negocio/{negocioId}")
    public ResponseEntity<?> listarCajas(@PathVariable Long negocioId) {
        try {
            List<CajasRegistradoras> cajas = posService.listarCajasPorNegocio(negocioId);
            return ResponseEntity.ok(cajas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/cajas/{cajaId}/negocio/{negocioId}")
    public ResponseEntity<?> obtenerCaja(@PathVariable Long cajaId, @PathVariable Long negocioId) {
        try {
            CajasRegistradoras caja = posService.obtenerCaja(cajaId, negocioId);
            return ResponseEntity.ok(caja);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cajas")
    public ResponseEntity<?> crearCaja(@RequestBody CrearCajaRequest request) {
        try {
            CajasRegistradoras nueva = posService.crearCaja(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/cajas")
    public ResponseEntity<?> actualizarCaja(@RequestBody CrearCajaRequest request) {
        try {
            CajasRegistradoras actualizada = posService.actualizarCaja(request);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  SESIONES DE CAJA (TURNOS)
    // ════════════════════════════════════════════════════════════

    @PostMapping("/sesiones/abrir")
    public ResponseEntity<?> abrirCaja(@RequestBody AbrirCajaRequest request) {
        try {
            SesionesCaja sesion = posService.abrirCaja(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sesiones/cerrar")
    public ResponseEntity<?> cerrarCaja(@RequestBody CerrarCajaRequest request) {
        try {
            SesionesCaja sesion = posService.cerrarCaja(request);
            return ResponseEntity.ok(sesion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sesiones/activa/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerSesionActiva(@PathVariable Long usuarioId) {
        try {
            SesionesCaja sesion = posService.obtenerSesionActiva(usuarioId);
            if (sesion == null) {
                return ResponseEntity.ok(Map.of("sesionActiva", false));
            }
            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sesiones/negocio/{negocioId}")
    public ResponseEntity<?> listarSesionesPorNegocio(@PathVariable Long negocioId) {
        try {
            List<SesionesCaja> sesiones = posService.listarSesionesPorNegocio(negocioId);
            return ResponseEntity.ok(sesiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sesiones/caja/{cajaId}")
    public ResponseEntity<?> listarSesionesPorCaja(@PathVariable Long cajaId) {
        try {
            List<SesionesCaja> sesiones = posService.listarSesionesPorCaja(cajaId);
            return ResponseEntity.ok(sesiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  MOVIMIENTOS DE CAJA
    // ════════════════════════════════════════════════════════════

    @PostMapping("/movimientos")
    public ResponseEntity<?> registrarMovimiento(@RequestBody MovimientoCajaRequest request) {
        try {
            MovimientosCaja movimiento = posService.registrarMovimiento(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/movimientos/sesion/{sesionCajaId}")
    public ResponseEntity<?> listarMovimientos(@PathVariable Long sesionCajaId) {
        try {
            List<MovimientosCaja> movimientos = posService.listarMovimientosSesion(sesionCajaId);
            return ResponseEntity.ok(movimientos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  VENTAS POS
    // ════════════════════════════════════════════════════════════

    @PostMapping("/ventas")
    public ResponseEntity<?> crearVenta(@RequestBody CrearVentaPosRequest request) {
        try {
            Ventas venta = posService.crearVentaPos(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/ventas/anular")
    public ResponseEntity<?> anularVenta(@RequestBody AnularVentaRequest request) {
        try {
            Ventas venta = posService.anularVenta(request);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/{ventaId}/negocio/{negocioId}")
    public ResponseEntity<?> obtenerVenta(@PathVariable Long ventaId, @PathVariable Long negocioId) {
        try {
            Ventas venta = posService.obtenerVenta(ventaId, negocioId);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/negocio/{negocioId}")
    public ResponseEntity<?> listarVentasPorNegocio(@PathVariable Long negocioId) {
        try {
            List<Ventas> ventas = posService.listarVentasPorNegocio(negocioId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/sesion/{sesionCajaId}")
    public ResponseEntity<?> listarVentasPorSesion(@PathVariable Long sesionCajaId) {
        try {
            List<Ventas> ventas = posService.listarVentasPorSesion(sesionCajaId);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/{ventaId}/detalle")
    public ResponseEntity<?> obtenerDetalleVenta(@PathVariable Long ventaId) {
        try {
            List<DetalleVentas> detalles = posService.obtenerDetalleVenta(ventaId);
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ventas/{ventaId}/pagos")
    public ResponseEntity<?> obtenerPagosVenta(@PathVariable Long ventaId) {
        try {
            List<PagosVenta> pagos = posService.obtenerPagosVenta(ventaId);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  MÉTODOS DE PAGO
    // ════════════════════════════════════════════════════════════

    @GetMapping("/metodos-pago/negocio/{negocioId}")
    public ResponseEntity<?> listarMetodosPago(@PathVariable Long negocioId) {
        try {
            List<MetodosPago> metodos = posService.listarMetodosPagoPOS(negocioId);
            return ResponseEntity.ok(metodos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  RESUMEN DE TURNO
    // ════════════════════════════════════════════════════════════

    @GetMapping("/sesiones/{sesionCajaId}/resumen")
    public ResponseEntity<?> obtenerResumenTurno(@PathVariable Long sesionCajaId) {
        try {
            Map<String, Object> resumen = posService.obtenerResumenTurno(sesionCajaId);
            return ResponseEntity.ok(resumen);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
