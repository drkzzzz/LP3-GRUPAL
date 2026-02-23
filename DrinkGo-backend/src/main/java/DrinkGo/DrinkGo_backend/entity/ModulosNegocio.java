package DrinkGo.DrinkGo_backend.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "modulos_negocio")
@SQLDelete(sql = "UPDATE modulos_negocio SET esta_activo = 0, desactivado_en = NOW() WHERE id = ?")
@SQLRestriction("esta_activo = 1")
@JsonPropertyOrder({ "id", "negocioId", "moduloId", "estaActivo", "activadoEn", "desactivadoEn" })
public class ModulosNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negocio_id", nullable = false)
    private Negocios negocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id", nullable = false)
    private ModulosSistema modulo;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "activado_en")
    private LocalDateTime activadoEn;

    @Column(name = "desactivado_en")
    private LocalDateTime desactivadoEn;

    @PrePersist
    protected void onCreate() {
        activadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Negocios getNegocio() {
        return negocio;
    }

    public void setNegocio(Negocios negocio) {
        this.negocio = negocio;
    }

    public ModulosSistema getModulo() {
        return modulo;
    }

    public void setModulo(ModulosSistema modulo) {
        this.modulo = modulo;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getActivadoEn() {
        return activadoEn;
    }

    public void setActivadoEn(LocalDateTime activadoEn) {
        this.activadoEn = activadoEn;
    }

    public LocalDateTime getDesactivadoEn() {
        return desactivadoEn;
    }

    public void setDesactivadoEn(LocalDateTime desactivadoEn) {
        this.desactivadoEn = desactivadoEn;
    }

    @Override
    public String toString() {
        return "ModulosNegocio [id=" + id + ", negocio=" + (negocio != null ? negocio.getId() : null) + ", modulo="
                + (modulo != null ? modulo.getId() : null) + ", estaActivo=" + estaActivo + ", activadoEn=" + activadoEn
                + ", desactivadoEn=" + desactivadoEn + "]";
    }
}
