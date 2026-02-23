package DrinkGo.DrinkGo_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import DrinkGo.DrinkGo_backend.entity.Registros;
import DrinkGo.DrinkGo_backend.security.JwtUtil;
import DrinkGo.DrinkGo_backend.service.IRegistrosService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/restful")
public class RegistrosController {
    @Autowired
    private IRegistrosService serviceRegistros;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/registros")
    public List<Registros> BuscarTodos() {
        return serviceRegistros.BuscarTodos();
    }

    @PostMapping("/registros")
    public Registros guardar(@RequestBody Registros registros) {
        registros.setCliente_id(null);
        String claveOriginal = registros.getEmail() + registros.getNombres() + registros.getApellidos();
        registros.setLlave_secreta(claveOriginal);
        serviceRegistros.guardar(registros);
        return registros;
    }

    @PutMapping("/registros")
    public Registros modificar(@RequestBody Registros registros) {
        serviceRegistros.modificar(registros);
        return registros;
    }

    @GetMapping("/registros/{id}")
    public Optional<Registros> BuscarId(@PathVariable("id") Integer id) {
        return serviceRegistros.BuscarId(id);
    }

    @DeleteMapping("/registros/{id}")
    public String eliminar(@PathVariable Integer id) {
        serviceRegistros.eliminar(id);
        return "Registro eliminado";
    }

    @PostMapping("/token")
    public ResponseEntity<?> obtenerToken(@RequestBody Map<String, String> credenciales) {
        String clienteId = credenciales.get("cliente_id");
        String llaveSecreta = credenciales.get("llave_secreta");

        if (clienteId == null || llaveSecreta == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("cliente_id y llave_secreta son requeridos");
        }

        Optional<Registros> user = serviceRegistros.BuscarTodos().stream()
                .filter(r -> r.getCliente_id() != null && r.getCliente_id().equals(clienteId))
                .findFirst();

        if (user.isPresent() && passwordEncoder.matches(llaveSecreta, user.get()
                .getLlave_secreta())) {
            String token = jwtUtil.generarToken(clienteId);
            Registros registro = user.get();
            registro.setAccess_token(token);
            serviceRegistros.guardar(registro);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Credenciales inválidas");
    }
}
