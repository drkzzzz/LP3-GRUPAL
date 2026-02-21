package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.ConfiguracionTiendaOnlineDTO;
import DrinkGo.DrinkGo_backend.entity.ConfiguracionTiendaOnline;
import DrinkGo.DrinkGo_backend.repository.ConfiguracionTiendaOnlineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ConfiguracionTiendaOnlineService {

    private final ConfiguracionTiendaOnlineRepository configuracionRepository;

    public ConfiguracionTiendaOnlineService(ConfiguracionTiendaOnlineRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    /**
     * Obtener configuración de tienda online por negocio
     */
    @Transactional(readOnly = true)
    public ConfiguracionTiendaOnline obtenerPorNegocio(Long negocioId) {
        return configuracionRepository.findByNegocioId(negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Configuración de tienda online no encontrada para el negocio: " + negocioId));
    }

    /**
     * Obtener configuración por slug de tienda
     */
    @Transactional(readOnly = true)
    public Optional<ConfiguracionTiendaOnline> obtenerPorSlug(String slugTienda) {
        return configuracionRepository.findBySlugTienda(slugTienda);
    }

    /**
     * Crear configuración de tienda online
     */
    @Transactional
    public ConfiguracionTiendaOnline crear(ConfiguracionTiendaOnlineDTO dto) {
        // Validar que no exista configuración para este negocio
        if (configuracionRepository.existsByNegocioId(dto.getNegocioId())) {
            throw new RuntimeException("Ya existe una configuración de tienda online para este negocio");
        }

        // Validar slug único si se proporciona
        if (dto.getSlugTienda() != null && !dto.getSlugTienda().isEmpty()) {
            if (configuracionRepository.existsBySlugTienda(dto.getSlugTienda())) {
                throw new RuntimeException("El slug de tienda ya está en uso: " + dto.getSlugTienda());
            }
        }

        ConfiguracionTiendaOnline configuracion = new ConfiguracionTiendaOnline();
        mapearDtoAEntidad(dto, configuracion);

        return configuracionRepository.save(configuracion);
    }

    /**
     * Actualizar configuración de tienda online
     */
    @Transactional
    public ConfiguracionTiendaOnline actualizar(Long id, ConfiguracionTiendaOnlineDTO dto, Long negocioId) {
        ConfiguracionTiendaOnline configuracion = configuracionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración de tienda online no encontrada"));

        // Verificar que la configuración pertenece al negocio
        if (!configuracion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("La configuración no pertenece al negocio especificado");
        }

        // Validar slug único si cambió
        if (dto.getSlugTienda() != null && !dto.getSlugTienda().equals(configuracion.getSlugTienda())) {
            if (configuracionRepository.existsBySlugTienda(dto.getSlugTienda())) {
                throw new RuntimeException("El slug de tienda ya está en uso: " + dto.getSlugTienda());
            }
        }

        mapearDtoAEntidad(dto, configuracion);

        return configuracionRepository.save(configuracion);
    }

    /**
     * Habilitar/Deshabilitar tienda online
     */
    @Transactional
    public ConfiguracionTiendaOnline cambiarEstado(Long negocioId, Boolean habilitado) {
        ConfiguracionTiendaOnline configuracion = obtenerPorNegocio(negocioId);
        configuracion.setEstaHabilitado(habilitado);
        return configuracionRepository.save(configuracion);
    }

    /**
     * Verificar si existe configuración para un negocio
     */
    @Transactional(readOnly = true)
    public boolean existeConfiguracion(Long negocioId) {
        return configuracionRepository.existsByNegocioId(negocioId);
    }
    
    /**
     * Obtener configuración por ID
     */
    @Transactional(readOnly = true)
    public ConfiguracionTiendaOnline obtenerPorId(Long id) {
        return configuracionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración de tienda online no encontrada"));
    }
    
    /**
     * Eliminar configuración de tienda online
     */
    @Transactional
    public void eliminar(Long id, Long negocioId) {
        ConfiguracionTiendaOnline configuracion = configuracionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración de tienda online no encontrada"));
        
        // Verificar que la configuración pertenece al negocio
        if (!configuracion.getNegocioId().equals(negocioId)) {
            throw new RuntimeException("La configuración no pertenece al negocio especificado");
        }
        
        configuracionRepository.delete(configuracion);
    }

    /**
     * Mapear DTO a entidad
     */
    private void mapearDtoAEntidad(ConfiguracionTiendaOnlineDTO dto, ConfiguracionTiendaOnline configuracion) {
        if (dto.getNegocioId() != null) {
            configuracion.setNegocioId(dto.getNegocioId());
        }
        if (dto.getEstaHabilitado() != null) {
            configuracion.setEstaHabilitado(dto.getEstaHabilitado());
        }
        if (dto.getNombreTienda() != null) {
            configuracion.setNombreTienda(dto.getNombreTienda());
        }
        if (dto.getSlugTienda() != null) {
            configuracion.setSlugTienda(dto.getSlugTienda());
        }
        if (dto.getDominioPersonalizado() != null) {
            configuracion.setDominioPersonalizado(dto.getDominioPersonalizado());
        }
        if (dto.getMensajeBienvenida() != null) {
            configuracion.setMensajeBienvenida(dto.getMensajeBienvenida());
        }
        if (dto.getImagenesBanner() != null) {
            configuracion.setImagenesBanner(dto.getImagenesBanner());
        }
        if (dto.getCategoriasDestacadas() != null) {
            configuracion.setCategoriasDestacadas(dto.getCategoriasDestacadas());
        }
        if (dto.getTituloSeo() != null) {
            configuracion.setTituloSeo(dto.getTituloSeo());
        }
        if (dto.getDescripcionSeo() != null) {
            configuracion.setDescripcionSeo(dto.getDescripcionSeo());
        }
        if (dto.getPalabrasClaveSeo() != null) {
            configuracion.setPalabrasClaveSeo(dto.getPalabrasClaveSeo());
        }
        if (dto.getIdGoogleAnalytics() != null) {
            configuracion.setIdGoogleAnalytics(dto.getIdGoogleAnalytics());
        }
        if (dto.getIdPixelFacebook() != null) {
            configuracion.setIdPixelFacebook(dto.getIdPixelFacebook());
        }
        if (dto.getEnlacesSociales() != null) {
            configuracion.setEnlacesSociales(dto.getEnlacesSociales());
        }
        if (dto.getMontoMinimoPedido() != null) {
            configuracion.setMontoMinimoPedido(dto.getMontoMinimoPedido());
        }
        if (dto.getMontoMaximoPedido() != null) {
            configuracion.setMontoMaximoPedido(dto.getMontoMaximoPedido());
        }
        if (dto.getTerminosCondiciones() != null) {
            configuracion.setTerminosCondiciones(dto.getTerminosCondiciones());
        }
        if (dto.getPoliticaPrivacidad() != null) {
            configuracion.setPoliticaPrivacidad(dto.getPoliticaPrivacidad());
        }
        if (dto.getPoliticaDevoluciones() != null) {
            configuracion.setPoliticaDevoluciones(dto.getPoliticaDevoluciones());
        }
        if (dto.getMostrarPreciosConImpuesto() != null) {
            configuracion.setMostrarPreciosConImpuesto(dto.getMostrarPreciosConImpuesto());
        }
        if (dto.getPermitirCompraInvitado() != null) {
            configuracion.setPermitirCompraInvitado(dto.getPermitirCompraInvitado());
        }
        if (dto.getRequiereVerificacionEdad() != null) {
            configuracion.setRequiereVerificacionEdad(dto.getRequiereVerificacionEdad());
        }
    }
}
