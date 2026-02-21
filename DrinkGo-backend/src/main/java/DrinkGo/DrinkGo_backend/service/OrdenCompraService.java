package DrinkGo.DrinkGo_backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * - Cálculo automático del total de la orden
 * - Validación de estados (pendiente, recibida, cancelada)
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
     * Calcula automáticamente el total sumando los totales de cada item.
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
        orden.setEstado(EstadoOrden.pendiente);
        orden.setNotas(request.getNotas());
        orden.setCreadoPor(usuarioId);

        // Guardar orden primero para obtener el ID
        orden = ordenCompraRepository.save(orden);

        // Crear items y calcular total general
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (DetalleOrdenCompraRequest itemReq : request.getItems()) {
            DetalleOrdenCompra item = crearDetalleItem(orden.getId(), itemReq);
            detalleRepository.save(item);
            totalGeneral = totalGeneral.add(item.getTotal());
        }

        // Actualizar total en la orden
        orden.setTotal(totalGeneral);
        orden = ordenCompraRepository.save(orden);

        List<DetalleOrdenCompra> items = detalleRepository.findByOrdenCompraId(orden.getId());

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("orden", orden);
        resultado.put("items", items);
        return resultado;
    }

    /**
     * Actualizar una orden de compra.
     * Solo se puede modificar completamente si está en estado 'pendiente'.
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
                        ". Valores permitidos: pendiente, recibida, cancelada");
            }

            validarTransicionEstado(orden.getEstado(), nuevoEstado);
            orden.setEstado(nuevoEstado);
        }

        // Solo permitir edición de campos si está en pendiente
        if (orden.getEstado() == EstadoOrden.pendiente) {
            if (request.getNotas() != null) orden.setNotas(request.getNotas());

            // Si se envían items, reemplazar todos los existentes
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                detalleRepository.deleteByOrdenCompraId(id);

                BigDecimal totalGeneral = BigDecimal.ZERO;

                for (DetalleOrdenCompraRequest itemReq : request.getItems()) {
                    DetalleOrdenCompra item = crearDetalleItem(id, itemReq);
                    detalleRepository.save(item);
                    totalGeneral = totalGeneral.add(item.getTotal());
                }

                orden.setTotal(totalGeneral);
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
     * Solo se puede cancelar si está en estado 'pendiente'.
     */
    @Transactional
    public void eliminarOrden(Long negocioId, Long id) {
        OrdenCompra orden = ordenCompraRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Orden de compra no encontrada con id " + id + " para el negocio actual"));

        if (orden.getEstado() != EstadoOrden.pendiente) {
            throw new RuntimeException("Solo se pueden eliminar órdenes en estado 'pendiente'. " +
                    "Estado actual: " + orden.getEstado());
        }

        // Eliminar items primero
        detalleRepository.deleteByOrdenCompraId(id);

        // Cambiar estado a cancelada (borrado lógico de negocio)
        orden.setEstado(EstadoOrden.cancelada);
        ordenCompraRepository.save(orden);
    }

    // ============================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ============================================================

    /**
     * Crear un DetalleOrdenCompra a partir del DTO y calcular montos.
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
     * pendiente → recibida | cancelada
     * recibida → (no se puede cambiar)
     * cancelada → (no se puede cambiar)
     */
    private void validarTransicionEstado(EstadoOrden estadoActual, EstadoOrden nuevoEstado) {
        boolean transicionValida = switch (estadoActual) {
            case pendiente -> nuevoEstado == EstadoOrden.recibida || nuevoEstado == EstadoOrden.cancelada;
            case recibida, cancelada -> false;
        };

        if (!transicionValida) {
            throw new RuntimeException("Transición de estado no permitida: " + estadoActual + " → " + nuevoEstado);
        }
    }
}
