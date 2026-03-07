package DrinkGo.DrinkGo_backend.service.jpa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.entity.CuentasMesa;
import DrinkGo.DrinkGo_backend.entity.CuentasMesa.EstadoCuenta;
import DrinkGo.DrinkGo_backend.entity.DetalleCuentaMesa;
import DrinkGo.DrinkGo_backend.entity.Mesas;
import DrinkGo.DrinkGo_backend.entity.Mesas.EstadoMesa;
import DrinkGo.DrinkGo_backend.entity.Productos;
import DrinkGo.DrinkGo_backend.repository.CuentasMesaRepository;
import DrinkGo.DrinkGo_backend.repository.DetalleCuentaMesaRepository;
import DrinkGo.DrinkGo_backend.repository.MesasRepository;
import DrinkGo.DrinkGo_backend.repository.ProductosRepository;
import DrinkGo.DrinkGo_backend.service.ICuentasMesaService;

@Service
public class CuentasMesaService implements ICuentasMesaService {

    @Autowired
    private CuentasMesaRepository repoCuentas;

    @Autowired
    private DetalleCuentaMesaRepository repoDetalles;

    @Autowired
    private MesasRepository repoMesas;

    @Autowired
    private ProductosRepository repoProductos;

    // ── RF-VTA-011: Abrir cuenta ───────────────────────────────────────

    @Override
    @Transactional
    public CuentasMesa abrirCuenta(CuentasMesa cuenta) {
        Mesas mesa = repoMesas.findById(cuenta.getMesa().getId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        if (mesa.getEstado() == EstadoMesa.ocupada) {
            throw new RuntimeException("La mesa ya tiene una cuenta abierta");
        }

        // Generar número correlativo: CM-XXXX
        long numero = repoCuentas.nextNumero(cuenta.getNegocioId() != null
                ? cuenta.getNegocioId()
                : mesa.getSede().getNegocio().getId());
        cuenta.setNumeroCuenta(String.format("CM-%04d", numero));

        // Marcar mesa como ocupada
        mesa.setEstado(EstadoMesa.ocupada);
        repoMesas.save(mesa);

        cuenta.setEstado(EstadoCuenta.abierta);
        repoCuentas.save(cuenta);
        return cuenta;
    }

    // ── Consultas ─────────────────────────────────────────────────────

    @Override
    public List<CuentasMesa> buscarAbiertasPorSede(Long sedeId) {
        return repoCuentas.findBySedeIdAndEstado(sedeId, EstadoCuenta.abierta);
    }

    @Override
    public Optional<CuentasMesa> buscarPorId(Long id) {
        return repoCuentas.findById(id);
    }

    // ── RF-VTA-012: Gestión de productos en la cuenta ─────────────────

    @Override
    @Transactional
    public DetalleCuentaMesa agregarProducto(Long cuentaId, DetalleCuentaMesa detalle) {
        CuentasMesa cuenta = repoCuentas.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (cuenta.getEstado() != EstadoCuenta.abierta) {
            throw new RuntimeException("Solo se pueden agregar productos a cuentas abiertas");
        }

        // Capturar snapshot del nombre del producto y reemplazar objeto transient por
        // entidad gestionada
        Long productoIdRef = detalle.getProducto() != null && detalle.getProducto().getId() != null
                ? detalle.getProducto().getId()
                : detalle.getProductoId();
        if (productoIdRef == null) {
            detalle.setProducto(null); // limpiar posible POJO transient sin ID (combo)
        }
        if (productoIdRef != null) {
            repoProductos.findById(productoIdRef).ifPresent(p -> {
                detalle.setProducto(p); // reemplazar el POJO transient de Jackson por la entidad gestionada
                if (detalle.getNombreProductoSnapshot() == null) {
                    detalle.setNombreProductoSnapshot(p.getNombre());
                }
                if (detalle.getPrecioUnitario() == null
                        || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) == 0) {
                    detalle.setPrecioUnitario(p.getPrecioVenta() != null ? p.getPrecioVenta() : BigDecimal.ZERO);
                }
            });
        }

        // Calcular subtotal / total del ítem
        BigDecimal subtotal = detalle.getPrecioUnitario().multiply(detalle.getCantidad())
                .setScale(2, RoundingMode.HALF_UP);
        detalle.setSubtotal(subtotal);
        detalle.setTotal(subtotal);
        detalle.setCuenta(cuenta);

        repoDetalles.save(detalle);

        // Recalcular totales de la cuenta
        recalcularTotales(cuenta);
        repoCuentas.save(cuenta);

        return detalle;
    }

    @Override
    @Transactional
    public void removerProducto(Long detalleId) {
        DetalleCuentaMesa detalle = repoDetalles.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("Ítem no encontrado"));

        CuentasMesa cuenta = detalle.getCuenta();
        repoDetalles.deleteById(detalleId); // triggers @SQLDelete soft-delete

        recalcularTotales(cuenta);
        repoCuentas.save(cuenta);
    }

    @Override
    public List<DetalleCuentaMesa> obtenerDetalles(Long cuentaId) {
        return repoDetalles.findByCuentaId(cuentaId);
    }

    // ── RF-VTA-013: Transferir productos / mesa ───────────────────────

    @Override
    @Transactional
    public void transferirProductos(Long cuentaOrigenId, Long cuentaDestinoId, List<Long> detalleIds) {
        CuentasMesa origen = repoCuentas.findById(cuentaOrigenId)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
        CuentasMesa destino = repoCuentas.findById(cuentaDestinoId)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (origen.getEstado() != EstadoCuenta.abierta || destino.getEstado() != EstadoCuenta.abierta) {
            throw new RuntimeException("Ambas cuentas deben estar abiertas para transferir productos");
        }

        for (Long detalleId : detalleIds) {
            DetalleCuentaMesa detalle = repoDetalles.findById(detalleId)
                    .orElseThrow(() -> new RuntimeException("Ítem " + detalleId + " no encontrado"));
            detalle.setCuenta(destino);
            repoDetalles.save(detalle);
        }

        recalcularTotales(origen);
        recalcularTotales(destino);
        repoCuentas.save(origen);
        repoCuentas.save(destino);
    }

    @Override
    @Transactional
    public CuentasMesa transferirMesa(Long cuentaId, Long nuevaMesaId) {
        CuentasMesa cuenta = repoCuentas.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Mesas mesaActual = cuenta.getMesa();
        Mesas nuevaMesa = repoMesas.findById(nuevaMesaId)
                .orElseThrow(() -> new RuntimeException("Mesa destino no encontrada"));

        if (nuevaMesa.getEstado() == EstadoMesa.ocupada) {
            throw new RuntimeException("La mesa destino ya está ocupada");
        }

        // Liberar mesa actual
        mesaActual.setEstado(EstadoMesa.disponible);
        repoMesas.save(mesaActual);

        // Ocupar nueva mesa
        nuevaMesa.setEstado(EstadoMesa.ocupada);
        repoMesas.save(nuevaMesa);

        cuenta.setMesa(nuevaMesa);
        cuenta.setEstado(EstadoCuenta.abierta);
        repoCuentas.save(cuenta);
        return cuenta;
    }

    // ── RF-VTA-014: Pre-cuenta / Cerrar cuenta ────────────────────────

    @Override
    public List<Map<String, Object>> dividirPorPersonas(Long cuentaId, int personas) {
        if (personas < 1)
            throw new RuntimeException("El número de personas debe ser al menos 1");

        List<DetalleCuentaMesa> detalles = repoDetalles.findByCuentaId(cuentaId);
        CuentasMesa cuenta = repoCuentas.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        BigDecimal totalPorPersona = cuenta.getTotal()
                .divide(BigDecimal.valueOf(personas), 2, RoundingMode.HALF_UP);

        List<Map<String, Object>> divisiones = new ArrayList<>();
        for (int i = 1; i <= personas; i++) {
            Map<String, Object> division = new LinkedHashMap<>();
            division.put("persona", i);
            division.put("total", totalPorPersona);
            division.put("items", detalles); // cada persona ve todos los ítems (monto dividido)
            divisiones.add(division);
        }
        return divisiones;
    }

    @Override
    @Transactional
    public CuentasMesa cerrarCuenta(Long cuentaId, Long usuarioId) {
        CuentasMesa cuenta = repoCuentas.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (cuenta.getEstado() == EstadoCuenta.cerrada) {
            throw new RuntimeException("La cuenta ya está cerrada");
        }

        // Liberar la mesa
        Mesas mesa = cuenta.getMesa();
        mesa.setEstado(EstadoMesa.disponible);
        repoMesas.save(mesa);

        // Cerrar la cuenta
        cuenta.setEstado(EstadoCuenta.cerrada);
        cuenta.setCerradoEn(LocalDateTime.now());
        repoCuentas.save(cuenta);
        return cuenta;
    }

    // ── Utilidades privadas ───────────────────────────────────────────

    private void recalcularTotales(CuentasMesa cuenta) {
        List<DetalleCuentaMesa> items = repoDetalles.findByCuentaId(cuenta.getId());
        BigDecimal subtotal = items.stream()
                .map(DetalleCuentaMesa::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cuenta.setSubtotal(subtotal);
        cuenta.setTotal(subtotal);
    }
}
