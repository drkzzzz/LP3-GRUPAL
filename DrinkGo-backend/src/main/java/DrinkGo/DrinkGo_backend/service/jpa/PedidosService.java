package DrinkGo.DrinkGo_backend.service.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DrinkGo.DrinkGo_backend.entity.Almacenes;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;
import DrinkGo.DrinkGo_backend.entity.Pedidos;
import DrinkGo.DrinkGo_backend.repository.AlmacenesRepository;
import DrinkGo.DrinkGo_backend.repository.PedidosRepository;
import DrinkGo.DrinkGo_backend.service.IPedidosService;
import DrinkGo.DrinkGo_backend.service.InventarioTransaccionalService;

@Service
public class PedidosService implements IPedidosService {
    @Autowired
    private PedidosRepository repoPedidos;

    @Autowired
    private InventarioTransaccionalService inventarioService;

    @Autowired
    private AlmacenesRepository almacenesRepo;

    @Transactional(readOnly = true)
    public List<Pedidos> buscarTodos() {
        List<Pedidos> pedidos = repoPedidos.findAll();
        // Inicializar relaciones LAZY con protección contra entidades eliminadas
        for (Pedidos pedido : pedidos) {
            try {
                // Cliente
                if (pedido.getCliente() != null) {
                    pedido.getCliente().getNombres();
                    pedido.getCliente().getApellidos();
                    pedido.getCliente().getTelefono();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Cliente no encontrado para pedido " + pedido.getId());
            }
            try {
                // Zona delivery (si aplica)
                if (pedido.getZonaDelivery() != null) {
                    pedido.getZonaDelivery().getNombre();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Zona delivery no encontrada para pedido " + pedido.getId());
            }
            try {
                // Usuario que procesó (si hay)
                if (pedido.getUsuario() != null) {
                    pedido.getUsuario().getNombres();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Usuario no encontrado para pedido " + pedido.getId());
            }
            try {
                // Venta asociada (si ya se generó)
                if (pedido.getVenta() != null) {
                    pedido.getVenta().getNumeroVenta();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Venta no encontrada para pedido " + pedido.getId());
            }
            try {
                // DETALLES (productos del pedido)
                if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                    for (DetallePedidos detalle : pedido.getDetalles()) {
                        if (detalle.getProducto() != null) {
                            detalle.getProducto().getNombre();
                            detalle.getProducto().getSku();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error cargando detalles para pedido " + pedido.getId());
            }
        }
        return pedidos;
    }

    public void guardar(Pedidos pedidos) {
        // ✅ Generar número de pedido automáticamente si no existe
        if (pedidos.getNumeroPedido() == null || pedidos.getNumeroPedido().isEmpty()) {
            pedidos.setNumeroPedido(generarNumeroPedido());
        }
        repoPedidos.save(pedidos);

        // ✅ Reservar stock para cada producto del pedido
        reservarStockParaPedido(pedidos);
    }

    /**
     * Reserva stock en el almacén predeterminado de la sede del pedido.
     * Si no hay almacén configurado o no existe registro de stock, registra un
     * aviso pero NO interrumpe la creación del pedido.
     */
    private void reservarStockParaPedido(Pedidos pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) return;

        Long negocioId = pedido.getNegocio() != null ? pedido.getNegocio().getId() : null;
        Long almacenPreferidoId = null;
        if (pedido.getSede() != null) {
            almacenPreferidoId = almacenesRepo
                .findFirstBySede_IdAndEsPredeterminado(pedido.getSede().getId(), true)
                .map(Almacenes::getId)
                .orElse(null);
        }

        if (negocioId == null && almacenPreferidoId == null) {
            System.err.println("⚠️ Pedido " + pedido.getNumeroPedido() + " sin negocio ni sede — stock no reservado.");
            return;
        }

        for (DetallePedidos detalle : pedido.getDetalles()) {
            if (detalle.getProducto() == null) continue;
            Long productoId = detalle.getProducto().getId();
            BigDecimal cantidad = detalle.getCantidad();
            try {
                inventarioService.reservarStockConFallback(productoId, almacenPreferidoId, negocioId, cantidad);
                System.out.println("✅ Stock reservado — producto " + productoId + " x" + cantidad);
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo reservar stock para producto " + productoId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Genera un número de pedido único secuencial
     * Formato: PED-000001, PED-000002, etc.
     */
    private String generarNumeroPedido() {
        Pageable topOne = PageRequest.of(0, 1);
        Page<String> result = repoPedidos.findTopNumeroPedido(topOne);
        
        if (result.hasContent()) {
            String ultimoNumero = result.getContent().get(0);
            try {
                // Extraer el número del formato PED-XXXXXX
                int numero = Integer.parseInt(ultimoNumero.replace("PED-", "")) + 1;
                return String.format("PED-%06d", numero);
            } catch (Exception e) {
                // Si hay error parseando, empezar desde 1
                System.err.println("⚠️ Error parseando numero_pedido: " + ultimoNumero);
                return "PED-000001";
            }
        } else {
            // Primer pedido
            return "PED-000001";
        }
    }

    public void modificar(Pedidos pedidos) {
        // Detectar cambio de estado para manejar reservas de stock
        repoPedidos.findById(pedidos.getId()).ifPresent(anterior -> {
            Pedidos.EstadoPedido estadoAnterior = anterior.getEstadoPedido();
            Pedidos.EstadoPedido estadoNuevo = pedidos.getEstadoPedido();

            if (estadoAnterior != null && estadoNuevo != null && !estadoAnterior.equals(estadoNuevo)) {
                if (estadoNuevo == Pedidos.EstadoPedido.cancelado) {
                    // Liberar toda la reserva de stock
                    liberarStockParaPedido(anterior);
                } else if (estadoNuevo == Pedidos.EstadoPedido.entregado
                        && estadoAnterior != Pedidos.EstadoPedido.entregado) {
                    // Confirmar salida definitiva del inventario
                    confirmarSalidaStockParaPedido(anterior);
                }
            }
        });
        repoPedidos.save(pedidos);
    }

    /**
     * Cambia solo el estado de un pedido, cargando el registro completo desde BD.
     * Evita sobrescribir relaciones (negocio, sede, cliente) con nulos.
     */
    @Transactional
    public void cambiarEstado(Long pedidoId, String nuevoEstado) {
        Pedidos pedido = repoPedidos.findById(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + pedidoId));

        Pedidos.EstadoPedido estadoAnterior = pedido.getEstadoPedido();
        Pedidos.EstadoPedido estadoNuevo;
        try {
            estadoNuevo = Pedidos.EstadoPedido.valueOf(nuevoEstado);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado no v\u00e1lido: " + nuevoEstado);
        }

        if (estadoAnterior.equals(estadoNuevo)) {
            return; // nada que cambiar
        }

        // Manejar reservas de stock seg\u00fan transici\u00f3n
        if (estadoNuevo == Pedidos.EstadoPedido.cancelado) {
            liberarStockParaPedido(pedido);
        } else if (estadoNuevo == Pedidos.EstadoPedido.entregado) {
            confirmarSalidaStockParaPedido(pedido);
        }

        pedido.setEstadoPedido(estadoNuevo);
        repoPedidos.save(pedido);
        System.out.println("\u2705 Estado pedido " + pedido.getNumeroPedido() + ": " + estadoAnterior + " \u2192 " + estadoNuevo);
    }

    /**
     * Libera la reserva de stock cuando un pedido se cancela.
     */
    private void liberarStockParaPedido(Pedidos pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) return;

        Long negocioId = pedido.getNegocio() != null ? pedido.getNegocio().getId() : null;
        Long almacenPreferidoId = null;
        if (pedido.getSede() != null) {
            almacenPreferidoId = almacenesRepo
                .findFirstBySede_IdAndEsPredeterminado(pedido.getSede().getId(), true)
                .map(Almacenes::getId)
                .orElse(null);
        }

        for (DetallePedidos detalle : pedido.getDetalles()) {
            if (detalle.getProducto() == null) continue;
            Long productoId = detalle.getProducto().getId();
            BigDecimal cantidad = detalle.getCantidad();
            try {
                inventarioService.liberarReservaConFallback(productoId, almacenPreferidoId, negocioId, cantidad);
                System.out.println("✅ Reserva liberada — producto " + productoId + " x" + cantidad);
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo liberar reserva para producto " + productoId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Reducción física del stock cuando el pedido es marcado como entregado.
     * Descuenta cantidadActual y cantidadReservada (la reserva se convierte en salida real).
     */
    private void confirmarSalidaStockParaPedido(Pedidos pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) return;

        Long negocioId = pedido.getNegocio() != null ? pedido.getNegocio().getId() : null;
        Long usuarioId = pedido.getUsuario() != null ? pedido.getUsuario().getId() : null;

        if (negocioId == null || usuarioId == null) {
            liberarStockParaPedido(pedido);
            return;
        }

        Long almacenPreferidoId = null;
        if (pedido.getSede() != null) {
            almacenPreferidoId = almacenesRepo
                .findFirstBySede_IdAndEsPredeterminado(pedido.getSede().getId(), true)
                .map(Almacenes::getId)
                .orElse(null);
        }

        for (DetallePedidos detalle : pedido.getDetalles()) {
            if (detalle.getProducto() == null) continue;
            Long productoId = detalle.getProducto().getId();
            BigDecimal cantidad = detalle.getCantidad();
            try {
                inventarioService.confirmarReservaYSalidaConFallback(
                    negocioId, productoId, almacenPreferidoId, cantidad, usuarioId,
                    "Entrega pedido " + pedido.getNumeroPedido(),
                    pedido.getNumeroPedido()
                );
                System.out.println("✅ Salida confirmada — producto " + productoId + " x" + cantidad);
            } catch (Exception e) {
                System.err.println("⚠️ No se pudo confirmar salida para producto " + productoId + ": " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<Pedidos> buscarId(Long id) {
        Optional<Pedidos> pedido = repoPedidos.findById(id);
        // ✅ Inicializar TODAS las relaciones para detalle completo
        pedido.ifPresent(p -> {
            if (p.getCliente() != null) {
                p.getCliente().getNombres();
                p.getCliente().getApellidos();
                p.getCliente().getTelefono();
            }
            if (p.getNegocio() != null) {
                p.getNegocio().getRazonSocial();
            }
            if (p.getSede() != null) {
                p.getSede().getNombre();
            }
            if (p.getZonaDelivery() != null) {
                p.getZonaDelivery().getNombre();
            }
            if (p.getUsuario() != null) {
                p.getUsuario().getNombres();
            }
            if (p.getVenta() != null) {
                p.getVenta().getNumeroVenta();
            }
            // ✅ DETALLES (productos del pedido)
            if (p.getDetalles() != null && !p.getDetalles().isEmpty()) {
                for (DetallePedidos detalle : p.getDetalles()) {
                    // Inicializar producto de cada detalle
                    if (detalle.getProducto() != null) {
                        detalle.getProducto().getNombre();
                        detalle.getProducto().getSku();
                    }
                }
            }
        });
        return pedido;
    }

    public void eliminar(Long id) {
        repoPedidos.deleteById(id);
    }
}
