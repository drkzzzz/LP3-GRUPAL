package DrinkGo.DrinkGo_backend.service.jpa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import DrinkGo.DrinkGo_backend.entity.FacturasSuscripcion;
import DrinkGo.DrinkGo_backend.entity.Suscripciones;
import DrinkGo.DrinkGo_backend.repository.FacturasSuscripcionRepository;
import DrinkGo.DrinkGo_backend.repository.SuscripcionesRepository;
import DrinkGo.DrinkGo_backend.service.IFacturasSuscripcionService;

@Service
public class FacturasSuscripcionService implements IFacturasSuscripcionService {
    @Autowired
    private FacturasSuscripcionRepository repoFacturasSuscripcion;

    @Autowired
    private SuscripcionesRepository repoSuscripciones;

    public List<FacturasSuscripcion> buscarTodos() {
        return repoFacturasSuscripcion.findAll();
    }

    public List<FacturasSuscripcion> buscarPorNegocio(Long negocioId) {
        return repoFacturasSuscripcion.findByNegocioId(negocioId);
    }

    public void guardar(FacturasSuscripcion facturasSuscripcion) {
        repoFacturasSuscripcion.save(facturasSuscripcion);
    }

    public void modificar(FacturasSuscripcion facturasSuscripcion) {
        repoFacturasSuscripcion.save(facturasSuscripcion);
    }

    public Optional<FacturasSuscripcion> buscarId(Long id) {
        return repoFacturasSuscripcion.findById(id);
    }

    public void eliminar(Long id) {
        repoFacturasSuscripcion.deleteById(id);
    }

    public FacturasSuscripcion generarFactura(Long suscripcionId) {
        Suscripciones suscripcion = repoSuscripciones.findById(suscripcionId)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada: " + suscripcionId));

        LocalDate hoy = LocalDate.now();

        // Número de factura: DG-YYYY-MM-NNNN (secuencia global por mes, con reintento
        // ante colisión)
        String numeroFactura;
        int intentos = 0;
        do {
            long secuencia = repoFacturasSuscripcion.countByYearAndMonth(hoy.getYear(), hoy.getMonthValue()) + 1
                    + intentos;
            numeroFactura = String.format("DG-%d-%02d-%04d", hoy.getYear(), hoy.getMonthValue(), secuencia);
            intentos++;
            if (intentos > 100)
                throw new RuntimeException("No se pudo generar un número de factura único");
        } while (repoFacturasSuscripcion.findByNumeroFactura(numeroFactura).isPresent());

        BigDecimal subtotal = suscripcion.getPlan().getPrecio() != null
                ? suscripcion.getPlan().getPrecio()
                : BigDecimal.ZERO;
        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        FacturasSuscripcion factura = new FacturasSuscripcion();
        factura.setSuscripcion(suscripcion);
        factura.setNegocio(suscripcion.getNegocio());
        factura.setNumeroFactura(numeroFactura);
        factura.setSubtotal(subtotal);
        factura.setMontoImpuesto(igv);
        factura.setMontoDescuento(BigDecimal.ZERO);
        factura.setTotal(total);
        factura.setMoneda("PEN");
        factura.setEstado(FacturasSuscripcion.EstadoFactura.pendiente);
        factura.setInicioPeriodo(hoy.withDayOfMonth(1));
        factura.setFinPeriodo(hoy.withDayOfMonth(hoy.lengthOfMonth()));
        factura.setFechaVencimiento(hoy.plusDays(5));
        factura.setEmitidoEn(LocalDateTime.now());

        repoFacturasSuscripcion.save(factura);
        return factura;
    }

    public FacturasSuscripcion marcarPagada(Long id, String metodoPago, String referenciaPago) {
        FacturasSuscripcion factura = repoFacturasSuscripcion.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id));
        factura.setEstado(FacturasSuscripcion.EstadoFactura.pagada);
        factura.setPagadoEn(LocalDateTime.now());
        if (metodoPago != null && !metodoPago.isBlank())
            factura.setMetodoPago(metodoPago);
        if (referenciaPago != null && !referenciaPago.isBlank())
            factura.setReferenciaPago(referenciaPago);
        repoFacturasSuscripcion.save(factura);

        // Extender automáticamente el período de suscripción al mes siguiente
        Suscripciones suscripcion = factura.getSuscripcion();
        if (suscripcion != null) {
            LocalDate base = factura.getFinPeriodo() != null
                    ? factura.getFinPeriodo()
                    : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            LocalDate nuevoInicio = base.plusDays(1);
            LocalDate nuevoFin = nuevoInicio.withDayOfMonth(nuevoInicio.lengthOfMonth());
            suscripcion.setInicioPeriodoActual(nuevoInicio);
            suscripcion.setFinPeriodoActual(nuevoFin);
            suscripcion.setEstado(Suscripciones.EstadoSuscripcion.activa);
            repoSuscripciones.save(suscripcion);
        }

        return factura;
    }

    public FacturasSuscripcion cancelarFactura(Long id) {
        FacturasSuscripcion factura = repoFacturasSuscripcion.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada: " + id));
        factura.setEstado(FacturasSuscripcion.EstadoFactura.anulada);
        repoFacturasSuscripcion.save(factura);
        return factura;
    }
}
