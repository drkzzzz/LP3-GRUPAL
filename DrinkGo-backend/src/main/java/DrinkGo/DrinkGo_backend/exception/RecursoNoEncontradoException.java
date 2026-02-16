package DrinkGo.DrinkGo_backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * Retorna HTTP 404 NOT FOUND.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String entidad, Long id) {
        super(entidad + " con ID " + id + " no fue encontrado(a)");
    }
}
