package DrinkGo.DrinkGo_backend.exception;

/**
 * Excepción lanzada para operaciones de negocio inválidas.
 * Retorna HTTP 400 BAD REQUEST.
 */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
