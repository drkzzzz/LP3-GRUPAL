package DrinkGo.DrinkGo_backend.enums;

/**
 * Enum para estado de pedido
 * Tipo PostgreSQL: drinkgo.order_status
 */
public enum OrderStatus {
    pendiente,
    en_preparacion,
    listo,
    entregado,
    anulado
}
