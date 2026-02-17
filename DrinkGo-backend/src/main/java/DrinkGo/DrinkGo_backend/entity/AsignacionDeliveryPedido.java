package DrinkGo.DrinkGo_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones_delivery_pedido")
public class AsignacionDeliveryPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "repartidor_id", nullable = false)
    private Long repartidorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoDelivery estado = EstadoDelivery.asignado;

    @Column(name = "asignado_en", nullable = false)
    private LocalDateTime asignadoEn;

    @Column(name = "aceptado_en")
    private LocalDateTime aceptadoEn;

    @Column(name = "recogido_en")
    private LocalDateTime recogidoEn;

    @Column(name = "entregado_en")
    private LocalDateTime entregadoEn;

    @Column(name = "notas", length = 300)
    private String notas;

    public enum EstadoDelivery {
        asignado,
        aceptado,
        recogido,
        en_transito,
        entregado,
        fallido
    }

    @PrePersist
    protected void onCreate() {
        if (this.asignadoEn == null) {
            this.asignadoEn = LocalDateTime.now();
        }
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getRepartidorId() {
        return repartidorId;
    }

    public void setRepartidorId(Long repartidorId) {
        this.repartidorId = repartidorId;
    }

    public EstadoDelivery getEstado() {
        return estado;
    }

    public void setEstado(EstadoDelivery estado) {
        this.estado = estado;
    }

    public LocalDateTime getAsignadoEn() {
        return asignadoEn;
    }

    public void setAsignadoEn(LocalDateTime asignadoEn) {
        this.asignadoEn = asignadoEn;
    }

    public LocalDateTime getAceptadoEn() {
        return aceptadoEn;
    }

    public void setAceptadoEn(LocalDateTime aceptadoEn) {
        this.aceptadoEn = aceptadoEn;
    }

    public LocalDateTime getRecogidoEn() {
        return recogidoEn;
    }

    public void setRecogidoEn(LocalDateTime recogidoEn) {
        this.recogidoEn = recogidoEn;
    }

    public LocalDateTime getEntregadoEn() {
        return entregadoEn;
    }

    public void setEntregadoEn(LocalDateTime entregadoEn) {
        this.entregadoEn = entregadoEn;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
