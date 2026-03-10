package DrinkGo.DrinkGo_backend.service.jpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.entity.Almacenes;
import DrinkGo.DrinkGo_backend.entity.DetalleDevoluciones;
import DrinkGo.DrinkGo_backend.entity.Devoluciones;
import DrinkGo.DrinkGo_backend.entity.Devoluciones.EstadoDevolucion;
import DrinkGo.DrinkGo_backend.entity.LotesInventario;
import DrinkGo.DrinkGo_backend.entity.StockInventario;
import DrinkGo.DrinkGo_backend.entity.Usuarios;
import DrinkGo.DrinkGo_backend.entity.Ventas;
import DrinkGo.DrinkGo_backend.repository.AlmacenesRepository;
import DrinkGo.DrinkGo_backend.repository.DetalleDevolucionesRepository;
import DrinkGo.DrinkGo_backend.repository.DevolucionesRepository;
import DrinkGo.DrinkGo_backend.repository.LotesInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.StockInventarioRepository;
import DrinkGo.DrinkGo_backend.repository.UsuariosRepository;
import DrinkGo.DrinkGo_backend.repository.VentasRepository;
import DrinkGo.DrinkGo_backend.service.IDevolucionesService;

@Service
public class DevolucionesService implements IDevolucionesService {
    @Autowired
    private DevolucionesRepository repoDevoluciones;

    @Autowired
    private UsuariosRepository repoUsuarios;

    @Autowired
    private DetalleDevolucionesRepository repoDetalleDevoluciones;

    @Autowired
    private StockInventarioRepository repoStock;

    @Autowired
    private AlmacenesRepository repoAlmacenes;

    @Autowired
    private VentasRepository repoVentas;

    @Autowired
    private LotesInventarioRepository repoLotes;

    public List<Devoluciones> buscarTodos() {
        return repoDevoluciones.findAll();
    }

    public List<Devoluciones> buscarPorNegocio(Long negocioId) {
        return repoDevoluciones.findByNegocioIdOrderBySolicitadoEnDesc(negocioId);
    }

    public void guardar(Devoluciones devoluciones) {
        if (devoluciones.getNumeroDevolucion() == null || devoluciones.getNumeroDevolucion().isBlank()) {
            Long negocioId = devoluciones.getNegocio().getId();
            devoluciones.setNumeroDevolucion(generarNumeroDevolucion(negocioId));
        }
        repoDevoluciones.save(devoluciones);
    }

    public void modificar(Devoluciones devoluciones) {
        repoDevoluciones.save(devoluciones);
    }

    public Optional<Devoluciones> buscarId(Long id) {
        return repoDevoluciones.findById(id);
    }

    public void eliminar(Long id) {
        repoDevoluciones.deleteById(id);
    }

    @Transactional
    public Devoluciones aprobar(Long devolucionId, Long usuarioId) {
        Devoluciones dev = repoDevoluciones.findById(devolucionId)
                .orElseThrow(() -> new IllegalArgumentException("Devolución no encontrada con id: " + devolucionId));

        if (dev.getEstado() != EstadoDevolucion.solicitada) {
            throw new IllegalArgumentException("Solo se pueden aprobar devoluciones en estado 'solicitada'. Estado actual: " + dev.getEstado());
        }

        Usuarios usuario = repoUsuarios.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + usuarioId));

        dev.setEstado(EstadoDevolucion.aprobada);
        dev.setAprobadoPor(usuario);
        dev.setAprobadoEn(LocalDateTime.now());
        Devoluciones saved = repoDevoluciones.save(dev);

        if (dev.getVenta() != null) {
            // Devolución de cliente: restaurar stock y marcar venta como devuelta
            restaurarStock(dev);
            Ventas venta = dev.getVenta();
            venta.setEstado(Ventas.Estado.devuelta);
            repoVentas.save(venta);
        } else {
            // Devolución a proveedor: disminuir stock (se devuelve producto al proveedor)
            disminuirStock(dev);
        }

        return saved;
    }

    /**
     * Incrementa el stock de cada producto devuelto que tenga devolverStock = true.
     * Busca el almacén predeterminado de la sede de la devolución.
     */
    private void restaurarStock(Devoluciones dev) {
        List<DetalleDevoluciones> detalles = repoDetalleDevoluciones.findByDevolucionId(dev.getId());
        if (detalles.isEmpty()) return;

        Long sedeId = dev.getSede() != null ? dev.getSede().getId() : null;
        Long negocioId = dev.getNegocio() != null ? dev.getNegocio().getId() : null;

        // Buscar almacén predeterminado: primero por sede, luego por negocio
        Optional<Almacenes> almacenOpt = Optional.empty();
        if (sedeId != null) {
            almacenOpt = repoAlmacenes.findFirstBySede_IdAndEsPredeterminado(sedeId, true);
        }
        if (almacenOpt.isEmpty() && negocioId != null) {
            almacenOpt = repoAlmacenes.findFirstByNegocio_IdAndEsPredeterminado(negocioId, true);
        }
        if (almacenOpt.isEmpty()) return; // No hay almacén configurado

        Almacenes almacen = almacenOpt.get();

        for (DetalleDevoluciones detalle : detalles) {
            if (!Boolean.TRUE.equals(detalle.getDevolverStock())) continue;
            if (detalle.getProducto() == null) continue;

            Long productoId = detalle.getProducto().getId();
            BigDecimal cantidadDevuelta = BigDecimal.valueOf(detalle.getCantidad());

            Optional<StockInventario> stockOpt = repoStock.findFirstByProductoIdAndAlmacenId(productoId, almacen.getId());
            if (stockOpt.isPresent()) {
                StockInventario stock = stockOpt.get();
                stock.setCantidadActual(stock.getCantidadActual().add(cantidadDevuelta));
                BigDecimal reservada = stock.getCantidadReservada() != null ? stock.getCantidadReservada() : BigDecimal.ZERO;
                stock.setCantidadDisponible(stock.getCantidadActual().subtract(reservada));
                repoStock.save(stock);
            } else {
                // Crear registro de stock si no existe
                StockInventario nuevoStock = new StockInventario();
                nuevoStock.setProducto(detalle.getProducto());
                nuevoStock.setAlmacen(almacen);
                nuevoStock.setNegocio(dev.getNegocio());
                nuevoStock.setCantidadActual(cantidadDevuelta);
                nuevoStock.setCantidadReservada(BigDecimal.ZERO);
                nuevoStock.setCantidadDisponible(cantidadDevuelta);
                nuevoStock.setCostoPromedio(detalle.getPrecioUnitario() != null
                        ? detalle.getPrecioUnitario() : BigDecimal.ZERO);
                repoStock.save(nuevoStock);
            }
        }
    }

    /**
     * Disminuye el stock de cada producto devuelto al proveedor.
     * Busca el almacén predeterminado de la sede y reduce cantidades en stock y lote.
     */
    private void disminuirStock(Devoluciones dev) {
        List<DetalleDevoluciones> detalles = repoDetalleDevoluciones.findByDevolucionId(dev.getId());
        if (detalles.isEmpty()) return;

        Long sedeId = dev.getSede() != null ? dev.getSede().getId() : null;
        Long negocioId = dev.getNegocio() != null ? dev.getNegocio().getId() : null;

        Optional<Almacenes> almacenOpt = Optional.empty();
        if (sedeId != null) {
            almacenOpt = repoAlmacenes.findFirstBySede_IdAndEsPredeterminado(sedeId, true);
        }
        if (almacenOpt.isEmpty() && negocioId != null) {
            almacenOpt = repoAlmacenes.findFirstByNegocio_IdAndEsPredeterminado(negocioId, true);
        }
        if (almacenOpt.isEmpty()) return;

        Almacenes almacen = almacenOpt.get();

        for (DetalleDevoluciones detalle : detalles) {
            if (!Boolean.TRUE.equals(detalle.getDevolverStock())) continue;
            if (detalle.getProducto() == null) continue;

            Long productoId = detalle.getProducto().getId();
            BigDecimal cantidadDevuelta = BigDecimal.valueOf(detalle.getCantidad());

            // Disminuir stock del producto
            Optional<StockInventario> stockOpt = repoStock.findFirstByProductoIdAndAlmacenId(productoId, almacen.getId());
            if (stockOpt.isPresent()) {
                StockInventario stock = stockOpt.get();
                stock.setCantidadActual(stock.getCantidadActual().subtract(cantidadDevuelta));
                BigDecimal reservada = stock.getCantidadReservada() != null ? stock.getCantidadReservada() : BigDecimal.ZERO;
                stock.setCantidadDisponible(stock.getCantidadActual().subtract(reservada));
                repoStock.save(stock);
            }

            // Disminuir cantidad del lote si existe referencia
            if (detalle.getLote() != null) {
                Optional<LotesInventario> loteOpt = repoLotes.findById(detalle.getLote().getId());
                if (loteOpt.isPresent()) {
                    LotesInventario lote = loteOpt.get();
                    lote.setCantidadActual(lote.getCantidadActual().subtract(cantidadDevuelta));
                    repoLotes.save(lote);
                }
            }
        }
    }

    @Transactional
    public Devoluciones rechazar(Long devolucionId, Long usuarioId, String razon) {
        Devoluciones dev = repoDevoluciones.findById(devolucionId)
                .orElseThrow(() -> new IllegalArgumentException("Devolución no encontrada con id: " + devolucionId));

        if (dev.getEstado() != EstadoDevolucion.solicitada && dev.getEstado() != EstadoDevolucion.aprobada) {
            throw new IllegalArgumentException("Solo se pueden rechazar devoluciones en estado 'solicitada' o 'aprobada'. Estado actual: " + dev.getEstado());
        }

        Usuarios usuario = repoUsuarios.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + usuarioId));

        dev.setEstado(EstadoDevolucion.rechazada);
        dev.setProcesadoPor(usuario);
        dev.setRechazadoEn(LocalDateTime.now());
        dev.setRazonRechazo(razon);
        return repoDevoluciones.save(dev);
    }

    public String generarNumeroDevolucion(Long negocioId) {
        Long maxNum = repoDevoluciones.findMaxNumeroByNegocioId(negocioId);
        long next = (maxNum != null ? maxNum : 0) + 1;
        return String.format("DEV-%06d", next);
    }
}
