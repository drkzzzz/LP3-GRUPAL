package DrinkGo.DrinkGo_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import DrinkGo.DrinkGo_backend.dto.ClienteCreateRequest;
import DrinkGo.DrinkGo_backend.dto.ClienteUpdateRequest;
import DrinkGo.DrinkGo_backend.entity.Cliente;
import DrinkGo.DrinkGo_backend.service.ClienteService;
import DrinkGo.DrinkGo_backend.service.UsuarioService;

/**
 * Controlador de Clientes - Bloque 7.
 * Todos los endpoints requieren JWT.
 * CRUD completo: GET, POST, PUT, DELETE (borrado lógico).
 * Implementa RF-CLI-001 a RF-CLI-005.
 */
@RestController
@RequestMapping("/restful/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    // ============================================================
    // CRUD CLIENTES
    // ============================================================

    /**
     * GET /restful/clientes
     * Listar todos los clientes del negocio autenticado.
     */
    @GetMapping
    public ResponseEntity<?> listarClientes() {
        try {
            Long negocioId = obtenerNegocioId();
            List<Cliente> clientes = clienteService.listarClientes(negocioId);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * GET /restful/clientes/{id}
     * Obtener un cliente por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            Cliente cliente = clienteService.obtenerCliente(negocioId, id);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * POST /restful/clientes
     * Crear un nuevo cliente.
     */
    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody ClienteCreateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Cliente cliente = clienteService.crearCliente(negocioId, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Cliente creado exitosamente");
            response.put("cliente", cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * PUT /restful/clientes/{id}
     * Actualizar un cliente existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id,
                                                @RequestBody ClienteUpdateRequest request) {
        try {
            Long negocioId = obtenerNegocioId();
            Cliente cliente = clienteService.actualizarCliente(negocioId, id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Cliente actualizado exitosamente");
            response.put("cliente", cliente);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * DELETE /restful/clientes/{id}
     * Eliminar cliente (borrado lógico).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            Long negocioId = obtenerNegocioId();
            clienteService.eliminarCliente(negocioId, id);

            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cliente eliminado exitosamente (borrado lógico)");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    private Long obtenerNegocioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("No se pudo obtener la autenticación del contexto de seguridad");
        }
        String uuid = auth.getPrincipal().toString();
        return usuarioService.obtenerNegocioId(uuid);
    }

    private ResponseEntity<?> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}