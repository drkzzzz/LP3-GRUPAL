package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Usuario - Mapea la tabla 'usuarios' de drinkgo_database.sql.
 * Reemplaza la entidad Registro del patrón de clase.
 *
 * Equivalencias con CLASE_API_REFERENCIA.md:
 *   cliente_id    → uuid (identificador único del usuario/tenant)
 *   llave_secreta → hash_contrasena (BCrypt)
 *   access_token  → almacenado en sesiones_usuario.hash_token
 *
 * Borrado lógico con @SQLDelete y @SQLRestriction (RF-ADM-011..013).
 */
@Entity
@Table(name = "usuarios")
@SQLDelete(sql = "UPDATE usuarios SET eliminado_en = NOW() WHERE id = ?")
@SQLRestriction("eliminado_en IS NULL")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(name = "negocio_id", nullable = false)
    private Long negocioId;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "hash_contrasena", nullable = false)
    private String hashContrasena;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "url_avatar", length = 500)
    private String urlAvatar;

    @Column(name = "esta_activo", nullable = false)
    private Boolean estaActivo = true;

    @Column(name = "email_verificado_en")
    private LocalDateTime emailVerificadoEn;

    @Column(name = "ultimo_acceso_en")
    private LocalDateTime ultimoAccesoEn;

    @Column(name = "ip_ultimo_acceso", length = 45)
    private String ipUltimoAcceso;

    @Column(name = "contrasena_cambiada_en")
    private LocalDateTime contrasenaCambiadaEn;

    @Column(name = "intentos_fallidos_acceso", nullable = false)
    private Integer intentosFallidosAcceso = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @Column(name = "debe_cambiar_contrasena", nullable = false)
    private Boolean debeCambiarContrasena = false;

    @Column(name = "idioma", nullable = false, length = 5)
    private String idioma = "es";

    @Column(name = "formato_fecha_preferido", length = 20)
    private String formatoFechaPreferido;

    @Column(name = "zona_horaria_preferida", length = 50)
    private String zonaHorariaPreferida;

    @Column(name = "notificaciones_habilitadas", nullable = false)
    private Boolean notificacionesHabilitadas = true;

    @Column(name = "preferencias_notificacion_json", columnDefinition = "JSON")
    private String preferenciasNotificacionJson;

    @Column(name = "vista_pos_predeterminada", nullable = false, length = 20)
    private String vistaPosPredeterminada = "cuadricula";

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    @Column(name = "eliminado_en")
    private LocalDateTime eliminadoEn;

    // --- Generación de UUID (equivalente al cliente_id de la clase) ---
    @PrePersist
    protected void onCreate() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    /**
     * Encriptar contraseña con BCrypt (equivalente a setLlave_secreta de la clase).
     */
    public void setHashContrasena(String contrasenaPlana) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.hashContrasena = encoder.encode(contrasenaPlana);
    }

    /**
     * Setter directo para hashContrasena sin encriptar (para carga desde BD).
     */
    public void setHashContrasenaDirecto(String hashContrasena) {
        this.hashContrasena = hashContrasena;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getEmailVerificadoEn() {
        return emailVerificadoEn;
    }

    public void setEmailVerificadoEn(LocalDateTime emailVerificadoEn) {
        this.emailVerificadoEn = emailVerificadoEn;
    }

    public LocalDateTime getUltimoAccesoEn() {
        return ultimoAccesoEn;
    }

    public void setUltimoAccesoEn(LocalDateTime ultimoAccesoEn) {
        this.ultimoAccesoEn = ultimoAccesoEn;
    }

    public String getIpUltimoAcceso() {
        return ipUltimoAcceso;
    }

    public void setIpUltimoAcceso(String ipUltimoAcceso) {
        this.ipUltimoAcceso = ipUltimoAcceso;
    }

    public LocalDateTime getContrasenaCambiadaEn() {
        return contrasenaCambiadaEn;
    }

    public void setContrasenaCambiadaEn(LocalDateTime contrasenaCambiadaEn) {
        this.contrasenaCambiadaEn = contrasenaCambiadaEn;
    }

    public Integer getIntentosFallidosAcceso() {
        return intentosFallidosAcceso;
    }

    public void setIntentosFallidosAcceso(Integer intentosFallidosAcceso) {
        this.intentosFallidosAcceso = intentosFallidosAcceso;
    }

    public LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    public void setBloqueadoHasta(LocalDateTime bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }

    public Boolean getDebeCambiarContrasena() {
        return debeCambiarContrasena;
    }

    public void setDebeCambiarContrasena(Boolean debeCambiarContrasena) {
        this.debeCambiarContrasena = debeCambiarContrasena;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getFormatoFechaPreferido() {
        return formatoFechaPreferido;
    }

    public void setFormatoFechaPreferido(String formatoFechaPreferido) {
        this.formatoFechaPreferido = formatoFechaPreferido;
    }

    public String getZonaHorariaPreferida() {
        return zonaHorariaPreferida;
    }

    public void setZonaHorariaPreferida(String zonaHorariaPreferida) {
        this.zonaHorariaPreferida = zonaHorariaPreferida;
    }

    public Boolean getNotificacionesHabilitadas() {
        return notificacionesHabilitadas;
    }

    public void setNotificacionesHabilitadas(Boolean notificacionesHabilitadas) {
        this.notificacionesHabilitadas = notificacionesHabilitadas;
    }

    public String getPreferenciasNotificacionJson() {
        return preferenciasNotificacionJson;
    }

    public void setPreferenciasNotificacionJson(String preferenciasNotificacionJson) {
        this.preferenciasNotificacionJson = preferenciasNotificacionJson;
    }

    public String getVistaPosPredeterminada() {
        return vistaPosPredeterminada;
    }

    public void setVistaPosPredeterminada(String vistaPosPredeterminada) {
        this.vistaPosPredeterminada = vistaPosPredeterminada;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public LocalDateTime getEliminadoEn() {
        return eliminadoEn;
    }

    public void setEliminadoEn(LocalDateTime eliminadoEn) {
        this.eliminadoEn = eliminadoEn;
    }
}
