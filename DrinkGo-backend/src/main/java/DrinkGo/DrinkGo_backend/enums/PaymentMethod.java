package DrinkGo.DrinkGo_backend.enums;

/**
 * Enum para método de pago
 * Tipo PostgreSQL: drinkgo.payment_method
 */
public enum PaymentMethod {
    efectivo,
    tarjeta_debito,
    tarjeta_credito,
    yape,
    plin,
    transferencia,
    otro
}
