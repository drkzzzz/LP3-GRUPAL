package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO Response para UsuarioPlataforma (sin contraseña)
 */
public class UsuarioPlataformaResponse {

    private Long id;
    private String uuid;
    private String email;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String urlAvatar;
    private String rol;
    private Boolean estaActivo;
    private LocalDateTime ultimoAccesoEn;
    private String ipUltimoAcceso;
    private LocalDateTime contrasenaCambiadaEn;
    private Integer intentosFallidosAcceso;
    private LocalDateTime bloqueadoHasta;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // ── Getters y Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUrlAvatar() { return urlAvatar; }
    public void setUrlAvatar(String urlAvatar) { this.urlAvatar = urlAvatar; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getEstaActivo() { return estaActivo; }
    public void setEstaActivo(Boolean estaActivo) { this.estaActivo = estaActivo; }

    public LocalDateTime getUltimoAccesoEn() { return ultimoAccesoEn; }
    public void setUltimoAccesoEn(LocalDateTime ultimoAccesoEn) { this.ultimoAccesoEn = ultimoAccesoEn; }

    public String getIpUltimoAcceso() { return ipUltimoAcceso; }
    public void setIpUltimoAcceso(String ipUltimoAcceso) { this.ipUltimoAcceso = ipUltimoAcceso; }

    public LocalDateTime getContrasenaCambiadaEn() { return contrasenaCambiadaEn; }
    public void setContrasenaCambiadaEn(LocalDateTime contrasenaCambiadaEn) { this.contrasenaCambiadaEn = contrasenaCambiadaEn; }

    public Integer getIntentosFallidosAcceso() { return intentosFallidosAcceso; }
    public void setIntentosFallidosAcceso(Integer intentosFallidosAcceso) { this.intentosFallidosAcceso = intentosFallidosAcceso; }

    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
