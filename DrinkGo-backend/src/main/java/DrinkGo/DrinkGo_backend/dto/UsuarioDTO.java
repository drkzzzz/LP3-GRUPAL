package DrinkGo.DrinkGo_backend.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UsuarioDTO {
    private String codigoEmpleado;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String contrasena;
    private Set<String> rolesCodigos;
    private Long sedePreferidaId;
}
