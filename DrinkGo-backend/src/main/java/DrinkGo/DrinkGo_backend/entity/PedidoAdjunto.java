package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad PedidoAdjunto - Archivos adjuntos a pedidos
 * Tabla: drinkgo.pedido_adjunto
 */
@Entity
@Table(name = "pedido_adjunto", schema = "drinkgo")
public class PedidoAdjunto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;
    
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo; // comprobante_pago, documento_cliente, foto_entrega, otro
    
    @Column(name = "nombre_archivo", nullable = false, length = 200)
    private String nombreArchivo;
    
    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;
    
    @Column(name = "tipo_mime", length = 80)
    private String tipoMime;
    
    @Column(name = "tamano_bytes")
    private Long tamanoBytes;
    
    @Column(name = "subido_por_id")
    private Long subidoPorId;
    
    @Column(name = "subido_en", nullable = false, updatable = false)
    private OffsetDateTime subidoEn;
    
    @PrePersist
    protected void onCreate() {
        subidoEn = OffsetDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    
    public String getTipoMime() { return tipoMime; }
    public void setTipoMime(String tipoMime) { this.tipoMime = tipoMime; }
    
    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }
    
    public Long getSubidoPorId() { return subidoPorId; }
    public void setSubidoPorId(Long subidoPorId) { this.subidoPorId = subidoPorId; }
    
    public OffsetDateTime getSubidoEn() { return subidoEn; }
}
