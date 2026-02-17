package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.dto.RecepcionCompraRequest;
import DrinkGo.DrinkGo_backend.dto.RecepcionCompraResponse;
import DrinkGo.DrinkGo_backend.entity.EstadoRecepcion;
import DrinkGo.DrinkGo_backend.entity.RecepcionCompra;
import DrinkGo.DrinkGo_backend.repository.RecepcionCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para RecepcionCompra
 */
@Service
public class RecepcionCompraService {

    @Autowired
    private RecepcionCompraRepository recepcionCompraRepository;

    @Transactional(readOnly = true)
    public List<RecepcionCompraResponse> obtenerTodasLasRecepciones() {
        return recepcionCompraRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionCompraResponse> obtenerRecepcionesPorNegocio(Long negocioId) {
        return recepcionCompraRepository.findByNegocioId(negocioId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionCompraResponse> obtenerRecepcionesPorOrden(Long ordenCompraId) {
        return recepcionCompraRepository.findByOrdenCompraId(ordenCompraId).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecepcionCompraResponse> obtenerRecepcionesPorFecha(Long negocioId, LocalDate fechaInicio, LocalDate fechaFin) {
        return recepcionCompraRepository.findByNegocioIdAndFechaRecepcionBetween(negocioId, fechaInicio, fechaFin).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecepcionCompraResponse obtenerRecepcionPorId(Long id) {
        RecepcionCompra recepcion = recepcionCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepción no encontrada con ID: " + id));
        return convertirAResponse(recepcion);
    }

    @Transactional(readOnly = true)
    public RecepcionCompraResponse obtenerRecepcionPorNumero(Long negocioId, String numeroRecepcion) {
        RecepcionCompra recepcion = recepcionCompraRepository.findByNegocioIdAndNumeroRecepcion(negocioId, numeroRecepcion)
                .orElseThrow(() -> new RuntimeException("Recepción no encontrada con número: " + numeroRecepcion));
        return convertirAResponse(recepcion);
    }

    @Transactional
    public RecepcionCompraResponse crearRecepcion(RecepcionCompraRequest request) {
        validarNumeroRecepcionUnico(request.getNegocioId(), request.getNumeroRecepcion(), null);

        RecepcionCompra recepcion = new RecepcionCompra();
        mapearRequestAEntidad(request, recepcion);
        RecepcionCompra guardada = recepcionCompraRepository.save(recepcion);
        return convertirAResponse(guardada);
    }

    @Transactional
    public RecepcionCompraResponse actualizarRecepcion(Long id, RecepcionCompraRequest request) {
        RecepcionCompra recepcion = recepcionCompraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recepción no encontrada con ID: " + id));

        validarNumeroRecepcionUnico(request.getNegocioId(), request.getNumeroRecepcion(), id);

        mapearRequestAEntidad(request, recepcion);
        RecepcionCompra actualizada = recepcionCompraRepository.save(recepcion);
        return convertirAResponse(actualizada);
    }

    @Transactional
    public void eliminarRecepcion(Long id) {
        if (!recepcionCompraRepository.existsById(id)) {
            throw new RuntimeException("Recepción no encontrada con ID: " + id);
        }
        recepcionCompraRepository.deleteById(id);
    }

    // ── Métodos de Validación ──

    private void validarNumeroRecepcionUnico(Long negocioId, String numeroRecepcion, Long idExcluir) {
        if (numeroRecepcion == null || numeroRecepcion.trim().isEmpty()) {
            throw new RuntimeException("El número de recepción no puede estar vacío");
        }

        recepcionCompraRepository.findByNegocioIdAndNumeroRecepcion(negocioId, numeroRecepcion)
                .ifPresent(recepcion -> {
                    if (idExcluir == null || !recepcion.getId().equals(idExcluir)) {
                        throw new RuntimeException("Ya existe una recepción con el número: " + numeroRecepcion);
                    }
                });
    }

    // ── Métodos de Conversión ──

    private void mapearRequestAEntidad(RecepcionCompraRequest request, RecepcionCompra recepcion) {
        recepcion.setNegocioId(request.getNegocioId());
        recepcion.setOrdenCompraId(request.getOrdenCompraId());
        recepcion.setNumeroRecepcion(request.getNumeroRecepcion());
        recepcion.setRecibidoPor(request.getRecibidoPor());
        recepcion.setFechaRecepcion(request.getFechaRecepcion() != null ? request.getFechaRecepcion() : LocalDate.now());
        recepcion.setNotas(request.getNotas());
        recepcion.setEstado(request.getEstado() != null ? EstadoRecepcion.fromString(request.getEstado()) : EstadoRecepcion.PENDIENTE);
    }

    private RecepcionCompraResponse convertirAResponse(RecepcionCompra recepcion) {
        RecepcionCompraResponse response = new RecepcionCompraResponse();
        response.setId(recepcion.getId());
        response.setNegocioId(recepcion.getNegocioId());
        response.setOrdenCompraId(recepcion.getOrdenCompraId());
        response.setNumeroRecepcion(recepcion.getNumeroRecepcion());
        response.setRecibidoPor(recepcion.getRecibidoPor());
        response.setFechaRecepcion(recepcion.getFechaRecepcion());
        response.setNotas(recepcion.getNotas());
        response.setEstado(recepcion.getEstado() != null ? recepcion.getEstado().toString() : null);
        response.setCreadoEn(recepcion.getCreadoEn());
        return response;
    }
}
