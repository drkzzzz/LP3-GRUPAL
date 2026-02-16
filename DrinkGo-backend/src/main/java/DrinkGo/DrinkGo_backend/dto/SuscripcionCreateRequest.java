package DrinkGo.DrinkGo_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear una suscripción para un negocio.
 */
public class SuscripcionCreateRequest {

    private Long negocioId;
    private Long planId;
    private LocalDate inicioPeriodoActual;
    private LocalDate finPeriodoActual;
    private BigDecimal precioPeriodoActual;

    // --- Getters y Setters ---

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDate getInicioPeriodoActual() {
        return inicioPeriodoActual;
    }

    public void setInicioPeriodoActual(LocalDate inicioPeriodoActual) {
        this.inicioPeriodoActual = inicioPeriodoActual;
    }

    public LocalDate getFinPeriodoActual() {
        return finPeriodoActual;
    }

    public void setFinPeriodoActual(LocalDate finPeriodoActual) {
        this.finPeriodoActual = finPeriodoActual;
    }

    public BigDecimal getPrecioPeriodoActual() {
        return precioPeriodoActual;
    }

    public void setPrecioPeriodoActual(BigDecimal precioPeriodoActual) {
        this.precioPeriodoActual = precioPeriodoActual;
    }
}
