package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.PaginaTiendaOnlineDTO;
import DrinkGo.DrinkGo_backend.entity.PaginaTiendaOnline;
import DrinkGo.DrinkGo_backend.repository.PaginaTiendaOnlineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaginaTiendaOnlineService {
    
    private final PaginaTiendaOnlineRepository paginaRepository;
    
    public PaginaTiendaOnlineService(PaginaTiendaOnlineRepository paginaRepository) {
        this.paginaRepository = paginaRepository;
    }
    
    /**
     * Listar todas las páginas de un negocio
     */
    @Transactional(readOnly = true)
    public List<PaginaTiendaOnline> listarPorNegocio(Long negocioId) {
        return paginaRepository.findByNegocioIdOrdenadas(negocioId);
    }
    
    /**
     * Listar solo páginas publicadas de un negocio
     */
    @Transactional(readOnly = true)
    public List<PaginaTiendaOnline> listarPublicadas(Long negocioId) {
        return paginaRepository.findByNegocioIdPublicadasOrdenadas(negocioId);
    }
    
    /**
     * Obtener página por ID
     */
    @Transactional(readOnly = true)
    public PaginaTiendaOnline obtenerPorId(Long id, Long negocioId) {
        return paginaRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException("Página no encontrada"));
    }
    
    /**
     * Obtener página por slug
     */
    @Transactional(readOnly = true)
    public PaginaTiendaOnline obtenerPorSlug(Long negocioId, String slug) {
        return paginaRepository.findByNegocioIdAndSlug(negocioId, slug)
                .orElseThrow(() -> new RuntimeException("Página no encontrada con slug: " + slug));
    }
    
    /**
     * Crear nueva página
     */
    @Transactional
    public PaginaTiendaOnline crear(PaginaTiendaOnlineDTO dto) {
        // Validar slug único por negocio
        if (paginaRepository.existsByNegocioIdAndSlug(dto.getNegocioId(), dto.getSlug())) {
            throw new RuntimeException("Ya existe una página con el slug: " + dto.getSlug());
        }
        
        PaginaTiendaOnline pagina = new PaginaTiendaOnline();
        mapearDtoAEntidad(dto, pagina);
        
        return paginaRepository.save(pagina);
    }
    
    /**
     * Actualizar página existente
     */
    @Transactional
    public PaginaTiendaOnline actualizar(Long id, PaginaTiendaOnlineDTO dto, Long negocioId) {
        PaginaTiendaOnline pagina = obtenerPorId(id, negocioId);
        
        // Validar slug único si cambió
        if (!pagina.getSlug().equals(dto.getSlug())) {
            if (paginaRepository.existsByNegocioIdAndSlug(negocioId, dto.getSlug())) {
                throw new RuntimeException("Ya existe una página con el slug: " + dto.getSlug());
            }
        }
        
        mapearDtoAEntidad(dto, pagina);
        
        return paginaRepository.save(pagina);
    }
    
    /**
     * Eliminar página
     */
    @Transactional
    public void eliminar(Long id, Long negocioId) {
        PaginaTiendaOnline pagina = obtenerPorId(id, negocioId);
        paginaRepository.delete(pagina);
    }
    
    /**
     * Publicar/Despublicar página
     */
    @Transactional
    public PaginaTiendaOnline cambiarEstadoPublicacion(Long id, Long negocioId, Boolean publicado) {
        PaginaTiendaOnline pagina = obtenerPorId(id, negocioId);
        pagina.setEstaPublicado(publicado);
        return paginaRepository.save(pagina);
    }
    
    /**
     * Actualizar orden de página
     */
    @Transactional
    public PaginaTiendaOnline actualizarOrden(Long id, Long negocioId, Integer nuevoOrden) {
        PaginaTiendaOnline pagina = obtenerPorId(id, negocioId);
        pagina.setOrden(nuevoOrden);
        return paginaRepository.save(pagina);
    }
    
    /**
     * Mapear DTO a entidad
     */
    private void mapearDtoAEntidad(PaginaTiendaOnlineDTO dto, PaginaTiendaOnline pagina) {
        if (dto.getNegocioId() != null) {
            pagina.setNegocioId(dto.getNegocioId());
        }
        if (dto.getTitulo() != null) {
            pagina.setTitulo(dto.getTitulo());
        }
        if (dto.getSlug() != null) {
            pagina.setSlug(dto.getSlug());
        }
        if (dto.getContenido() != null) {
            pagina.setContenido(dto.getContenido());
        }
        if (dto.getEstaPublicado() != null) {
            pagina.setEstaPublicado(dto.getEstaPublicado());
        }
        if (dto.getOrden() != null) {
            pagina.setOrden(dto.getOrden());
        }
        if (dto.getMetaTitulo() != null) {
            pagina.setMetaTitulo(dto.getMetaTitulo());
        }
        if (dto.getMetaDescripcion() != null) {
            pagina.setMetaDescripcion(dto.getMetaDescripcion());
        }
    }
}
