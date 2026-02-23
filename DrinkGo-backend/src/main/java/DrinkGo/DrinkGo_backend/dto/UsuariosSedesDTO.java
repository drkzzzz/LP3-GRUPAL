package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class UsuariosSedesDTO {

    private Long id;
    private Long usuarioId;
    private Long sedeId;
    private Boolean esSedePrincipal;
    private LocalDateTime creadoEn;

    // Constructores
    public UsuariosSedesDTO() {
    }

    public UsuariosSedesDTO(Long id, Long usuarioId, Long sedeId, Boolean esSedePrincipal, LocalDateTime creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.sedeId = sedeId;
        this.esSedePrincipal = esSedePrincipal;
        this.creadoEn = creadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSedeId() {
        return sedeId;
    }

    public void setSedeId(Long sedeId) {
        this.sedeId = sedeId;
    }

    public Boolean getEsSedePrincipal() {
        return esSedePrincipal;
    }

    public void setEsSedePrincipal(Boolean esSedePrincipal) {
        this.esSedePrincipal = esSedePrincipal;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
