package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.DetalleOrdenCompraRequest;
import DrinkGo.DrinkGo_backend.dto.OrdenCompraCreateRequest;
import DrinkGo.DrinkGo_backend.dto.OrdenCompraUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.DetalleOrdenCompra;
import DrinkGo.DrinkGo_backend.entity.OrdenCompra;
import DrinkGo.DrinkGo_backend.entity.OrdenCompra.EstadoOrden;
import DrinkGo.DrinkGo_backend.repository.DetalleOrdenCompraRepository;
import DrinkGo.DrinkGo_backend.repository.OrdenCompraRepository;
import DrinkGo.DrinkGo_backend.repository.ProveedorRepository;

/**
 * Servicio de Órdenes de Compra - Bloque 6.
 * Implementa RF-COM-004 a RF-COM-007:
 * - CRUD completo de órdenes de compra con detalle
 * - Cálculo automático de totales (subtotal, impuesto, descuento, total)
 * - Validación de estados
 * - Filtrado multi-tenant por negocio_id
 */
@Service
public class OrdenCompraService {

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private DetalleOrdenCompraRepository detalleRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    /**
     * Listar todas las órdenes de compra del negocio autenticado.
     */
    public List<OrdenCompra> listarOrdenes(Long negocioId) {
        return ordenCompraRepository.findByNegocioId(negocioId);
    }

    /**
     * Obtener una orden de compra por ID con sus detalles.
     * Devuelve un Map con la orden y la lista de items.
     */
    public Map<String, Object> obtenerOrden(Long negocioId, Long id) {
        OrdenCompra orden = ordenCompraRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Orden de compra no encontrada con id " + id + " para el negocio actual"));

        List<DetalleOrdenCompra> items = detalleRepository.findByOrdenCompraId(id);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("orden", orden);
        resultado.put("items", items);
        return resultado;
    }

    /**
     * Crear una nueva orden de compra con sus items.
     * Calcula automáticamente subtotal, impuesto, descuento y total.
     */
    @Transactional
    public Map<String, Object> crearOrden(Long negocioId, Long usuarioId, OrdenCompraCreateRequest request) {
        // Validar campos obligatorios
        if (request.getNumeroOrden() == null || request.getNumeroOrden().isBlank()) {
            throw new RuntimeException("El número de orden es obligatorio");
        }
        if (request.getProveedorId() == null) {
            throw new RuntimeException("El proveedor es obligatorio");
        }
        if (request.getSedeId() == null) {
            throw new RuntimeException("La sede es obligatoria");
        }
        if (request.getAlmacenId() == null) {
            throw new RuntimeException("El almacén es obligatorio");
        }

        // Validar unicidad del número de orden dentro del negocio
        if (ordenCompraRepository.existsByNegocioIdAndNumeroOrden(negocioId, request.getNumeroOrden())) {
            throw new RuntimeException("Ya existe una orden con número '" + request.getNumeroOrden() + "' en este negocio");
        }

        // Validar que el proveedor pertenezca al negocio
        proveedorRepository.findByIdAndNegocioId(request.getProveedorId(), negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Proveedor no encontrado con id " + request.getProveedorId() + " para el negocio actual"));

        // Validar que se envíen items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("La orden debe tener al menos un item");
        }

        // Crear la orden
        OrdenCompra orden = new OrdenCompra();
        orden.setNegocioId(negocioId);
        orden.setNumeroOrden(request.getNumeroOrden());
        orden.setProveedorId(request.getProveedorId());
        orden.setSedeId(request.getSedeId());
        orden.setAlmacenId(request.getAlmacenId());
        orden.setEstado(EstadoOrden.borrador);
        if (request.getMoneda() != null) orden.setMoneda(request.getMoneda());
        orden.setFechaEntregaEsperada(request.getFechaEntregaEsperada());
        orden.setPlazoPagoDias(request.getPlazoPagoDias());
        orden.setNotas(request.getNotas());
        orden.setCreadoPor(usuarioId);

        // Guardar orden primero para obtener el ID
        orden = ordenCompraRepository.save(orden);

        // Crear items y calcular totales
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        BigDecimal impuestoGeneral = BigDecimal.ZERO;
        BigDecimal descuentoGeneral = BigDecimal.ZERO;

        for (DetalleOrdenCompraRequest itemReq : request.getItems()) {
            DetalleOrdenCompra item = crearDetalleItem(orden.getId(), itemReq);
            detalleRepository.save(item);

            subtotalGeneral = subtotalGeneral.add(item.getSubtotal());
            impuestoGeneral = impuestoGeneral.add(item.getMontoImpuesto());
            descuentoGeneral = descuentoGeneral.add(
                    item.getSubtotal().multiply(item.getPorcentajeDescuento())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        }

        // Actualizar totales en la orden
        orden.setSubtotal(subtotalGeneral);
        orden.setMontoImpuesto(impuestoGeneral);
        orden.setMontoDescuento(descuentoGeneral);
        orden.setTotal(subtotalGeneral.add(impuestoGeneral).subtract(descuentoGeneral));
        orden = ordenCompraRepository.save(orden);

        List<DetalleOrdenCompra> items = detalleRepository.findByOrdenCompraId(orden.getId());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("orden", orden);
        resultado.put("items", items);
        return resultado;
    }

    /**
     * Actualizar una orden de compra.
     * Solo se puede modificar completamente si está en estado 'borrador'.
     * Si se envía un cambio de estado, se aplica la transición.
     */
    @Transactional
    public Map<String, Object> actualizarOrden(Long negocioId, Long id, Long usuarioId,
                                                OrdenCompraUpdateRequest request) {
        OrdenCompra orden = ordenCompraRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Orden de compra no encontrada con id " + id + " para el negocio actual"));

        // Procesar cambio de estado si se envía
        if (request.getEstado() != null) {
            EstadoOrden nuevoEstado;
            try {
                nuevoEstado = EstadoOrden.valueOf(request.getEstado());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido: " + request.getEstado() +
                        ". Valores permitidos: borrador, pendiente_aprobacion, aprobada, enviada, recepcion_parcial, recibida, cancelada");
            }

            validarTransicionEstado(orden.getEstado(), nuevoEstado);

            // Registrar timestamps según transición
            switch (nuevoEstado) {
                case aprobada:
                    orden.setAprobadoPor(usuarioId);
                    orden.setAprobadoEn(LocalDateTime.now());
                    break;
                case enviada:
                    orden.setEnviadoEn(LocalDateTime.now());
                    break;
                case recibida:
                case recepcion_parcial:
                    orden.setRecibidoPor(usuarioId);
                    orden.setRecibidoEn(LocalDateTime.now());
                    break;
                case cancelada:
                    orden.setCanceladoEn(LocalDateTime.now());
                    if (request.getRazonCancelacion() != null) {
                        orden.setRazonCancelacion(request.getRazonCancelacion());
                    }
                    break;
                default:
                    break;
            }

            orden.setEstado(nuevoEstado);
        }

        // Solo permitir edición de campos si está en borrador
        if (orden.getEstado() == EstadoOrden.borrador) {
            if (request.getMoneda() != null) orden.setMoneda(request.getMoneda());
            if (request.getFechaEntregaEsperada() != null) orden.setFechaEntregaEsperada(request.getFechaEntregaEsperada());
            if (request.getPlazoPagoDias() != null) orden.setPlazoPagoDias(request.getPlazoPagoDias());
            if (request.getNotas() != null) orden.setNotas(request.getNotas());

            // Si se envían items, reemplazar todos los existentes
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                detalleRepository.deleteByOrdenCompraId(id);

                BigDecimal subtotalGeneral = BigDecimal.ZERO;
                BigDecimal impuestoGeneral = BigDecimal.ZERO;
                BigDecimal descuentoGeneral = BigDecimal.ZERO;

                for (DetalleOrdenCompraRequest itemReq : request.getItems()) {
                    DetalleOrdenCompra item = crearDetalleItem(id, itemReq);
                    detalleRepository.save(item);

                    subtotalGeneral = subtotalGeneral.add(item.getSubtotal());
                    impuestoGeneral = impuestoGeneral.add(item.getMontoImpuesto());
                    descuentoGeneral = descuentoGeneral.add(
                            item.getSubtotal().multiply(item.getPorcentajeDescuento())
                                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                }

                orden.setSubtotal(subtotalGeneral);
                orden.setMontoImpuesto(impuestoGeneral);
                orden.setMontoDescuento(descuentoGeneral);
                orden.setTotal(subtotalGeneral.add(impuestoGeneral).subtract(descuentoGeneral));
            }
        }

        orden = ordenCompraRepository.save(orden);
        List<DetalleOrdenCompra> items = detalleRepository.findByOrdenCompraId(orden.getId());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("orden", orden);
        resultado.put("items", items);
        return resultado;
    }

    /**
     * Eliminar (cancelar) una orden de compra.
     * Solo se puede cancelar si está en estado 'borrador' o 'pendiente_aprobacion'.
     */
    @Transactional
    public void eliminarOrden(Long negocioId, Long id) {
        OrdenCompra orden = ordenCompraRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Orden de compra no encontrada con id " + id + " para el negocio actual"));

        if (orden.getEstado() != EstadoOrden.borrador && orden.getEstado() != EstadoOrden.pendiente_aprobacion) {
            throw new RuntimeException("Solo se pueden eliminar órdenes en estado 'borrador' o 'pendiente_aprobacion'. " +
                    "Estado actual: " + orden.getEstado());
        }

        // Eliminar items primero
        detalleRepository.deleteByOrdenCompraId(id);

        // Cambiar estado a cancelada (borrado lógico de negocio)
        orden.setEstado(EstadoOrden.cancelada);
        orden.setCanceladoEn(LocalDateTime.now());
        orden.setRazonCancelacion("Eliminada por el usuario");
        ordenCompraRepository.save(orden);
    }

    // ============================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ============================================================

    /**
     * Crear un DetalleOrdenCompra a partir del DTO y calcular montos.
     * Incluye mapeo de campos de recepción simplificados.
     */
    private DetalleOrdenCompra crearDetalleItem(Long ordenId, DetalleOrdenCompraRequest itemReq) {
        if (itemReq.getProductoId() == null) {
            throw new RuntimeException("El producto es obligatorio en cada item");
        }
        if (itemReq.getCantidadOrdenada() == null || itemReq.getCantidadOrdenada() <= 0) {
            throw new RuntimeException("La cantidad ordenada debe ser mayor a 0");
        }
        if (itemReq.getPrecioUnitario() == null || itemReq.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio unitario no puede ser negativo");
        }

        DetalleOrdenCompra item = new DetalleOrdenCompra();
        item.setOrdenCompraId(ordenId);
        item.setProductoId(itemReq.getProductoId());
        item.setCantidadOrdenada(itemReq.getCantidadOrdenada());

        // Mapeo de campos de recepción (opcionales)
        if (itemReq.getCantidadRecibida() != null) {
            item.setCantidadRecibida(itemReq.getCantidadRecibida());
        }
        if (itemReq.getCantidadRechazada() != null) {
            item.setCantidadRechazada(itemReq.getCantidadRechazada());
        }
        if (itemReq.getRazonRechazo() != null) {
            item.setRazonRechazo(itemReq.getRazonRechazo());
        }

        BigDecimal precioUnitario = itemReq.getPrecioUnitario();
        item.setPrecioUnitario(precioUnitario);

        BigDecimal tasaImpuesto = itemReq.getTasaImpuesto() != null
                ? itemReq.getTasaImpuesto()
                : new BigDecimal("18.00");
        item.setTasaImpuesto(tasaImpuesto);

        BigDecimal porcentajeDescuento = itemReq.getPorcentajeDescuento() != null
                ? itemReq.getPorcentajeDescuento()
                : BigDecimal.ZERO;
        item.setPorcentajeDescuento(porcentajeDescuento);

        // Calcular subtotal = cantidad * precio_unitario
        BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(itemReq.getCantidadOrdenada()));
        item.setSubtotal(subtotal);

        // Calcular impuesto
        BigDecimal montoImpuesto = subtotal.multiply(tasaImpuesto)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        item.setMontoImpuesto(montoImpuesto);

        // Calcular total = subtotal + impuesto - descuento
        BigDecimal descuento = subtotal.multiply(porcentajeDescuento)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(montoImpuesto).subtract(descuento);
        item.setTotal(total);

        if (itemReq.getNotas() != null) item.setNotas(itemReq.getNotas());

        return item;
    }

    /**
     * Validar transiciones de estado permitidas.
     */
    private void validarTransicionEstado(EstadoOrden estadoActual, EstadoOrden nuevoEstado) {
        boolean transicionValida = switch (estadoActual) {
            case borrador -> nuevoEstado == EstadoOrden.pendiente_aprobacion || nuevoEstado == EstadoOrden.cancelada;
            case pendiente_aprobacion -> nuevoEstado == EstadoOrden.aprobada || nuevoEstado == EstadoOrden.cancelada;
            case aprobada -> nuevoEstado == EstadoOrden.enviada || nuevoEstado == EstadoOrden.cancelada;
            case enviada -> nuevoEstado == EstadoOrden.recepcion_parcial || nuevoEstado == EstadoOrden.recibida || nuevoEstado == EstadoOrden.cancelada;
            case recepcion_parcial -> nuevoEstado == EstadoOrden.recibida;
            case recibida, cancelada -> false;
        };

        if (!transicionValida) {
            throw new RuntimeException("Transición de estado no permitida: " + estadoActual + " → " + nuevoEstado);
        }
    }
}