package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.VentaDTO;
import DrinkGo.DrinkGo_backend.dto.VentaCreateRequest;
import DrinkGo.DrinkGo_backend.service.VentaService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Ventas - Bloque 8
 * Requiere autenticación JWT
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final UsuarioService usuarioService;

    public VentaController(VentaService ventaService, UsuarioService usuarioService) {
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/ventas - Listar todas las ventas del negocio
     */
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listar(negocioId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/sede/{sedeId} - Listar ventas de una sede
     */
    @GetMapping("/sede/{sedeId}")
    public ResponseEntity<?> listarPorSede(@PathVariable Long sedeId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listarPorSede(negocioId, sedeId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/sesion/{sesionId} - Listar ventas de una sesión de caja
     */
    @GetMapping("/sesion/{sesionId}")
    public ResponseEntity<?> listarPorSesion(@PathVariable Long sesionId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listarPorSesion(negocioId, sesionId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/cliente/{clienteId} - Listar ventas de un cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long clienteId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listarPorCliente(negocioId, clienteId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/vendedor/{vendedorId} - Listar ventas de un vendedor
     */
    @GetMapping("/vendedor/{vendedorId}")
    public ResponseEntity<?> listarPorVendedor(@PathVariable Long vendedorId) {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listarPorVendedor(negocioId, vendedorId);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/tipo/{tipo} - Listar ventas por tipo (LOCAL, DELIVERY, etc.)
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> listarPorTipo(@PathVariable String tipo) {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listarPorTipo(negocioId, tipo);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/estado/{estado} - Listar ventas por estado (PENDIENTE,
     * CONFIRMADA, etc.)
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> listarPorEstado(@PathVariable String estado) {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listarPorEstado(negocioId, estado);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/rango - Listar ventas en rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<?> listarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Long negocioId = obtenerNegocioId();
            List<VentaDTO> ventas = ventaService.listarPorRango(negocioId,
                    fechaInicio.atStartOfDay(), fechaFin.plusDays(1).atStartOfDay());
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/{id} - Obtener una venta completa (con detalles y pagos)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            VentaDTO venta = ventaService.obtener(negocioId, id);
            return ResponseEntity.ok(venta);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /api/ventas - Crear nueva venta
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody VentaCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Long vendedorId = obtenerUsuarioId();

            VentaDTO venta = ventaService.crear(negocioId, vendedorId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Venta creada exitosamente");
            response.put("venta", venta);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /api/ventas/{id}/anular - Anular/cancelar una venta
     */
    @PutMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id, @RequestParam(required = false) String motivoAnulacion) {
        try {
            Long negocioId = obtenerNegocioId();
            VentaDTO venta = ventaService.anular(negocioId, id, motivoAnulacion);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Venta anulada exitosamente");
            response.put("venta", venta);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/sede/{sedeId}/total - Calcular total de ventas de una sede en
     * rango
     */
    @GetMapping("/sede/{sedeId}/total")
    public ResponseEntity<?> calcularTotalSede(
            @PathVariable Long sedeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Long negocioId = obtenerNegocioId();
            BigDecimal total = ventaService.calcularTotalVentasSede(negocioId, sedeId,
                    fechaInicio.atStartOfDay(), fechaFin.plusDays(1).atStartOfDay());

            Map<String, Object> response = new HashMap<>();
            response.put("sedeId", sedeId);
            response.put("fechaInicio", fechaInicio);
            response.put("fechaFin", fechaFin);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/sesion/{sesionId}/total - Calcular total de ventas de una
     * sesión
     */
    @GetMapping("/sesion/{sesionId}/total")
    public ResponseEntity<?> calcularTotalSesion(@PathVariable Long sesionId) {
        try {
            Long negocioId = obtenerNegocioId();
            BigDecimal total = ventaService.calcularTotalVentasSesion(negocioId, sesionId);

            Map<String, Object> response = new HashMap<>();
            response.put("sesionId", sesionId);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /api/ventas/cliente/{clienteId}/total - Calcular total de compras de un
     * cliente
     */
    @GetMapping("/cliente/{clienteId}/total")
    public ResponseEntity<?> calcularTotalCliente(@PathVariable Long clienteId) {
        try {
            Long negocioId = obtenerNegocioId();
            BigDecimal total = ventaService.calcularTotalComprasCliente(negocioId, clienteId);

            Map<String, Object> response = new HashMap<>();
            response.put("clienteId", clienteId);
            response.put("total", total);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * Extrae negocioId del contexto de seguridad JWT
     */
    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    /**
     * Extrae usuarioId del contexto de seguridad JWT
     */
    private Long obtenerUsuarioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No autorizado");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerUsuarioIdPorUuid(uuid);
    }

    /**
     * Construye respuesta de error
     */
    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
