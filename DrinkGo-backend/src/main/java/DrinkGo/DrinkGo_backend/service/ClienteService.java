package DrinkGo.DrinkGo_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.ClienteCreateRequest;
import DrinkGo.DrinkGo_backend.dto.ClienteUpdateRequest;
import DrinkGo.DrinkGo_backend.dto.DireccionClienteCreateRequest;
import DrinkGo.DrinkGo_backend.dto.DireccionClienteUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Cliente;
import DrinkGo.DrinkGo_backend.entity.Cliente.Genero;
import DrinkGo.DrinkGo_backend.entity.Cliente.TipoCliente;
import DrinkGo.DrinkGo_backend.entity.Cliente.TipoDocumento;
import DrinkGo.DrinkGo_backend.entity.DireccionCliente;
import DrinkGo.DrinkGo_backend.repository.ClienteRepository;
import DrinkGo.DrinkGo_backend.repository.DireccionClienteRepository;

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

    @Autowired
    private DireccionClienteRepository direccionRepository;

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
    // DIRECCIONES DE CLIENTE
    // ============================================================

    /**
     * Listar direcciones de un cliente.
     */
    public List<DireccionCliente> listarDirecciones(Long negocioId, Long clienteId) {
        // Validar que el cliente pertenezca al negocio
        obtenerCliente(negocioId, clienteId);
        return direccionRepository.findByClienteId(clienteId);
    }

    /**
     * Obtener una dirección por ID.
     */
    public DireccionCliente obtenerDireccion(Long negocioId, Long clienteId, Long direccionId) {
        obtenerCliente(negocioId, clienteId);
        return direccionRepository.findByIdAndClienteId(direccionId, clienteId)
                .orElseThrow(() -> new RuntimeException(
                        "Dirección no encontrada con id " + direccionId + " para el cliente " + clienteId));
    }

    /**
     * Crear una dirección para un cliente.
     */
    @Transactional
    public DireccionCliente crearDireccion(Long negocioId, Long clienteId,
                                            DireccionClienteCreateRequest request) {
        obtenerCliente(negocioId, clienteId);

        if (request.getDireccion() == null || request.getDireccion().isBlank()) {
            throw new RuntimeException("La dirección es obligatoria");
        }

        DireccionCliente direccion = new DireccionCliente();
        direccion.setClienteId(clienteId);
        direccion.setEtiqueta(request.getEtiqueta());
        direccion.setDireccion(request.getDireccion());
        direccion.setDireccion2(request.getDireccion2());
        direccion.setCiudad(request.getCiudad());
        direccion.setDepartamento(request.getDepartamento());
        if (request.getPais() != null) direccion.setPais(request.getPais());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setLatitud(request.getLatitud());
        direccion.setLongitud(request.getLongitud());
        direccion.setReferencia(request.getReferencia());
        direccion.setTelefonoContacto(request.getTelefonoContacto());
        if (request.getEsPredeterminado() != null) {
            direccion.setEsPredeterminado(request.getEsPredeterminado());
        }

        return direccionRepository.save(direccion);
    }

    /**
     * Actualizar una dirección de un cliente.
     */
    @Transactional
    public DireccionCliente actualizarDireccion(Long negocioId, Long clienteId, Long direccionId,
                                                 DireccionClienteUpdateRequest request) {
        DireccionCliente direccion = obtenerDireccion(negocioId, clienteId, direccionId);

        if (request.getEtiqueta() != null) direccion.setEtiqueta(request.getEtiqueta());
        if (request.getDireccion() != null) direccion.setDireccion(request.getDireccion());
        if (request.getDireccion2() != null) direccion.setDireccion2(request.getDireccion2());
        if (request.getCiudad() != null) direccion.setCiudad(request.getCiudad());
        if (request.getDepartamento() != null) direccion.setDepartamento(request.getDepartamento());
        if (request.getPais() != null) direccion.setPais(request.getPais());
        if (request.getCodigoPostal() != null) direccion.setCodigoPostal(request.getCodigoPostal());
        if (request.getLatitud() != null) direccion.setLatitud(request.getLatitud());
        if (request.getLongitud() != null) direccion.setLongitud(request.getLongitud());
        if (request.getReferencia() != null) direccion.setReferencia(request.getReferencia());
        if (request.getTelefonoContacto() != null) direccion.setTelefonoContacto(request.getTelefonoContacto());
        if (request.getEsPredeterminado() != null) direccion.setEsPredeterminado(request.getEsPredeterminado());

        return direccionRepository.save(direccion);
    }

    /**
     * Eliminar dirección (borrado lógico vía @SQLDelete).
     */
    @Transactional
    public void eliminarDireccion(Long negocioId, Long clienteId, Long direccionId) {
        DireccionCliente direccion = obtenerDireccion(negocioId, clienteId, direccionId);
        direccionRepository.delete(direccion);
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