package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.PaginaTiendaOnlineDTO;
import DrinkGo.DrinkGo_backend.entity.PaginaTiendaOnline;
import DrinkGo.DrinkGo_backend.service.PaginaTiendaOnlineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de Páginas de Tienda Online
 * BLOQUE 14: TIENDA ONLINE (STOREFRONT)
 */
@RestController
@RequestMapping("/api/tienda-online/paginas")
public class PaginaTiendaOnlineController {

    private final PaginaTiendaOnlineService paginaService;

    public PaginaTiendaOnlineController(PaginaTiendaOnlineService paginaService) {
        this.paginaService = paginaService;
    }

    /**
     * GET /api/tienda-online/paginas?negocioId={negocioId}
     * Listar todas las páginas de un negocio (admin)
     */
    @GetMapping
    public ResponseEntity<List<PaginaTiendaOnline>> listarPorNegocio(@RequestParam Long negocioId) {
        List<PaginaTiendaOnline> paginas = paginaService.listarPorNegocio(negocioId);
        return ResponseEntity.ok(paginas);
    }

    /**
     * GET /api/tienda-online/paginas/publicadas?negocioId={negocioId}
     * Listar solo páginas publicadas (público)
     */
    @GetMapping("/publicadas")
    public ResponseEntity<List<PaginaTiendaOnline>> listarPublicadas(@RequestParam Long negocioId) {
        List<PaginaTiendaOnline> paginas = paginaService.listarPublicadas(negocioId);
        return ResponseEntity.ok(paginas);
    }

    /**
     * GET /api/tienda-online/paginas/{id}?negocioId={negocioId}
     * Obtener página por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(
            @PathVariable Long id,
            @RequestParam Long negocioId) {
        try {
            PaginaTiendaOnline pagina = paginaService.obtenerPorId(id, negocioId);
            return ResponseEntity.ok(pagina);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/tienda-online/paginas/slug/{slug}?negocioId={negocioId}
     * Obtener página por slug (público)
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> obtenerPorSlug(
            @PathVariable String slug,
            @RequestParam Long negocioId) {
        try {
            PaginaTiendaOnline pagina = paginaService.obtenerPorSlug(negocioId, slug);
            return ResponseEntity.ok(pagina);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/tienda-online/paginas
     * Crear nueva página
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PaginaTiendaOnlineDTO dto) {
        try {
            PaginaTiendaOnline pagina = paginaService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(pagina);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/tienda-online/paginas/{id}?negocioId={negocioId}
     * Actualizar página existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestParam Long negocioId,
            @RequestBody PaginaTiendaOnlineDTO dto) {
        try {
            PaginaTiendaOnline pagina = paginaService.actualizar(id, dto, negocioId);
            return ResponseEntity.ok(pagina);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/tienda-online/paginas/{id}?negocioId={negocioId}
     * Eliminar página
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id,
            @RequestParam Long negocioId) {
        try {
            paginaService.eliminar(id, negocioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH
     * /api/tienda-online/paginas/{id}/publicar?negocioId={negocioId}&publicado={true/false}
     * Publicar/Despublicar página
     */
    @PatchMapping("/{id}/publicar")
    public ResponseEntity<?> cambiarEstadoPublicacion(
            @PathVariable Long id,
            @RequestParam Long negocioId,
            @RequestParam Boolean publicado) {
        try {
            PaginaTiendaOnline pagina = paginaService.cambiarEstadoPublicacion(id, negocioId, publicado);
            return ResponseEntity.ok(pagina);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH
     * /api/tienda-online/paginas/{id}/orden?negocioId={negocioId}&orden={numero}
     * Actualizar orden de página
     */
    @PatchMapping("/{id}/orden")
    public ResponseEntity<?> actualizarOrden(
            @PathVariable Long id,
            @RequestParam Long negocioId,
            @RequestParam Integer orden) {
        try {
            PaginaTiendaOnline pagina = paginaService.actualizarOrden(id, negocioId, orden);
            return ResponseEntity.ok(pagina);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
