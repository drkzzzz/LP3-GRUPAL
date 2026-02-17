package DrinkGo.DrinkGo_backend.entity;

/**
 * Enum para Estado de Recepción de Compra
 */
public enum EstadoRecepcion {
    PENDIENTE,
    PARCIAL,
    COMPLETADA;

    public static EstadoRecepcion fromString(String value) {
        if (value == null) return PENDIENTE;
        try {
            return EstadoRecepcion.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDIENTE;
        }
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
