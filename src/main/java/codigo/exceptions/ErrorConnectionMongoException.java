package codigo.exceptions;

public class ErrorConnectionMongoException extends Exception {

    private static final long serialVersionUID = 601128023082493198L;

    public ErrorConnectionMongoException(String mensaje) {
        super(mensaje);
    }

}
