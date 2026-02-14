package DrinkGo.DrinkGo_backend.dto;

/**
 * DTO para actualizar un movimiento de inventario (PUT /restful/inventario/movimientos/{id}).
 * Solo campos editables (motivo, referencia). Cantidades y tipo no se modifican.
 */
public class MovimientoUpdateRequest {

    private String motivo;
    private String tipoReferencia;
    private Long referenciaId;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTipoReferencia() {
        return tipoReferencia;
    }

    public void setTipoReferencia(String tipoReferencia) {
        this.tipoReferencia = tipoReferencia;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }
}
