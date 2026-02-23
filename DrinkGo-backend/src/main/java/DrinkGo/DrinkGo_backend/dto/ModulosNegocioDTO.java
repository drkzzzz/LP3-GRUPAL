package DrinkGo.DrinkGo_backend.dto;

import java.time.LocalDateTime;

public class ModulosNegocioDTO {

    private Long id;
    private Long negocioId;
    private Long moduloSistemaId;
    private Boolean estaActivo;
    private LocalDateTime activadoEn;
    private LocalDateTime desactivadoEn;

    // Constructores
    public ModulosNegocioDTO() {
    }

    public ModulosNegocioDTO(Long id, Long negocioId, Long moduloSistemaId, Boolean estaActivo,
            LocalDateTime activadoEn, LocalDateTime desactivadoEn) {
        this.id = id;
        this.negocioId = negocioId;
        this.moduloSistemaId = moduloSistemaId;
        this.estaActivo = estaActivo;
        this.activadoEn = activadoEn;
        this.desactivadoEn = desactivadoEn;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getModuloSistemaId() {
        return moduloSistemaId;
    }

    public void setModuloSistemaId(Long moduloSistemaId) {
        this.moduloSistemaId = moduloSistemaId;
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
}
