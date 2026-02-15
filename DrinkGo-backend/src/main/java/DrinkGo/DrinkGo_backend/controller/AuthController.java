package DrinkGo.DrinkGo_backend.controller;

import DrinkGo.DrinkGo_backend.dto.LoginRequest;
import DrinkGo.DrinkGo_backend.dto.LoginResponse;
import DrinkGo.DrinkGo_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación.
 * Endpoint público para obtener el token JWT.
 * POST /restful/token
 */
@RestController
@RequestMapping("/restful")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login: genera token JWT.
     * El token contiene: sub=usuario_id, negocio_id, rol, iat, exp.
     * Se almacena hash SHA-256 en sesiones_usuario.
     */
    @PostMapping("/token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                                HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }
}
