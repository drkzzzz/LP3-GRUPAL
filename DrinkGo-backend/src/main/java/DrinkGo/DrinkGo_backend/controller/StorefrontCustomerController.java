package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.Clientes;
import DrinkGo.DrinkGo_backend.entity.Combos;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;
import DrinkGo.DrinkGo_backend.entity.Devoluciones;
import DrinkGo.DrinkGo_backend.entity.MetodosPago;
import DrinkGo.DrinkGo_backend.entity.PagosPedido;
import DrinkGo.DrinkGo_backend.entity.Pedidos;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.entity.Sedes;
import DrinkGo.DrinkGo_backend.entity.ZonasDelivery;
import DrinkGo.DrinkGo_backend.repository.ClientesRepository;
import DrinkGo.DrinkGo_backend.repository.CombosRepository;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionTiendaOnlineRepository;
import DrinkGo.DrinkGo_backend.repository.DevolucionesRepository;
import DrinkGo.DrinkGo_backend.repository.MetodosPagoRepository;
import DrinkGo.DrinkGo_backend.repository.PagosPedidoRepository;
import DrinkGo.DrinkGo_backend.repository.PedidosRepository;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.repository.SedesRepository;
import DrinkGo.DrinkGo_backend.repository.ZonasDeliveryRepository;
import DrinkGo.DrinkGo_backend.service.IPedidosService;
import DrinkGo.DrinkGo_backend.service.jpa.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import DrinkGo.DrinkGo_backend.entity.DetalleDevoluciones;
import DrinkGo.DrinkGo_backend.entity.Negocios;
import DrinkGo.DrinkGo_backend.repository.DetalleDevolucionesRepository;
import DrinkGo.DrinkGo_backend.repository.DetallePedidosRepository;
import DrinkGo.DrinkGo_backend.service.IDevolucionesService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Endpoints de cliente autenticado del storefront.
 * Requieren JWT del cliente en el header Authorization.
 * Rutas: /restful/tienda/{slug}/...
 */
@RestController
@RequestMapping("/restful/tienda/{slug}")
public class StorefrontCustomerController {

    @Autowired
    private ClientesRepository clientesRepo;

    @Autowired
    private PedidosRepository pedidosRepo;

    @Autowired
    private DevolucionesRepository devolucionesRepo;

    @Autowired
    private IPedidosService pedidosService;

    @Autowired
    private ConfiguracionTiendaOnlineRepository configRepo;

    @Autowired
    private CombosRepository combosRepo;

    @Autowired
    private SedesRepository sedesRepo;

    @Autowired
    private ProductosRepository productosRepo;

    @Autowired
    private MetodosPagoRepository metodosPagoRepo;

    @Autowired
    private ZonasDeliveryRepository zonasDeliveryRepo;

    @Autowired
    private PagosPedidoRepository pagosPedidoRepo;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private IDevolucionesService devolucionesService;

    @Autowired
    private DetalleDevolucionesRepository detalleDevolucionesRepo;

    @Autowired
    private DetallePedidosRepository detallePedidosRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /* ── Helper: obtener clienteId del JWT ── */
    private Long getClienteId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal.toString());
    }

    /* ── Helper: build response without passwordHash ── */
    private Map<String, Object> buildClienteResponse(Clientes c) {
        return Map.of(
                "id", c.getId(),
                "nombres", c.getNombres() != null ? c.getNombres() : "",
                "apellidos", c.getApellidos() != null ? c.getApellidos() : "",
                "email", c.getEmail() != null ? c.getEmail() : "",
                "telefono", c.getTelefono() != null ? c.getTelefono() : "",
                "direccion", c.getDireccion() != null ? c.getDireccion() : "",
                "tipoDocumento", c.getTipoDocumento() != null ? c.getTipoDocumento().name() : "",
                "numeroDocumento", c.getNumeroDocumento() != null ? c.getNumeroDocumento() : "");
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/{slug}/mi-perfil
     * ══════════════════════════════════════════════
     */
    @GetMapping("/mi-perfil")
    public ResponseEntity<?> getMiPerfil(@PathVariable String slug) {
        Long clienteId = getClienteId();
        Optional<Clientes> clienteOpt = clientesRepo.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cliente no encontrado"));
        }
        return ResponseEntity.ok(buildClienteResponse(clienteOpt.get()));
    }

    /*
     * ══════════════════════════════════════════════
     * PUT /restful/tienda/{slug}/mi-perfil
     * Body: { nombres, apellidos, telefono, direccion }
     * ══════════════════════════════════════════════
     */
    @PutMapping("/mi-perfil")
    public ResponseEntity<?> updateMiPerfil(
            @PathVariable String slug,
            @RequestBody Map<String, String> body) {

        Long clienteId = getClienteId();
        Optional<Clientes> clienteOpt = clientesRepo.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cliente no encontrado"));
        }

        Clientes cliente = clienteOpt.get();

        if (body.containsKey("nombres"))
            cliente.setNombres(body.get("nombres"));
        if (body.containsKey("apellidos"))
            cliente.setApellidos(body.get("apellidos"));
        if (body.containsKey("telefono"))
            cliente.setTelefono(body.get("telefono"));
        if (body.containsKey("direccion"))
            cliente.setDireccion(body.get("direccion"));

        clientesRepo.save(cliente);

        return ResponseEntity.ok(buildClienteResponse(cliente));
    }

    /*
     * ══════════════════════════════════════════════
     * PUT /restful/tienda/{slug}/mi-perfil/password
     * Body: { currentPassword, newPassword }
     * ══════════════════════════════════════════════
     */
    @PutMapping("/mi-perfil/password")
    public ResponseEntity<?> changePassword(
            @PathVariable String slug,
            @RequestBody Map<String, String> body) {

        Long clienteId = getClienteId();
        Optional<Clientes> clienteOpt = clientesRepo.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cliente no encontrado"));
        }

        Clientes cliente = clienteOpt.get();
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "currentPassword y newPassword son requeridos"));
        }

        if (cliente.getPasswordHash() == null ||
                !passwordEncoder.matches(currentPassword, cliente.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "La contraseña actual es incorrecta"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "La nueva contraseña debe tener al menos 6 caracteres"));
        }

        cliente.setPasswordHash(passwordEncoder.encode(newPassword));
        clientesRepo.save(cliente);

        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
    }

    /*
     * ══════════════════════════════════════════════
     * POST /restful/tienda/{slug}/pedidos
     * Body: { sedeId?, tipoPedido, origenPedido, metodoPagoId?,
     * observaciones?, direccionEntrega?, departamento?, provincia?,
     * distrito?, referencia?, zonaDeliveryId?,
     * tipoComprobante?, docClienteNumero?, docClienteNombre?, docClienteDireccion?,
     * detalles: [{ productoId, cantidad, precioUnitario }] }
     * ══════════════════════════════════════════════
     */
    @PostMapping("/pedidos")
    public ResponseEntity<?> crearPedido(
            @PathVariable String slug,
            @RequestBody Map<String, Object> body) {

        Long clienteId = getClienteId();

        // 1. Resolver negocio desde el slug
        var configOpt = configRepo.findBySlugTienda(slug);
        if (configOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tienda no encontrada"));
        }
        var negocio = configOpt.get().getNegocio();
        if (negocio == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "La tienda no tiene un negocio asociado"));
        }

        // 2. Cliente autenticado
        Optional<Clientes> clienteOpt = clientesRepo.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cliente no encontrado"));
        }

        // 3. Construir el pedido
        Pedidos pedido = new Pedidos();
        pedido.setNegocio(negocio);
        pedido.setCliente(clienteOpt.get());
        pedido.setOrigenPedido(Pedidos.OrigenPedido.tienda_online);

        // 4. Sede (opcional)
        Object sedeIdRaw = body.get("sedeId");
        if (sedeIdRaw != null) {
            try {
                Long sedeId = Long.parseLong(sedeIdRaw.toString());
                sedesRepo.findById(sedeId).ifPresent(pedido::setSede);
            } catch (NumberFormatException ignored) {
            }
        }

        // 5. Tipo de pedido
        String tipoPedidoStr = (String) body.getOrDefault("tipoPedido", "recojo_tienda");
        try {
            pedido.setTipoPedido(Pedidos.TipoPedido.valueOf(tipoPedidoStr));
        } catch (IllegalArgumentException e) {
            pedido.setTipoPedido(Pedidos.TipoPedido.recojo_tienda);
        }

        // 6. Método de pago → guardar nombre como String
        Object metodoPagoIdRaw = body.get("metodoPagoId");
        if (metodoPagoIdRaw != null) {
            try {
                Long metodoPagoId = Long.parseLong(metodoPagoIdRaw.toString());
                Optional<MetodosPago> metodoOpt = metodosPagoRepo.findById(metodoPagoId);
                metodoOpt.ifPresent(m -> pedido.setMetodoPago(m.getNombre()));
            } catch (NumberFormatException ignored) {
            }
        }

        // 7. Campos de dirección
        if (body.get("direccionEntrega") != null)
            pedido.setDireccionEntrega(body.get("direccionEntrega").toString());
        if (body.get("departamento") != null)
            pedido.setDepartamento(body.get("departamento").toString());
        if (body.get("provincia") != null)
            pedido.setProvincia(body.get("provincia").toString());
        if (body.get("distrito") != null)
            pedido.setDistrito(body.get("distrito").toString());
        if (body.get("referencia") != null)
            pedido.setReferencia(body.get("referencia").toString());
        if (body.get("observaciones") != null)
            pedido.setObservaciones(body.get("observaciones").toString());

        // 8. Zona de delivery
        BigDecimal costoDelivery = BigDecimal.ZERO;
        Object zonaIdRaw = body.get("zonaDeliveryId");
        if (zonaIdRaw != null) {
            try {
                Long zonaId = Long.parseLong(zonaIdRaw.toString());
                Optional<ZonasDelivery> zonaOpt = zonasDeliveryRepo.findById(zonaId);
                if (zonaOpt.isPresent()) {
                    pedido.setZonaDelivery(zonaOpt.get());
                    costoDelivery = zonaOpt.get().getTarifaDelivery() != null
                            ? zonaOpt.get().getTarifaDelivery()
                            : BigDecimal.ZERO;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        pedido.setCostoDelivery(costoDelivery);

        // 9. Comprobante
        String tipoComprobante = body.containsKey("tipoComprobante")
                ? body.get("tipoComprobante").toString()
                : "boleta";
        pedido.setTipoComprobante(tipoComprobante);
        if (body.get("docClienteNumero") != null)
            pedido.setDocClienteNumero(body.get("docClienteNumero").toString());
        if (body.get("docClienteNombre") != null)
            pedido.setDocClienteNombre(body.get("docClienteNombre").toString());
        if ("factura".equals(tipoComprobante) && body.get("docClienteDireccion") != null)
            pedido.setDocClienteDireccion(body.get("docClienteDireccion").toString());

        // 10. Detalles del pedido
        Object detallesRaw = body.get("detalles");
        if (!(detallesRaw instanceof List<?> detallesList) || detallesList.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El pedido debe tener al menos un producto"));
        }

        List<DetallePedidos> detalles = new ArrayList<>();
        BigDecimal subtotalPedido = BigDecimal.ZERO;

        for (Object itemRaw : detallesList) {
            if (!(itemRaw instanceof Map<?, ?> item))
                continue;

            // Soporte para ítem de combo o producto
            Object comboIdRaw = item.get("comboId");
            Object productoIdRaw = item.get("productoId");
            if (comboIdRaw == null && productoIdRaw == null)
                continue;

            BigDecimal cantidad;
            try {
                Object cantidadRaw = item.get("cantidad");
                cantidad = new BigDecimal(cantidadRaw != null ? cantidadRaw.toString() : "1");
            } catch (Exception e) {
                cantidad = BigDecimal.ONE;
            }

            DetallePedidos detalle = new DetallePedidos();
            BigDecimal precioUnitario;

            if (comboIdRaw != null) {
                // Ítem de combo
                Long comboId;
                try {
                    comboId = Long.parseLong(comboIdRaw.toString());
                } catch (NumberFormatException e) {
                    continue;
                }
                Optional<Combos> comboOpt = combosRepo.findById(comboId);
                if (comboOpt.isEmpty())
                    continue;
                Combos combo = comboOpt.get();
                try {
                    precioUnitario = new BigDecimal(item.get("precioUnitario").toString());
                } catch (Exception e) {
                    precioUnitario = combo.getPrecioCombo() != null
                            ? combo.getPrecioCombo()
                            : BigDecimal.ZERO;
                }
                detalle.setCombo(combo);
            } else {
                // Ítem de producto
                Long productoId;
                try {
                    productoId = Long.parseLong(productoIdRaw.toString());
                } catch (NumberFormatException e) {
                    continue;
                }
                Optional<Productos> productoOpt = productosRepo.findById(productoId);
                if (productoOpt.isEmpty())
                    continue;
                Productos producto = productoOpt.get();
                try {
                    precioUnitario = new BigDecimal(item.get("precioUnitario").toString());
                } catch (Exception e) {
                    precioUnitario = producto.getPrecioVenta() != null
                            ? producto.getPrecioVenta()
                            : BigDecimal.ZERO;
                }
                detalle.setProducto(producto);
            }

            BigDecimal subtotalDetalle = precioUnitario.multiply(cantidad);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotalDetalle);
            detalle.setTotal(subtotalDetalle);

            detalles.add(detalle);
            subtotalPedido = subtotalPedido.add(subtotalDetalle);
        }

        if (detalles.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "No se pudo procesar ningún producto del pedido"));
        }

        pedido.setDetalles(detalles);
        pedido.setSubtotal(subtotalPedido);
        pedido.setTotal(subtotalPedido.add(costoDelivery));

        // 11. Persistir (genera numeroPedido automáticamente)
        try {
            pedidosService.guardar(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al crear el pedido: " + e.getMessage()));
        }

        // 12. Crear registro de pagos_pedido automáticamente
        try {
            if (metodoPagoIdRaw != null) {
                Long mpId = Long.parseLong(metodoPagoIdRaw.toString());
                metodosPagoRepo.findById(mpId).ifPresent(metodo -> {
                    PagosPedido pago = new PagosPedido();
                    pago.setPedido(pedido);
                    pago.setMetodoPago(metodo);
                    pago.setMonto(pedido.getTotal());
                    pago.setEstadoPago(PagosPedido.EstadoPago.pendiente);
                    if (body.get("banco") != null)
                        pago.setBanco(body.get("banco").toString());
                    if (body.get("ultimosCuatroDigitos") != null)
                        pago.setUltimosCuatroDigitos(body.get("ultimosCuatroDigitos").toString());
                    if (body.get("nombreTitular") != null)
                        pago.setNombreTitular(body.get("nombreTitular").toString());
                    if (body.get("numeroReferencia") != null)
                        pago.setNumeroReferencia(body.get("numeroReferencia").toString());
                    pagosPedidoRepo.save(pago);
                });
            }
        } catch (Exception e) {
            System.err.println(
                    "⚠️ No se pudo crear registro de pago para pedido " + pedido.getId() + ": " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", pedido.getId(),
                "numeroPedido", pedido.getNumeroPedido(),
                "total", pedido.getTotal(),
                "estadoPedido", pedido.getEstadoPedido().name(),
                "tipoComprobante", pedido.getTipoComprobante(),
                "message", "Pedido creado exitosamente"));
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/{slug}/pedidos/{pedidoId}/pago
     * Devuelve el pago registrado del pedido (para el cliente)
     * ══════════════════════════════════════════════
     */
    @GetMapping("/pedidos/{pedidoId}/pago")
    public ResponseEntity<?> getMiPedidoPago(
            @PathVariable String slug,
            @PathVariable Long pedidoId) {

        Long clienteId = getClienteId();
        Pedidos pedido = pedidosRepo.findById(pedidoId).orElse(null);
        if (pedido == null || pedido.getCliente() == null || !clienteId.equals(pedido.getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Sin acceso"));
        }
        List<PagosPedido> pagos = pagosPedidoRepo.findByPedidoId(pedidoId);
        return ResponseEntity.ok(pagos.isEmpty() ? null : pagos.get(0));
    }

    /*
     * ══════════════════════════════════════════════
     * POST /restful/tienda/{slug}/pedidos/{pedidoId}/comprobante
     * El cliente sube imagen de Yape/Plin como comprobante
     * ══════════════════════════════════════════════
     */
    @PostMapping(value = "/pedidos/{pedidoId}/comprobante", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirComprobante(
            @PathVariable String slug,
            @PathVariable Long pedidoId,
            @RequestPart("archivo") MultipartFile archivo) {

        Long clienteId = getClienteId();
        Pedidos pedido = pedidosRepo.findById(pedidoId).orElse(null);
        if (pedido == null || pedido.getCliente() == null || !clienteId.equals(pedido.getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Sin acceso"));
        }

        List<PagosPedido> pagos = pagosPedidoRepo.findByPedidoId(pedidoId);
        if (pagos.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No hay registro de pago para este pedido"));
        }

        try {
            String path = fileStorageService.guardar(archivo, "comprobantes_pago");
            PagosPedido pago = pagos.get(0);
            pago.setUrlComprobante(path);
            pagosPedidoRepo.save(pago);
            return ResponseEntity.ok(Map.of("urlComprobante", path, "message", "Comprobante subido correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al subir el comprobante: " + e.getMessage()));
        }
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/{slug}/mis-pedidos
     * ══════════════════════════════════════════════
     */
    @GetMapping("/mis-pedidos")
    public ResponseEntity<?> getMisPedidos(@PathVariable String slug) {
        Long clienteId = getClienteId();
        List<Pedidos> pedidos = pedidosRepo.findByClienteId(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/{slug}/mis-pedidos/{pedidoId}
     * ══════════════════════════════════════════════
     */
    @GetMapping("/mis-pedidos/{pedidoId}")
    public ResponseEntity<?> getMiPedido(
            @PathVariable String slug,
            @PathVariable Long pedidoId) {

        Long clienteId = getClienteId();
        Optional<Pedidos> pedidoOpt = pedidosRepo.findById(pedidoId);

        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pedido no encontrado"));
        }

        Pedidos pedido = pedidoOpt.get();

        // Verificar que el pedido pertenece al cliente autenticado
        if (pedido.getCliente() == null || !clienteId.equals(pedido.getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "No tiene acceso a este pedido"));
        }

        return ResponseEntity.ok(pedido);
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/{slug}/mis-devoluciones
     * ══════════════════════════════════════════════
     */
    @GetMapping("/mis-devoluciones")
    public ResponseEntity<?> getMisDevoluciones(@PathVariable String slug) {
        Long clienteId = getClienteId();
        List<Devoluciones> devoluciones = devolucionesRepo.findByClienteId(clienteId);
        return ResponseEntity.ok(devoluciones);
    }

    /*
     * ══════════════════════════════════════════════
     * POST /restful/tienda/{slug}/solicitar-devolucion
     * Body: { pedidoId, tipoDevolucion, categoriaMotivo,
     * detalleMotivo, metodoReembolso, notas?,
     * productos: [{ detallePedidoId, cantidad }] }
     * ══════════════════════════════════════════════
     */
    @PostMapping("/solicitar-devolucion")
    public ResponseEntity<?> solicitarDevolucion(
            @PathVariable String slug,
            @RequestBody Map<String, Object> body) {

        Long clienteId = getClienteId();

        // 1. Resolver negocio desde el slug
        var configOpt = configRepo.findBySlugTienda(slug);
        if (configOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tienda no encontrada"));
        }
        Negocios negocio = configOpt.get().getNegocio();
        if (negocio == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "La tienda no tiene un negocio asociado"));
        }

        // 2. Cliente autenticado
        Optional<Clientes> clienteOpt = clientesRepo.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Cliente no encontrado"));
        }
        Clientes cliente = clienteOpt.get();

        // 3. Validar pedidoId
        Object pedidoIdRaw = body.get("pedidoId");
        if (pedidoIdRaw == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "pedidoId es requerido"));
        }
        Long pedidoId;
        try {
            pedidoId = Long.parseLong(pedidoIdRaw.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "pedidoId inválido"));
        }

        Optional<Pedidos> pedidoOpt = pedidosRepo.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pedido no encontrado"));
        }
        Pedidos pedido = pedidoOpt.get();

        // Verificar que el pedido pertenece al cliente
        if (pedido.getCliente() == null || !clienteId.equals(pedido.getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "No tiene acceso a este pedido"));
        }

        // Verificar que el pedido esté entregado
        if (pedido.getEstadoPedido() != Pedidos.EstadoPedido.entregado) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Solo se pueden solicitar devoluciones de pedidos entregados"));
        }

        // Verificar que no exista ya una devolución para este pedido
        if (devolucionesRepo.existsByPedidoId(pedidoId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una solicitud de devolución para este pedido"));
        }

        // 4. Validar campos requeridos
        String tipoDevolucion = (String) body.get("tipoDevolucion");
        String categoriaMotivo = (String) body.get("categoriaMotivo");
        String detalleMotivo = (String) body.get("detalleMotivo");
        String metodoReembolso = (String) body.get("metodoReembolso");
        String notas = (String) body.get("notas");

        if (tipoDevolucion == null || categoriaMotivo == null || detalleMotivo == null || metodoReembolso == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Campos requeridos: tipoDevolucion, categoriaMotivo, detalleMotivo, metodoReembolso"));
        }

        // 5. Obtener detalles del pedido para calcular montos
        List<DetallePedidos> detallesPedido = detallePedidosRepo.findByPedido_Id(pedidoId);

        // 6. Parsear productos a devolver
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> productosBody = (List<Map<String, Object>>) body.get("productos");
        if (productosBody == null || productosBody.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Debe seleccionar al menos un producto a devolver"));
        }

        // 7. Crear la devolución cabecera
        Devoluciones devolucion = new Devoluciones();
        devolucion.setNegocio(negocio);
        devolucion.setSede(pedido.getSede());
        devolucion.setPedido(pedido);
        devolucion.setCliente(cliente);
        devolucion.setTipoDevolucion(Devoluciones.TipoDevolucion.valueOf(tipoDevolucion));
        devolucion.setCategoriaMotivo(Devoluciones.CategoriaMotivo.valueOf(categoriaMotivo));
        devolucion.setDetalleMotivo(detalleMotivo);
        devolucion.setMetodoReembolso(Devoluciones.MetodoReembolso.valueOf(metodoReembolso));
        devolucion.setEstado(Devoluciones.EstadoDevolucion.solicitada);
        devolucion.setNotas(notas);

        // Calcular totales según productos seleccionados
        BigDecimal totalDev = BigDecimal.ZERO;
        List<DetalleDevoluciones> detallesDevolucion = new ArrayList<>();

        for (Map<String, Object> prod : productosBody) {
            Long detallePedidoId = Long.parseLong(prod.get("detallePedidoId").toString());
            int cantidad = Integer.parseInt(prod.get("cantidad").toString());

            DetallePedidos detallePedido = detallesPedido.stream()
                    .filter(dp -> dp.getId().equals(detallePedidoId))
                    .findFirst()
                    .orElse(null);

            if (detallePedido == null)
                continue;

            BigDecimal precioUnit = detallePedido.getPrecioUnitario();
            BigDecimal totalItem = precioUnit.multiply(BigDecimal.valueOf(cantidad));
            totalDev = totalDev.add(totalItem);

            DetalleDevoluciones detDev = new DetalleDevoluciones();
            detDev.setProducto(detallePedido.getProducto());
            detDev.setDetallePedido(detallePedido);
            detDev.setCantidad(cantidad);
            detDev.setPrecioUnitario(precioUnit);
            detDev.setTotal(totalItem);
            detDev.setEstadoCondicion(DetalleDevoluciones.EstadoCondicion.bueno);
            detDev.setDevolverStock(true);
            detallesDevolucion.add(detDev);
        }

        devolucion.setSubtotal(totalDev);
        devolucion.setMontoImpuesto(BigDecimal.ZERO);
        devolucion.setTotal(totalDev);

        // 8. Guardar la devolución (genera número automáticamente)
        devolucionesService.guardar(devolucion);

        // 9. Guardar los detalles
        for (DetalleDevoluciones detDev : detallesDevolucion) {
            detDev.setDevolucion(devolucion);
            detalleDevolucionesRepo.save(detDev);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", devolucion.getId(),
                "numeroDevolucion", devolucion.getNumeroDevolucion() != null ? devolucion.getNumeroDevolucion() : "",
                "estado", devolucion.getEstado().name(),
                "total", devolucion.getTotal(),
                "message", "Solicitud de devolución registrada correctamente"));
    }
}
