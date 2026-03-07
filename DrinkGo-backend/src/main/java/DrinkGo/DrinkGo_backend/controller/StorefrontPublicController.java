package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Endpoints públicos del storefront (no requieren JWT).
 * Rutas habilitadas en SecurityConfig como permitAll:
 * GET /restful/tienda/public/{slug}
 * GET /restful/tienda/public/{slug}/sedes
 * GET /restful/tienda/public/{slug}/categorias
 * GET /restful/tienda/public/{slug}/productos
 * GET /restful/tienda/public/{slug}/productos/{productoId}
 * GET /restful/tienda/public/{slug}/metodos-pago
 * GET /restful/tienda/public/{slug}/zonas-delivery
 */
@RestController
@RequestMapping("/restful/tienda/public")
public class StorefrontPublicController {

    @Autowired
    private ConfiguracionTiendaOnlineRepository configRepo;

    @Autowired
    private SedesRepository sedesRepo;

    @Autowired
    private CategoriasRepository categoriasRepo;

    @Autowired
    private ProductosRepository productosRepo;

    @Autowired
    private MetodosPagoRepository metodosPagoRepo;

    @Autowired
    private ZonasDeliveryRepository zonasDeliveryRepo;

    /* ── Helper: buscar config o lanzar 404 ── */
    private ResponseEntity<Map<String, String>> notFound(String slug) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Tienda no encontrada: " + slug));
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/public/{slug}
     * Devuelve la configuración de la tienda
     * ══════════════════════════════════════════════
     */
    @GetMapping("/{slug}")
    public ResponseEntity<?> getConfigBySlug(@PathVariable String slug) {
        Optional<ConfiguracionTiendaOnline> config = configRepo.findBySlugTienda(slug);
        if (config.isEmpty()) {
            return notFound(slug);
        }
        return ResponseEntity.ok(config.get());
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/public/{slug}/sedes
     * ══════════════════════════════════════════════
     */
    @GetMapping("/{slug}/sedes")
    public ResponseEntity<?> getSedesBySlug(@PathVariable String slug) {
        Optional<ConfiguracionTiendaOnline> config = configRepo.findBySlugTienda(slug);
        if (config.isEmpty()) {
            return notFound(slug);
        }
        Long negocioId = config.get().getNegocio().getId();
        List<Sedes> sedes = sedesRepo.findByNegocioId(negocioId);
        return ResponseEntity.ok(sedes);
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/public/{slug}/categorias
     * ══════════════════════════════════════════════
     */
    @GetMapping("/{slug}/categorias")
    public ResponseEntity<?> getCategoriasBySlug(@PathVariable String slug) {
        Optional<ConfiguracionTiendaOnline> config = configRepo.findBySlugTienda(slug);
        if (config.isEmpty()) {
            return notFound(slug);
        }
        Long negocioId = config.get().getNegocio().getId();
        List<Categorias> categorias = categoriasRepo.findByNegocioId(negocioId)
                .stream()
                .filter(c -> Boolean.TRUE.equals(c.getVisibleTiendaOnline()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categorias);
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/public/{slug}/productos
     * Params opcionales: sedeId, categoriaId, search
     * ══════════════════════════════════════════════
     */
    @GetMapping("/{slug}/productos")
    public ResponseEntity<?> getProductosBySlug(
            @PathVariable String slug,
            @RequestParam(required = false) Long sedeId,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String search) {

        Optional<ConfiguracionTiendaOnline> config = configRepo.findBySlugTienda(slug);
        if (config.isEmpty()) {
            return notFound(slug);
        }

        Long negocioId = config.get().getNegocio().getId();

        List<Productos> productos;
        if (sedeId != null) {
            productos = productosRepo.findBySedeId(sedeId);
        } else {
            productos = productosRepo.findByNegocioId(negocioId);
        }

        if (categoriaId != null) {
            final Long catId = categoriaId;
            productos = productos.stream()
                    .filter(p -> p.getCategoria() != null && catId.equals(p.getCategoria().getId()))
                    .collect(Collectors.toList());
        }

        if (search != null && !search.isBlank()) {
            final String q = search.toLowerCase().trim();
            productos = productos.stream()
                    .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(q))
                            || (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(productos);
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/public/{slug}/productos/{productoId}
     * ══════════════════════════════════════════════
     */
    @GetMapping("/{slug}/productos/{productoId}")
    public ResponseEntity<?> getProductoById(
            @PathVariable String slug,
            @PathVariable Long productoId) {

        Optional<Productos> producto = productosRepo.findById(productoId);
        if (producto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Producto no encontrado"));
        }
        return ResponseEntity.ok(producto.get());
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/public/{slug}/metodos-pago
     * Solo devuelve los habilitados para tienda online
     * ══════════════════════════════════════════════
     */
    @GetMapping("/{slug}/metodos-pago")
    public ResponseEntity<?> getMetodosPagoBySlug(@PathVariable String slug) {
        Optional<ConfiguracionTiendaOnline> config = configRepo.findBySlugTienda(slug);
        if (config.isEmpty()) {
            return notFound(slug);
        }
        Long negocioId = config.get().getNegocio().getId();
        List<MetodosPago> metodos = metodosPagoRepo.findByNegocioId(negocioId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getDisponibleTiendaOnline())
                        && Boolean.TRUE.equals(m.getEstaActivo()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(metodos);
    }

    /*
     * ══════════════════════════════════════════════
     * GET /restful/tienda/public/{slug}/zonas-delivery
     * Param opcional: sedeId
     * ══════════════════════════════════════════════
     */
    @GetMapping("/{slug}/zonas-delivery")
    public ResponseEntity<?> getZonasDeliveryBySlug(
            @PathVariable String slug,
            @RequestParam(required = false) Long sedeId) {

        Optional<ConfiguracionTiendaOnline> config = configRepo.findBySlugTienda(slug);
        if (config.isEmpty()) {
            return notFound(slug);
        }
        Long negocioId = config.get().getNegocio().getId();

        List<ZonasDelivery> zonas;
        if (sedeId != null) {
            zonas = zonasDeliveryRepo.findBySedeId(sedeId);
        } else {
            zonas = zonasDeliveryRepo.findByNegocioId(negocioId);
        }
        return ResponseEntity.ok(zonas);
    }
}
