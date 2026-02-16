package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.ConfiguracionTiendaOnlineDTO;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionTiendaOnline;
import DrinkGo.DrinkGo_backend.service.ConfiguracionTiendaOnlineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST para gestión de Configuración de Tienda Online
 * BLOQUE 14: TIENDA ONLINE (STOREFRONT)
 */
@RestController
@RequestMapping("/api/tienda-online/configuracion")
public class ConfiguracionTiendaOnlineController {
    
    private final ConfiguracionTiendaOnlineService configuracionService;
    
    public ConfiguracionTiendaOnlineController(ConfiguracionTiendaOnlineService configuracionService) {
        this.configuracionService = configuracionService;
    }
    
    /**
     * GET /api/tienda-online/configuracion?negocioId={negocioId}
     * Obtener configuración de la tienda online por negocio
     */
    @GetMapping
    public ResponseEntity<ConfiguracionTiendaOnline> obtenerPorNegocio(@RequestParam Long negocioId) {
        try {
            ConfiguracionTiendaOnline configuracion = configuracionService.obtenerPorNegocio(negocioId);
            return ResponseEntity.ok(configuracion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    /**
     * GET /api/tienda-online/configuracion/slug/{slug}
     * Obtener configuración por slug de tienda (público)
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ConfiguracionTiendaOnline> obtenerPorSlug(@PathVariable String slug) {
        return configuracionService.obtenerPorSlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/tienda-online/configuracion
     * Crear configuración de tienda online
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ConfiguracionTiendaOnlineDTO dto) {
        try {
            ConfiguracionTiendaOnline configuracion = configuracionService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(configuracion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * PUT /api/tienda-online/configuracion/{id}?negocioId={negocioId}
     * Actualizar configuración de tienda online
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestParam Long negocioId,
            @RequestBody ConfiguracionTiendaOnlineDTO dto
    ) {
        try {
            ConfiguracionTiendaOnline configuracion = configuracionService.actualizar(id, dto, negocioId);
            return ResponseEntity.ok(configuracion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * PATCH /api/tienda-online/configuracion/estado?negocioId={negocioId}&habilitado={true/false}
     * Habilitar/Deshabilitar tienda online
     */
    @PatchMapping("/estado")
    public ResponseEntity<?> cambiarEstado(
            @RequestParam Long negocioId,
            @RequestParam Boolean habilitado
    ) {
        try {
            ConfiguracionTiendaOnline configuracion = configuracionService.cambiarEstado(negocioId, habilitado);
            return ResponseEntity.ok(configuracion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/tienda-online/configuracion/existe?negocioId={negocioId}
     * Verificar si existe configuración para un negocio
     */
    @GetMapping("/existe")
    public ResponseEntity<Map<String, Boolean>> existeConfiguracion(@RequestParam Long negocioId) {
        boolean existe = configuracionService.existeConfiguracion(negocioId);
        return ResponseEntity.ok(Map.of("existe", existe));
    }
}
