package DrinkGo.DrinkGo_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Configuración del Proveedor de Servicios Electrónicos (PSE) por negocio.
 * <p>
 * Almacena las credenciales y configuración de conexión con el PSE
 * seleccionado (Nubefact, Efact, Bizlinks, Simulador, etc.).
 * <p>
 * Tabla: configuracion_pse (creada por ddl-auto=update).
 */
@Entity
@Table(name = "configuracion_pse")
@Data
public class ConfiguracionPse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    /** Nombre del proveedor: SIMULADOR, NUBEFACT, EFACT, BIZLINKS */
    @Column(name = "proveedor", length = 50)
    private String proveedor = "SIMULADOR";

    /** Entorno: SANDBOX, PRODUCCION */
    @Column(name = "entorno", length = 20)
    private String entorno = "SANDBOX";

    /** Token o API key del PSE */
    @Column(name = "api_token", length = 500)
    private String apiToken;

    /** URL base del servicio PSE */
    @Column(name = "url_servicio", length = 500)
    private String urlServicio;

    /** RUC del emisor para el PSE */
    @Column(name = "ruc_emisor", length = 11)
    private String rucEmisor;

    /** Indica si la conexión PSE está activa */
    @Column(name = "esta_activo")
    private Boolean estaActivo = false;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}
