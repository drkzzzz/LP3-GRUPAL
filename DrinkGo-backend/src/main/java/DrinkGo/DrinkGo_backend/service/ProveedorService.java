package DrinkGo.DrinkGo_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DrinkGo.DrinkGo_backend.dto.ProveedorCreateRequest;
import DrinkGo.DrinkGo_backend.dto.ProveedorUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Proveedor;
import DrinkGo.DrinkGo_backend.repository.ProveedorRepository;

/**
 * Servicio de Proveedores - Bloque 6.
 * Implementa RF-COM-001 a RF-COM-003:
 * - CRUD completo de proveedores
 * - Filtrado por negocio_id (multi-tenant)
 * - Borrado lógico (esta_activo = 0 vía @SQLDelete)
 */
@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    /**
     * Listar todos los proveedores del negocio autenticado.
     */
    public List<Proveedor> listarProveedores(Long negocioId) {
        return proveedorRepository.findByNegocioId(negocioId);
    }

    /**
     * Obtener un proveedor por ID, validando que pertenezca al negocio.
     */
    public Proveedor obtenerProveedor(Long negocioId, Long id) {
        return proveedorRepository.findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() -> new RuntimeException(
                        "Proveedor no encontrado con id " + id + " para el negocio actual"));
    }

    /**
     * Crear un nuevo proveedor.
     */
    @Transactional
    public Proveedor crearProveedor(Long negocioId, ProveedorCreateRequest request) {
        // Validar campos obligatorios
        if (request.getCodigo() == null || request.getCodigo().isBlank()) {
            throw new RuntimeException("El código del proveedor es obligatorio");
        }
        if (request.getRazonSocial() == null || request.getRazonSocial().isBlank()) {
            throw new RuntimeException("La razón social del proveedor es obligatoria");
        }

        // Validar unicidad del código dentro del negocio
        if (proveedorRepository.existsByNegocioIdAndCodigo(negocioId, request.getCodigo())) {
            throw new RuntimeException("Ya existe un proveedor con código '" + request.getCodigo() + "' en este negocio");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNegocioId(negocioId);
        proveedor.setCodigo(request.getCodigo());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setNombreComercial(request.getNombreComercial());
        proveedor.setRuc(request.getRuc());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        proveedor.setRubro(request.getRubro());

        return proveedorRepository.save(proveedor);
    }

    /**
     * Actualizar un proveedor existente.
     * Solo se actualizan los campos que vienen con valor (no null).
     */
    @Transactional
    public Proveedor actualizarProveedor(Long negocioId, Long id, ProveedorUpdateRequest request) {
        Proveedor proveedor = obtenerProveedor(negocioId, id);

        if (request.getRazonSocial() != null) proveedor.setRazonSocial(request.getRazonSocial());
        if (request.getNombreComercial() != null) proveedor.setNombreComercial(request.getNombreComercial());
        if (request.getRuc() != null) proveedor.setRuc(request.getRuc());
        if (request.getDireccion() != null) proveedor.setDireccion(request.getDireccion());
        if (request.getTelefono() != null) proveedor.setTelefono(request.getTelefono());
        if (request.getEmail() != null) proveedor.setEmail(request.getEmail());
        if (request.getRubro() != null) proveedor.setRubro(request.getRubro());

        return proveedorRepository.save(proveedor);
    }

    /**
     * Eliminar proveedor (borrado lógico vía @SQLDelete).
     */
    @Transactional
    public void eliminarProveedor(Long negocioId, Long id) {
        Proveedor proveedor = obtenerProveedor(negocioId, id);
        proveedorRepository.delete(proveedor);
    }
}
