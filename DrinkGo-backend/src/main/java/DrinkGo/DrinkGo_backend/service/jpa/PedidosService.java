package DrinkGo.DrinkGo_backend.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import DrinkGo.DrinkGo_backend.entity.DetallePedidos;
import DrinkGo.DrinkGo_backend.entity.Pedidos;
import DrinkGo.DrinkGo_backend.repository.PedidosRepository;
import DrinkGo.DrinkGo_backend.service.IPedidosService;

@Service
public class PedidosService implements IPedidosService {
    @Autowired
    private PedidosRepository repoPedidos;

    @Transactional(readOnly = true)
    public List<Pedidos> buscarTodos() {
        List<Pedidos> pedidos = repoPedidos.findAll();
        // ✅ CRITICAL: Inicializar TODAS las relaciones LAZY para el flujo de pedidos
        for (Pedidos pedido : pedidos) {
            // Cliente (OBLIGATORIO - debe aparecer siempre)
            if (pedido.getCliente() != null) {
                pedido.getCliente().getNombres();
                pedido.getCliente().getApellidos();
                pedido.getCliente().getTelefono();
            }
            // Negocio y Sede
            if (pedido.getNegocio() != null) {
                pedido.getNegocio().getRazonSocial();
            }
            if (pedido.getSede() != null) {
                pedido.getSede().getNombre();
            }
            // Zona delivery (si aplica)
            if (pedido.getZonaDelivery() != null) {
                pedido.getZonaDelivery().getNombre();
            }
            // Usuario que procesó (si hay)
            if (pedido.getUsuario() != null) {
                pedido.getUsuario().getNombres();
            }
            // Venta asociada (si ya se generó)
            if (pedido.getVenta() != null) {
                pedido.getVenta().getNumeroVenta();
            }
            // ✅ DETALLES (productos del pedido)
            if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                for (DetallePedidos detalle : pedido.getDetalles()) {
                    // Inicializar producto de cada detalle
                    if (detalle.getProducto() != null) {
                        detalle.getProducto().getNombre();
                        detalle.getProducto().getSku();
                    }
                }
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
        repoPedidos.save(pedidos);
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
