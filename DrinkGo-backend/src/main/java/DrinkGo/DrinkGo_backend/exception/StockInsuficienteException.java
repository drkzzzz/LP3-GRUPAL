package DrinkGo.DrinkGo_backend.exception;

/**
 * Excepción lanzada cuando se intenta una operación con stock insuficiente.
 * Retorna HTTP 400 BAD REQUEST.
 */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
