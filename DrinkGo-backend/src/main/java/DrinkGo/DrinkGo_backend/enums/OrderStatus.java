package DrinkGo.DrinkGo_backend.enums;

/**
 * Enum para estado de pedido
 * Corresponde al campo estado en tabla pedidos
 */
public enum OrderStatus {
    pendiente,
    en_preparacion,
    listo,
    entregado,
    anulado
}
