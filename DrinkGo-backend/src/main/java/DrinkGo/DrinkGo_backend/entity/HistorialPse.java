package DrinkGo.DrinkGo_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Historial de comunicaciones con el PSE.
 * <p>
 * Registra cada intento de envío/reenvío de documentos a SUNAT,
 * incluyendo request, response, códigos y estado del resultado.
 * <p>
 * Tabla: historial_pse (creada por ddl-auto=update).
 */
@Entity
@Table(name = "historial_pse")
@Data
public class HistorialPse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id")
    private DocumentosFacturacion documento;

    /** Tipo: ENVIO, REENVIO, CONSULTA, ANULACION */
    @Column(name = "tipo_operacion", length = 30)
    private String tipoOperacion;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    /** XML o payload enviado al PSE */
    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    /** Respuesta del PSE/SUNAT */
    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    /** Código de respuesta SUNAT (0 = aceptado) */
    @Column(name = "codigo_respuesta", length = 20)
    private String codigoRespuesta;

    @Column(name = "mensaje_respuesta", length = 500)
    private String mensajeRespuesta;

    /** Si la operación fue exitosa */
    @Column(name = "exitoso")
    private Boolean exitoso;

    /** Número de intento (1, 2, 3...) */
    @Column(name = "intento_numero")
    private Integer intentoNumero;

    /** Nombre del proveedor PSE usado */
    @Column(name = "proveedor", length = 50)
    private String proveedor;

    /** Entorno: SANDBOX, PRODUCCION */
    @Column(name = "entorno", length = 20)
    private String entorno;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
    }
}
