package DrinkGo.DrinkGo_backend.entity;

/**
 * Enum para Estado de Orden de Compra
 */
public enum EstadoOrdenCompra {
    BORRADOR,
    PENDIENTE_APROBACION,
    APROBADA,
    ENVIADA,
    RECEPCION_PARCIAL,
    RECIBIDA,
    CANCELADA;

    public static EstadoOrdenCompra fromString(String value) {
        if (value == null) return BORRADOR;
        try {
            return EstadoOrdenCompra.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BORRADOR;
        }
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
