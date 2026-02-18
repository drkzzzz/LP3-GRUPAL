package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.NegocioRequest;
import DrinkGo.DrinkGo_backend.dto.NegocioResponse;
import DrinkGo.DrinkGo_backend.entity.Negocio;
import DrinkGo.DrinkGo_backend.repository.NegocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para Negocio
 */
@Service
public class NegocioService {

    @Autowired
    private NegocioRepository negocioRepository;

    @Transactional(readOnly = true)
    public List<NegocioResponse> obtenerTodosLosNegocios() {
        return negocioRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NegocioResponse> obtenerNegociosActivos() {
        return negocioRepository.findByEstaActivoTrue().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NegocioResponse obtenerNegocioPorId(Long id) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + id));
        return convertirAResponse(negocio);
    }

    @Transactional(readOnly = true)
    public NegocioResponse obtenerNegocioPorUuid(String uuid) {
        Negocio negocio = negocioRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con UUID: " + uuid));
        return convertirAResponse(negocio);
    }

    @Transactional
    public NegocioResponse crearNegocio(NegocioRequest request) {
        // Validaciones
        if (request.getRuc() != null && negocioRepository.existsByRuc(request.getRuc())) {
            throw new RuntimeException("Ya existe un negocio con el RUC: " + request.getRuc());
        }
        if (negocioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un negocio con el email: " + request.getEmail());
        }

        Negocio negocio = new Negocio();
        mapearRequestAEntidad(request, negocio);
        Negocio guardado = negocioRepository.save(negocio);
        return convertirAResponse(guardado);
    }

    @Transactional
    public NegocioResponse actualizarNegocio(Long id, NegocioRequest request) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + id));

        // Validar RUC si cambió
        if (request.getRuc() != null && !request.getRuc().equals(negocio.getRuc())) {
            if (negocioRepository.existsByRuc(request.getRuc())) {
                throw new RuntimeException("Ya existe un negocio con el RUC: " + request.getRuc());
            }
        }

        mapearRequestAEntidad(request, negocio);
        Negocio actualizado = negocioRepository.save(negocio);
        return convertirAResponse(actualizado);
    }

    @Transactional
    public void eliminarNegocio(Long id) {
        if (!negocioRepository.existsById(id)) {
            throw new RuntimeException("Negocio no encontrado con ID: " + id);
        }
        negocioRepository.deleteById(id);
    }

    @Transactional
    public NegocioResponse cambiarEstadoNegocio(Long id, String nuevoEstado) {
        Negocio negocio = negocioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Negocio no encontrado con ID: " + id));

        negocio.setEstado(Negocio.EstadoNegocio.valueOf(nuevoEstado));
        Negocio actualizado = negocioRepository.save(negocio);
        return convertirAResponse(actualizado);
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(NegocioRequest request, Negocio negocio) {
        negocio.setRazonSocial(request.getRazonSocial());
        negocio.setNombreComercial(request.getNombreComercial());
        negocio.setRuc(request.getRuc());
        
        if (request.getTipoDocumentoFiscal() != null) {
            negocio.setTipoDocumentoFiscal(Negocio.TipoDocumentoFiscal.valueOf(request.getTipoDocumentoFiscal()));
        }
        
        negocio.setRepresentanteLegal(request.getRepresentanteLegal());
        negocio.setDocumentoRepresentante(request.getDocumentoRepresentante());
        negocio.setTipoNegocio(request.getTipoNegocio());
        negocio.setEmail(request.getEmail());
        negocio.setTelefono(request.getTelefono());
        negocio.setDireccion(request.getDireccion());
        negocio.setCiudad(request.getCiudad());
        negocio.setDepartamento(request.getDepartamento());
        negocio.setPais(request.getPais() != null ? request.getPais() : "PE");
        negocio.setCodigoPostal(request.getCodigoPostal());
        negocio.setUrlLogo(request.getUrlLogo());
        
        if (request.getEstado() != null) {
            negocio.setEstado(Negocio.EstadoNegocio.valueOf(request.getEstado()));
        }
        
        if (request.getEstaActivo() != null) {
            negocio.setEstaActivo(request.getEstaActivo());
        }
    }

    private NegocioResponse convertirAResponse(Negocio negocio) {
        NegocioResponse response = new NegocioResponse();
        response.setId(negocio.getId());
        response.setUuid(negocio.getUuid());
        response.setRazonSocial(negocio.getRazonSocial());
        response.setNombreComercial(negocio.getNombreComercial());
        response.setRuc(negocio.getRuc());
        response.setTipoDocumentoFiscal(negocio.getTipoDocumentoFiscal() != null ? negocio.getTipoDocumentoFiscal().name() : null);
        response.setRepresentanteLegal(negocio.getRepresentanteLegal());
        response.setDocumentoRepresentante(negocio.getDocumentoRepresentante());
        response.setTipoNegocio(negocio.getTipoNegocio());
        response.setEmail(negocio.getEmail());
        response.setTelefono(negocio.getTelefono());
        response.setDireccion(negocio.getDireccion());
        response.setCiudad(negocio.getCiudad());
        response.setDepartamento(negocio.getDepartamento());
        response.setPais(negocio.getPais());
        response.setCodigoPostal(negocio.getCodigoPostal());
        response.setUrlLogo(negocio.getUrlLogo());
        response.setEstado(negocio.getEstado() != null ? negocio.getEstado().name() : null);
        response.setEstaActivo(negocio.getEstaActivo());
        response.setCreadoEn(negocio.getCreadoEn());
        response.setActualizadoEn(negocio.getActualizadoEn());
        return response;
    }
}
