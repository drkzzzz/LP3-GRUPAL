package DrinkGo.DrinkGo_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.ClienteCreateRequest;
import DrinkGo.DrinkGo_backend.dto.ClienteUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Cliente;
import DrinkGo.DrinkGo_backend.entity.Cliente.Genero;
import DrinkGo.DrinkGo_backend.entity.Cliente.TipoCliente;
import DrinkGo.DrinkGo_backend.entity.Cliente.TipoDocumento;
import DrinkGo.DrinkGo_backend.repository.ClienteRepository;

/**
 * Servicio de Clientes - Bloque 7.
 * Implementa RF-CLI-001 a RF-CLI-005:
 * - CRUD completo de clientes y direcciones
 * - Filtrado por negocio_id (multi-tenant)
 * - Borrado lógico (esta_activo = 0 vía @SQLDelete)
 * - UUID generado automáticamente al crear
 */
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // ============================================================
    // CLIENTES
    // ============================================================

    /**
     * Listar todos los clientes del negocio autenticado.
     */
    public List<Cliente> listarClientes(Long negocioId) {
        return clienteRepository.findByNegocioId(negocioId);
    }

    /**
     * Obtener un cliente por ID, validando que pertenezca al negocio.
     */
    public Cliente obtenerCliente(Long negocioId, Long id) {
        return clienteRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Cliente no encontrado con id " + id + " para el negocio actual"));
    }

    /**
     * Crear un nuevo cliente.
     */
    @Transactional
    public Cliente crearCliente(Long negocioId, ClienteCreateRequest request) {
        // Validar tipo de cliente
        TipoCliente tipoCliente = TipoCliente.individual;
        if (request.getTipoCliente() != null) {
            try {
                tipoCliente = TipoCliente.valueOf(request.getTipoCliente());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de cliente inválido: " + request.getTipoCliente()
                        + ". Valores permitidos: individual, empresa");
            }
        }

        // Validar campos según tipo
        if (tipoCliente == TipoCliente.individual) {
            if (request.getNombres() == null || request.getNombres().isBlank()) {
                throw new RuntimeException("Los nombres son obligatorios para clientes individuales");
            }
        } else {
            if (request.getRazonSocial() == null || request.getRazonSocial().isBlank()) {
                throw new RuntimeException("La razón social es obligatoria para clientes empresa");
            }
        }

        // Validar unicidad de documento dentro del negocio
        if (request.getTipoDocumento() != null && request.getNumeroDocumento() != null
                && !request.getNumeroDocumento().isBlank()) {
            TipoDocumento tipoDoc = parseTipoDocumento(request.getTipoDocumento());
            if (clienteRepository.existsByNegocioIdAndTipoDocumentoAndNumeroDocumento(
                    negocioId, tipoDoc, request.getNumeroDocumento())) {
                throw new RuntimeException("Ya existe un cliente con documento "
                        + request.getTipoDocumento() + " " + request.getNumeroDocumento()
                        + " en este negocio");
            }
        }

        Cliente cliente = new Cliente();
        cliente.setNegocioId(negocioId);
        cliente.setTipoCliente(tipoCliente);
        cliente.setNombres(request.getNombres());
        cliente.setApellidos(request.getApellidos());
        cliente.setRazonSocial(request.getRazonSocial());

        if (request.getTipoDocumento() != null) {
            cliente.setTipoDocumento(parseTipoDocumento(request.getTipoDocumento()));
        }
        cliente.setNumeroDocumento(request.getNumeroDocumento());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setTelefonoSecundario(request.getTelefonoSecundario());
        cliente.setFechaNacimiento(request.getFechaNacimiento());

        if (request.getGenero() != null) {
            cliente.setGenero(parseGenero(request.getGenero()));
        }

        if (request.getAceptaMarketing() != null) {
            cliente.setAceptaMarketing(request.getAceptaMarketing());
        }
        cliente.setCanalMarketing(request.getCanalMarketing());
        cliente.setNotas(request.getNotas());

        return clienteRepository.save(cliente);
    }

    /**
     * Actualizar un cliente existente.
     * Solo se actualizan los campos que vienen con valor (no null).
     */
    @Transactional
    public Cliente actualizarCliente(Long negocioId, Long id, ClienteUpdateRequest request) {
        Cliente cliente = obtenerCliente(negocioId, id);

        if (request.getTipoCliente() != null) {
            try {
                cliente.setTipoCliente(TipoCliente.valueOf(request.getTipoCliente()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de cliente inválido: " + request.getTipoCliente()
                        + ". Valores permitidos: individual, empresa");
            }
        }

        if (request.getNombres() != null) cliente.setNombres(request.getNombres());
        if (request.getApellidos() != null) cliente.setApellidos(request.getApellidos());
        if (request.getRazonSocial() != null) cliente.setRazonSocial(request.getRazonSocial());

        if (request.getTipoDocumento() != null) {
            cliente.setTipoDocumento(parseTipoDocumento(request.getTipoDocumento()));
        }
        if (request.getNumeroDocumento() != null) cliente.setNumeroDocumento(request.getNumeroDocumento());
        if (request.getEmail() != null) cliente.setEmail(request.getEmail());
        if (request.getTelefono() != null) cliente.setTelefono(request.getTelefono());
        if (request.getTelefonoSecundario() != null) cliente.setTelefonoSecundario(request.getTelefonoSecundario());
        if (request.getFechaNacimiento() != null) cliente.setFechaNacimiento(request.getFechaNacimiento());

        if (request.getGenero() != null) {
            cliente.setGenero(parseGenero(request.getGenero()));
        }

        if (request.getAceptaMarketing() != null) cliente.setAceptaMarketing(request.getAceptaMarketing());
        if (request.getCanalMarketing() != null) cliente.setCanalMarketing(request.getCanalMarketing());
        if (request.getNotas() != null) cliente.setNotas(request.getNotas());

        return clienteRepository.save(cliente);
    }

    /**
     * Eliminar cliente (borrado lógico vía @SQLDelete).
     */
    @Transactional
    public void eliminarCliente(Long negocioId, Long id) {
        Cliente cliente = obtenerCliente(negocioId, id);
        clienteRepository.delete(cliente);
    }

    // ============================================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ============================================================

    private TipoDocumento parseTipoDocumento(String valor) {
        try {
            return TipoDocumento.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de documento inválido: " + valor
                    + ". Valores permitidos: DNI, RUC, CE, PASAPORTE, OTRO");
        }
    }

    private Genero parseGenero(String valor) {
        try {
            return Genero.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Género inválido: " + valor
                    + ". Valores permitidos: M, F, OTRO, NO_ESPECIFICADO");
        }
    }
}